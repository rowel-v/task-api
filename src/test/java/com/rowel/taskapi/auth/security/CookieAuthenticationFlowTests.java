package com.rowel.taskapi.auth.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.rowel.taskapi.auth.controller.AuthController;
import com.rowel.taskapi.auth.dto.response.LoginResponse;
import com.rowel.taskapi.auth.model.TaskUser;
import com.rowel.taskapi.auth.service.AuthService;
import jakarta.servlet.Filter;
import jakarta.servlet.http.Cookie;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringJUnitWebConfig(CookieAuthenticationFlowTests.TestConfig.class)
class CookieAuthenticationFlowTests {
  @Autowired private WebApplicationContext context;
  @Autowired private AuthService auth;
  @Autowired private JwtService jwt;
  @Autowired private TaskUserDetailsService users;
  private MockMvc mvc;

  @Configuration
  @EnableWebMvc
  @EnableWebSecurity
  @Import(SecurityConfig.class)
  static class TestConfig {
    @Bean AuthService authService() { return mock(AuthService.class); }
    @Bean JwtService jwtService() { return mock(JwtService.class); }
    @Bean TaskUserDetailsService userDetailsService() { return mock(TaskUserDetailsService.class); }
    @Bean JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwt, TaskUserDetailsService users) {
      return new JwtAuthenticationFilter(jwt, users);
    }
    @Bean AuthController authController(AuthService auth) { return new AuthController(auth); }
    @Bean TestTaskController testTaskController() { return new TestTaskController(); }
  }

  @RestController
  static class TestTaskController {
    @GetMapping("/api/tasks")
    String tasks() { return "ok"; }
  }

  @BeforeEach
  void setup() {
    reset(auth, jwt, users);
    mvc = MockMvcBuilders.webAppContextSetup(context)
      .addFilters(context.getBean("springSecurityFilterChain", Filter.class))
      .build();
  }

  @Test
  void csrfIsRequiredAndAnExpiredCookieCanStillBeCleared() throws Exception {
    mvc.perform(post("/api/auth/logout"))
      .andExpect(status().isForbidden());

    Cookie csrf = mvc.perform(get("/api/auth/csrf")
        .cookie(new Cookie("access_token", "expired")))
      .andExpect(status().isOk())
      .andReturn().getResponse().getCookie("XSRF-TOKEN");
    org.junit.jupiter.api.Assertions.assertNotNull(csrf);

    mvc.perform(post("/api/auth/logout")
        .cookie(csrf, new Cookie("access_token", "expired"))
        .header("X-XSRF-TOKEN", csrf.getValue()))
      .andExpect(status().isOk())
      .andExpect(cookie().maxAge("access_token", 0));
    verifyNoInteractions(jwt, users);
  }

  @Test
  void loginCookieAuthenticatesRequestsAndLogoutClearsIt() throws Exception {
    when(auth.getJwtService()).thenReturn(jwt);
    when(jwt.getExpiration()).thenReturn(Duration.ofMinutes(15));
    when(auth.loginAccount(any())).thenReturn(new LoginResponse("signed-token"));
    when(jwt.extractUserId("signed-token")).thenReturn(7L);
    when(users.loadByUserId(7L)).thenReturn(new TaskUserDetails(
      TaskUser.builder().id(7L).username("rowel").build()
    ));

    Cookie csrf = mvc.perform(get("/api/auth/csrf"))
      .andExpect(status().isOk())
      .andReturn().getResponse().getCookie("XSRF-TOKEN");
    org.junit.jupiter.api.Assertions.assertNotNull(csrf);
    Cookie access = mvc.perform(post("/api/auth/login")
        .cookie(csrf)
        .header("X-XSRF-TOKEN", csrf.getValue())
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"username\":\"rowel\",\"password\":\"password\"}"))
      .andExpect(status().isOk())
      .andReturn().getResponse().getCookie("access_token");
    org.junit.jupiter.api.Assertions.assertNotNull(access);

    mvc.perform(get("/api/tasks").cookie(access)).andExpect(status().isOk());
    mvc.perform(post("/api/auth/logout").cookie(csrf, access)
        .header("X-XSRF-TOKEN", csrf.getValue()))
      .andExpect(status().isOk())
      .andExpect(cookie().maxAge("access_token", 0));
    mvc.perform(get("/api/tasks")).andExpect(status().isUnauthorized());
  }
}
