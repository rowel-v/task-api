package com.rowel.taskapi;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.rowel.taskapi.controller.protectedC.TaskController;
import com.rowel.taskapi.dto.auth.request.*;
import com.rowel.taskapi.exception.GlobalExceptionHandler;
import com.rowel.taskapi.exception.task.*;
import com.rowel.taskapi.exception.user.UserAlreadyExistsException;
import com.rowel.taskapi.mapper.*;
import com.rowel.taskapi.model.*;
import com.rowel.taskapi.repository.*;
import com.rowel.taskapi.security.*;
import com.rowel.taskapi.service.*;
import jakarta.validation.*;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

class BackendRegressionTests {
  static ValidatorFactory factory;
  static Validator validator;
  TaskUserRepository users = mock(TaskUserRepository.class);
  TaskRepository tasks = mock(TaskRepository.class);
  PasswordEncoder encoder = mock(PasswordEncoder.class);
  AuthService auth = new AuthService(users, encoder, new TaskUserMapper(), mock(JwtService.class), mock(AuthenticationManager.class));
  TaskService service = new TaskService(tasks, users, new TaskMapper());

  @BeforeAll static void validation() {
    factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }
  @AfterAll static void close() { factory.close(); }
  @BeforeEach void authenticate() {
    TaskUser user = TaskUser.builder().id(1L).username("owner").build();
    SecurityContextHolder.getContext().setAuthentication(
        UsernamePasswordAuthenticationToken.authenticated(new TaskUserDetails(user), null, List.of()));
    when(encoder.encode(anyString())).thenReturn("encoded");
  }
  @AfterEach void clear() { SecurityContextHolder.clearContext(); }

