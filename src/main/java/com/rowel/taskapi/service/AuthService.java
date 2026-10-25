package com.rowel.taskapi.service;

import com.rowel.taskapi.dto.auth.request.CreateAccountRequest;
import com.rowel.taskapi.dto.auth.request.LoginAccountRequest;
import com.rowel.taskapi.dto.auth.response.LoginResponse;
import com.rowel.taskapi.exception.user.UserAlreadyExistsException;
import com.rowel.taskapi.mapper.TaskUserMapper;
import com.rowel.taskapi.model.TaskUser;
import com.rowel.taskapi.repository.TaskUserRepository;
import com.rowel.taskapi.security.JwtService;
import com.rowel.taskapi.security.TaskUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;
import java.sql.SQLException;
import org.hibernate.exception.ConstraintViolationException;

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
  private final AuthenticationManager authenticationManager;

  public void createAccount(CreateAccountRequest req) {
    if (userRepository.findByUsername(req.username()).isPresent()) {
      throw new UserAlreadyExistsException();
    }

    TaskUser newUser = taskUserMapper.toEntity(req);
    newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
    try {
      // Flush here so a concurrent username conflict is caught in this method.
      userRepository.saveAndFlush(newUser);
    } catch (DataIntegrityViolationException ex) {
      if (isUsernameConflict(ex)) {
        throw new UserAlreadyExistsException(ex);
      }
      throw ex;
    }
  }

  private boolean isUsernameConflict(Throwable failure) {
    for (Throwable cause = failure; cause != null; cause = cause.getCause()) {
      if (cause instanceof ConstraintViolationException violation &&
          "UQ_user_username".equalsIgnoreCase(violation.getConstraintName())) {
        return true;
      }
      if (cause instanceof SQLException sql &&
          (sql.getErrorCode() == 2601 || sql.getErrorCode() == 2627) &&
          sql.getMessage() != null && sql.getMessage().contains("UQ_user_username")) {
        return true;
      }
    }
    return false;
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
}
