package com.rowel.taskapi.auth.service;

import com.rowel.taskapi.auth.dto.request.CreateAccountRequest;
import com.rowel.taskapi.auth.dto.request.LoginAccountRequest;
import com.rowel.taskapi.auth.dto.response.LoginResponse;
import com.rowel.taskapi.auth.event.UserCreatedAccountEvent;
import com.rowel.taskapi.auth.exception.UserAlreadyExistsException;
import com.rowel.taskapi.auth.mapper.TaskUserMapper;
import com.rowel.taskapi.auth.model.TaskUser;
import com.rowel.taskapi.auth.repository.TaskUserRepository;
import com.rowel.taskapi.auth.security.JwtService;
import com.rowel.taskapi.auth.security.TaskUserDetails;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

  private final TaskUserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final TaskUserMapper taskUserMapper;
  private final JwtService jwtService;

  private final AuthenticationManager authenticationManager;
  private final ApplicationEventPublisher eventPublisher;

  public AuthService(
    TaskUserRepository userRepository,
    PasswordEncoder passwordEncoder,
    TaskUserMapper taskUserMapper,
    JwtService jwtService,
    AuthenticationManager authenticationManager,
    ApplicationEventPublisher eventPublisher
  ) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.taskUserMapper = taskUserMapper;
    this.jwtService = jwtService;
    this.authenticationManager = authenticationManager;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public void createAccount(CreateAccountRequest req) {
    if (userRepository.findByUsername(req.username()).isPresent()) {
      throw new UserAlreadyExistsException();
    }

    TaskUser newUser = taskUserMapper.toEntity(req);
    newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
    userRepository.saveAndFlush(newUser);

    eventPublisher.publishEvent(new UserCreatedAccountEvent(newUser));
  }

  public LoginResponse loginAccount(LoginAccountRequest req) {
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

  public JwtService getJwtService() {
    return jwtService;
  }
}
