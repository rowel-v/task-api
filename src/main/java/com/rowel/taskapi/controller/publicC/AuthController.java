package com.rowel.taskapi.controller.publicC;

import com.rowel.taskapi.dto.auth.request.CreateAccountRequest;
import com.rowel.taskapi.dto.auth.request.LoginAccountRequest;
import com.rowel.taskapi.dto.auth.response.LoginResponse;
import com.rowel.taskapi.dto.common.ApiResponse;
import com.rowel.taskapi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  private final AuthService authService;

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
