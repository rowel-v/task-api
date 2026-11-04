package com.rowel.taskapi.auth.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.rowel.taskapi.auth.dto.response.LoginResponse;
import com.rowel.taskapi.auth.security.JwtService;
import com.rowel.taskapi.auth.service.AuthService;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AuthControllerTests {
  private final AuthService auth = mock(AuthService.class);
  private MockMvc mvc;

  @BeforeEach
  void setup() {
    mvc = MockMvcBuilders.standaloneSetup(new AuthController(auth)).build();
  }

  @Test
  void loginSetsAnHttpOnlyCookieWithoutReturningTheTokenInJson() throws Exception {
    JwtService jwt = mock(JwtService.class);
    when(jwt.getExpiration()).thenReturn(Duration.ofMinutes(15));
    when(auth.getJwtService()).thenReturn(jwt);
    when(auth.loginAccount(any())).thenReturn(new LoginResponse("test-token"));

    mvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"username\":\"rowel\",\"password\":\"password\"}"))
      .andExpect(status().isOk())
      .andExpect(cookie().value("access_token", "test-token"))
      .andExpect(cookie().httpOnly("access_token", true))
      .andExpect(cookie().secure("access_token", true))
      .andExpect(cookie().path("access_token", "/"))
      .andExpect(cookie().maxAge("access_token", 900))
      .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("SameSite=Lax")))
      .andExpect(header().string(HttpHeaders.CACHE_CONTROL, "no-store"))
      .andExpect(content().string(not(containsString("test-token"))));
  }

  @Test
  void logoutExpiresTheMatchingCookie() throws Exception {
    mvc.perform(post("/api/auth/logout"))
      .andExpect(status().isOk())
      .andExpect(cookie().value("access_token", ""))
      .andExpect(cookie().httpOnly("access_token", true))
      .andExpect(cookie().secure("access_token", true))
      .andExpect(cookie().path("access_token", "/"))
      .andExpect(cookie().maxAge("access_token", 0))
      .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("SameSite=Lax")))
      .andExpect(header().string(HttpHeaders.CACHE_CONTROL, "no-store"))
      .andExpect(jsonPath("$.message").value("Logged out successfully"));
  }
}
