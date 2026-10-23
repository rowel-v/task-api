package com.rowel.taskapi.service;

import com.rowel.taskapi.dto.request.CreateAccountRequest;
import com.rowel.taskapi.dto.request.LoginAccountRequest;
import com.rowel.taskapi.dto.response.LoginResponse;
import com.rowel.taskapi.exception.UserAlreadyExistException;
import com.rowel.taskapi.mapper.TaskUserMapper;
import com.rowel.taskapi.model.TaskUser;
import com.rowel.taskapi.repository.TaskUserRepository;
import com.rowel.taskapi.security.JwtService;
import com.rowel.taskapi.security.TaskUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  public AuthService(
    TaskUserRepository userRepository,
    PasswordEncoder passwordEncoder,
    TaskUserMapper taskUserMapper,
    JwtService jwtService,
    AuthenticationManager authenticationManager
  ) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.taskUserMapper = taskUserMapper;
    this.jwtService = jwtService;
    this.authenticationManager = authenticationManager;
  }

  private final TaskUserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final TaskUserMapper taskUserMapper;
  private final JwtService jwtService;
  private AuthenticationManager authenticationManager;

  void createAccount(CreateAccountRequest req) {
    userRepository
      .findByUsername(req.username())
      .ifPresentOrElse(
        user -> new UserAlreadyExistException(),
        () -> {
          TaskUser newUser = taskUserMapper.toEntity(req);
          newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
          userRepository.save(newUser);
        }
      );
  }

  LoginResponse loginAccount(LoginAccountRequest req) {
    Authentication authReq = new UsernamePasswordAuthenticationToken(
      req.username(),
      req.password()
    );

    Authentication authResult = authenticationManager.authenticate(authReq);

    TaskUserDetails taskUser = (TaskUserDetails) authResult.getPrincipal();

    return new LoginResponse(
      jwtService.generateToken(taskUserMapper.toEntity(taskUser))
    );
  }
}
