package com.rowel.taskapi.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.rowel.taskapi.model.TaskUser;
import com.rowel.taskapi.repository.TaskUserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

class JwtAuthenticationFilterTests {
  private final JwtService jwtService = mock(JwtService.class);
  private final TaskUserRepository repository = mock(TaskUserRepository.class);
  private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, repository);
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
    verifyNoInteractions(jwtService, repository);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void invalidTokenReturnsUnauthorizedWithoutContinuing() throws Exception {
    request.addHeader("Authorization", "Bearer invalid");
    when(jwtService.extractUserId("invalid")).thenThrow(new JwtException("Invalid"));
    filter.doFilter(request, response, chain);
    assertEquals(401, response.getStatus());
    assertEquals("Bearer error=\"invalid_token\"", response.getHeader("WWW-Authenticate"));
    verifyNoInteractions(repository, chain);
  }

  @Test
  void deletedUserReturnsUnauthorized() throws Exception {
    request.addHeader("Authorization", "Bearer valid");
    when(jwtService.extractUserId("valid")).thenReturn(5L);
    when(repository.findById(5L)).thenReturn(Optional.empty());
    filter.doFilter(request, response, chain);
    assertEquals(401, response.getStatus());
    verifyNoInteractions(chain);
  }

  @Test
  void validTokenSetsAuthenticatedPrincipalWithDatabaseId() throws Exception {
    request.addHeader("Authorization", "Bearer valid");
    TaskUser user = mock(TaskUser.class);
    when(user.getId()).thenReturn(5L);
    when(user.getUsername()).thenReturn("rowel");
    when(jwtService.extractUserId("valid")).thenReturn(5L);
    when(repository.findById(5L)).thenReturn(Optional.of(user));
    filter.doFilter(request, response, chain);
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(authentication);
    assertTrue(authentication.isAuthenticated());
    assertEquals(5L, ((TaskUserDetails) authentication.getPrincipal()).getUserId());
    assertNull(authentication.getCredentials());
    verify(chain).doFilter(request, response);
  }
}
