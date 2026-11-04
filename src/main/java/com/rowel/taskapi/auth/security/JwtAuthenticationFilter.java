package com.rowel.taskapi.auth.security;

import com.rowel.taskapi.auth.exception.UserNotFoundException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  public JwtAuthenticationFilter(
    JwtService jwtService,
    TaskUserDetailsService userDetailsService
  ) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  private final JwtService jwtService;
  private final TaskUserDetailsService userDetailsService;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI().substring(request.getContextPath().length());
    String method = request.getMethod();

    return ("GET".equals(method) && "/api/auth/csrf".equals(path)) ||
      ("POST".equals(method) && (
        "/api/auth/login".equals(path) ||
        "/api/auth/register".equals(path) ||
        "/api/auth/logout".equals(path)
      ));
  }

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {

    String token = null;
    Cookie[] cookies = request.getCookies();

    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("access_token".equals(cookie.getName())) {
          token = cookie.getValue();
          break;
        }
      }
    }

    if (token == null || token.isBlank()) {
      filterChain.doFilter(request, response);
      return;
    }

    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      Long userId;
      try {
        // Parsing also verifies the signature, issuer and expiration.
        userId = jwtService.extractUserId(token);
      } catch (JwtException | IllegalArgumentException _) {
        rejectToken(response);
        return;
      }

      TaskUserDetails user;
      try {
        user = (TaskUserDetails) userDetailsService.loadByUserId(userId);
      } catch (UserNotFoundException ex) {
        rejectToken(response);
        return;
      }

      UsernamePasswordAuthenticationToken authentication =
        UsernamePasswordAuthenticationToken.authenticated(
          user,
          null,
          user.getAuthorities()
        );

      authentication.setDetails(
        new WebAuthenticationDetailsSource().buildDetails(request)
      );

      SecurityContext context = SecurityContextHolder.createEmptyContext();
      context.setAuthentication(authentication);
      SecurityContextHolder.setContext(context);
    }

    // Keep downstream exceptions outside the token-validation catch block.
    filterChain.doFilter(request, response);
  }

  private void rejectToken(HttpServletResponse response) {
    SecurityContextHolder.clearContext();
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }
}
