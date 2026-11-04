package com.rowel.taskapi.auth.controller;

import com.rowel.taskapi.auth.dto.request.CreateAccountRequest;
import com.rowel.taskapi.auth.dto.request.LoginAccountRequest;
import com.rowel.taskapi.auth.dto.response.LoginResponse;
import com.rowel.taskapi.auth.service.AuthService;
import com.rowel.taskapi.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
  value = "/api/auth",
  produces = MediaType.APPLICATION_JSON_VALUE
)
public class AuthController {

  private static final String ACCESS_TOKEN_COOKIE = "access_token";

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  ResponseEntity<ApiResponse<Void>> register(
    @RequestBody @Valid CreateAccountRequest req
  ) {
    authService.createAccount(req);
    return ResponseEntity.ok(
      ApiResponse.successful("Account created successfully", null)
    );
  }

  @GetMapping("/csrf")
  ResponseEntity<ApiResponse<CsrfToken>> csrf(CsrfToken token) {
    return ResponseEntity.ok()
      .header(HttpHeaders.CACHE_CONTROL, "no-store")
      .body(ApiResponse.successful("CSRF token retrieved successfully", token));
  }

  @PostMapping("/login")
  ResponseEntity<ApiResponse<Void>> login(
    @RequestBody @Valid LoginAccountRequest req
  ) {
    LoginResponse result = authService.loginAccount(req);

    ResponseCookie cookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE, result.token())
      .httpOnly(true)
      .secure(true)
      .sameSite("Lax")
      .path("/")
      .maxAge(authService.getJwtService().getExpiration())
      .build();

    return ResponseEntity.ok()
      .header(HttpHeaders.SET_COOKIE, cookie.toString())
      .header(HttpHeaders.CACHE_CONTROL, "no-store")
      .body(ApiResponse.successful("Logged in successfully", null));
  }

  @PostMapping("/logout")
  ResponseEntity<ApiResponse<Void>> logout() {
    ResponseCookie cookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE, "")
      .httpOnly(true)
      .secure(true)
      .sameSite("Lax")
      .path("/")
      .maxAge(0)
      .build();

    return ResponseEntity.ok()
      .header(HttpHeaders.SET_COOKIE, cookie.toString())
      .header(HttpHeaders.CACHE_CONTROL, "no-store")
      .body(ApiResponse.successful("Logged out successfully", null));
  }
}
