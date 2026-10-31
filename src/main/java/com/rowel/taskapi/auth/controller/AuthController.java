package com.rowel.taskapi.auth.controller;

import com.rowel.taskapi.auth.dto.request.CreateAccountRequest;
import com.rowel.taskapi.auth.dto.request.LoginAccountRequest;
import com.rowel.taskapi.auth.dto.response.LoginResponse;
import com.rowel.taskapi.auth.service.AuthService;
import com.rowel.taskapi.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

  @PostMapping("/login")
  ResponseEntity<ApiResponse<LoginResponse>> login(
    @RequestBody @Valid LoginAccountRequest req
  ) {
    LoginResponse result = authService.loginAccount(req);
    return ResponseEntity.ok(
      ApiResponse.successful("Logged in successfully", result)
    );
  }
}