  @Test void accepts72AsciiBytes() {
    assertTrue(validator.validate(new CreateAccountRequest("user", "a".repeat(72))).isEmpty());
    assertTrue(validator.validate(new LoginAccountRequest("user", "a".repeat(72))).isEmpty());
  }
  @Test void rejects73AsciiBytes() {
    assertFalse(validator.validate(new CreateAccountRequest("user", "a".repeat(73))).isEmpty());
    assertFalse(validator.validate(new LoginAccountRequest("user", "a".repeat(73))).isEmpty());
  }
  @Test void validatesUtf8BytesRatherThanCharacters() {
    assertTrue(validator.validate(new CreateAccountRequest("user", "€".repeat(24))).isEmpty());
    assertFalse(validator.validate(new CreateAccountRequest("user", "€".repeat(25))).isEmpty());
    assertFalse(validator.validate(new LoginAccountRequest("user", "€".repeat(25))).isEmpty());
  }
  @Test void blankPasswordRemainsInvalid() {
    assertFalse(validator.validate(new CreateAccountRequest("user", " ")).isEmpty());
    assertFalse(validator.validate(new LoginAccountRequest("user", null)).isEmpty());
  }
  @Test void duplicateLookupDoesNotSave() {
    when(users.findByUsername("user")).thenReturn(Optional.of(new TaskUser()));
    assertThrows(UserAlreadyExistsException.class, () -> auth.createAccount(new CreateAccountRequest("user", "password")));
    verify(users, never()).saveAndFlush(any());
  }
  @Test void translatesConcurrentNamedUsernameConstraint() {
    var failure = new DataIntegrityViolationException("duplicate", new ConstraintViolationException("duplicate", new SQLException(), "UQ_user_username"));
    when(users.saveAndFlush(any())).thenThrow(failure);
    var ex = assertThrows(UserAlreadyExistsException.class, () -> auth.createAccount(new CreateAccountRequest("user", "password")));
    assertSame(failure, ex.getCause());
    assertEquals(409, new GlobalExceptionHandler().handleUserAlreadyExists(ex).getStatusCode().value());
  }
  @Test void translatesSqlServerUsernameConstraint() {
    var failure = new DataIntegrityViolationException("duplicate", new SQLException("Violation of UNIQUE KEY constraint 'UQ_user_username'", "23000", 2627));
    when(users.saveAndFlush(any())).thenThrow(failure);
    assertThrows(UserAlreadyExistsException.class, () -> auth.createAccount(new CreateAccountRequest("user", "password")));
  }
  @Test void unrelatedIntegrityFailureIsNotReportedAsDuplicate() {
    var failure = new DataIntegrityViolationException("other constraint", new SQLException("FK_other", "23000", 547));
    when(users.saveAndFlush(any())).thenThrow(failure);
    assertSame(failure, assertThrows(DataIntegrityViolationException.class, () -> auth.createAccount(new CreateAccountRequest("user", "password"))));
  }
  @Test void registrationEncodesPasswordAndLeavesDisplayNameUnset() {
    auth.createAccount(new CreateAccountRequest("user", "password"));
    verify(users).saveAndFlush(argThat(u -> u.getUsername().equals("user") && u.getPassword().equals("encoded") && u.getDisplayName() == null));
  }
  @Test void distinctUnsavedTasksAreUnequal() {
    Task first = Task.builder().build();
    Task second = Task.builder().build();
    assertNotEquals(first, second);
    assertEquals(first, first);
    assertEquals(2, new HashSet<>(List.of(first, second)).size());
  }
  @Test void persistedIdentityUsesStableHashAndGetters() {
    Task task = Task.builder().build();
    int hash = task.hashCode();
    ReflectionTestUtils.setField(task, "id", 10L);
    Task other = Task.builder().name("different").build();
    ReflectionTestUtils.setField(other, "id", 10L);
    assertEquals(task, other);
    assertEquals(hash, task.hashCode());
    assertEquals(task.hashCode(), other.hashCode());
  }
  Task completedTask() {
    Task task = Task.builder().status(TaskStatus.COMPLETED).name("Task").priority(TaskPriority.LOW).build();
    ReflectionTestUtils.setField(task, "id", 10L);
    return task;
  }
  @Test void deletionDeduplicatesAndReportsActualCount() {
    Task task = completedTask();
    when(tasks.findAllByIdInAndUser_Id(List.of(10L), 1L)).thenReturn(List.of(task));
    var response = new TaskController(service).deleteTask(List.of(10L, 10L));
    assertEquals("Task deleted successfully", response.getBody().message());
    verify(tasks).deleteAll(List.of(task));
  }
  @Test void archiveDeduplicatesAndReportsActualCount() {
    Task task = completedTask();
    when(tasks.findAllByIdInAndUser_Id(List.of(10L), 1L)).thenReturn(List.of(task));
    var response = new TaskController(service).archiveTask(List.of(10L, 10L));
    assertEquals("Task archived successfully", response.getBody().message());
    assertEquals(1, response.getBody().data().size());
    assertTrue(task.isArchived());
  }
  @Test void invalidBulkIdsFailBeforeRepositoryAccess() {
    for (List<Long> ids : Arrays.asList(List.<Long>of(), List.of(0L), List.of(-1L), Arrays.asList((Long) null))) {
      assertThrows(InvalidTaskIdsException.class, () -> service.deleteTask(ids));
      assertThrows(InvalidTaskIdsException.class, () -> service.archiveTasks(ids));
    }
    assertThrows(InvalidTaskIdsException.class, () -> service.deleteTask(null));
    verifyNoInteractions(tasks);
    assertEquals(400, new GlobalExceptionHandler().handleInvalidTaskIds(new InvalidTaskIdsException()).getStatusCode().value());
  }
  @Test void missingOrForeignTaskRejectsWholeDeletion() {
    when(tasks.findAllByIdInAndUser_Id(List.of(10L, 20L), 1L)).thenReturn(List.of(completedTask()));
    assertThrows(TaskNotFoundException.class, () -> service.deleteTask(List.of(10L, 20L)));
    verify(tasks, never()).deleteAll(any());
  }
  @Test void incompleteTaskRejectsWholeArchive() {
    Task task = completedTask();
    task.setStatus(TaskStatus.PENDING);
    when(tasks.findAllByIdInAndUser_Id(List.of(10L), 1L)).thenReturn(List.of(task));
    assertThrows(TaskNotCompletedException.class, () -> service.archiveTasks(List.of(10L)));
    assertFalse(task.isArchived());
    verify(tasks, never()).saveAll(any());
  }
}
