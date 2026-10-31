package com.rowel.taskapi.auth.security;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.rowel.taskapi.auth.exception.UserNotFoundException;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {
    String header = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (header == null || !header.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      Long userId;
      try {
        // Parsing also verifies the signature, issuer and expiration.
        userId = jwtService.extractUserId(header.substring(7).trim());
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
    response.setHeader(
      HttpHeaders.WWW_AUTHENTICATE,
      "Bearer error=\"invalid_token\""
    );
  }
}
