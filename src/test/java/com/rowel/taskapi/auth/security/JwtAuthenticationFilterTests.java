package com.rowel.taskapi.auth.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.rowel.taskapi.auth.exception.UserNotFoundException;
import com.rowel.taskapi.auth.model.TaskUser;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

class JwtAuthenticationFilterTests {
  private final JwtService jwtService = mock(JwtService.class);
  private final TaskUserDetailsService userDetailsService = mock(TaskUserDetailsService.class);
  private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService);
  private final MockHttpServletRequest request = new MockHttpServletRequest();
  private final MockHttpServletResponse response = new MockHttpServletResponse();
  private final FilterChain chain = mock(FilterChain.class);

  @AfterEach
  void clearContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void missingTokenContinuesWithoutAuthentication() throws Exception {
    filter.doFilter(request, response, chain);
    verify(chain).doFilter(request, response);
    verifyNoInteractions(jwtService, userDetailsService);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void invalidTokenReturnsUnauthorizedWithoutContinuing() throws Exception {
    request.setCookies(new Cookie("access_token", "invalid"));
    when(jwtService.extractUserId("invalid")).thenThrow(new JwtException("Invalid"));
    filter.doFilter(request, response, chain);
    assertEquals(401, response.getStatus());
    assertNull(response.getHeader("WWW-Authenticate"));
    verifyNoInteractions(userDetailsService, chain);
  }

  @Test
  void deletedUserReturnsUnauthorized() throws Exception {
    request.setCookies(new Cookie("access_token", "valid"));
    when(jwtService.extractUserId("valid")).thenReturn(5L);
    when(userDetailsService.loadByUserId(5L)).thenThrow(new UserNotFoundException());
    filter.doFilter(request, response, chain);
    assertEquals(401, response.getStatus());
    verifyNoInteractions(chain);
  }

  @Test
  void validTokenSetsAuthenticatedPrincipalWithDatabaseId() throws Exception {
    request.setCookies(new Cookie("access_token", "valid"));
    TaskUser user = TaskUser.builder().id(5L).username("rowel").build();
    when(jwtService.extractUserId("valid")).thenReturn(5L);
    when(userDetailsService.loadByUserId(5L)).thenReturn(new TaskUserDetails(user));
    filter.doFilter(request, response, chain);
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(authentication);
    assertTrue(authentication.isAuthenticated());
    assertEquals(5L, ((TaskUserDetails) authentication.getPrincipal()).getUserId());
    assertNull(authentication.getCredentials());
    verify(chain).doFilter(request, response);
  }

  @Test
  void unrelatedCookieContinuesWithoutAuthentication() throws Exception {
    request.setCookies(new Cookie("XSRF-TOKEN", "csrf"));
    filter.doFilter(request, response, chain);
    verify(chain).doFilter(request, response);
    verifyNoInteractions(jwtService, userDetailsService);
  }

  @Test
  void blankTokenContinuesWithoutAuthentication() throws Exception {
    request.setCookies(new Cookie("access_token", ""));
    filter.doFilter(request, response, chain);
    verify(chain).doFilter(request, response);
    verifyNoInteractions(jwtService, userDetailsService);
  }

  @Test
  void logoutCanProceedWithAnExpiredCookie() throws Exception {
    request.setMethod("POST");
    request.setRequestURI("/api/auth/logout");
    request.setCookies(new Cookie("access_token", "expired"));
    filter.doFilter(request, response, chain);
    verify(chain).doFilter(request, response);
    verifyNoInteractions(jwtService, userDetailsService);
  }

  @Test
  void loginCanProceedWithAnExpiredCookie() throws Exception {
    request.setMethod("POST");
    request.setRequestURI("/api/auth/login");
    request.setCookies(new Cookie("access_token", "expired"));
    filter.doFilter(request, response, chain);
    verify(chain).doFilter(request, response);
    verifyNoInteractions(jwtService, userDetailsService);
  }
}
