package com.rowel.taskapi.profile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.rowel.taskapi.auth.model.TaskUser;
import com.rowel.taskapi.auth.repository.TaskUserRepository;
import com.rowel.taskapi.auth.exception.UserNotFoundException;
import com.rowel.taskapi.auth.security.CurrentUserService;
import com.rowel.taskapi.auth.security.TaskUserDetails;
import com.rowel.taskapi.profile.dto.request.SaveTaskUserProfileRequest;
import com.rowel.taskapi.profile.mapper.TaskUserProfileMapper;
import com.rowel.taskapi.profile.model.TaskUserProfile;
import com.rowel.taskapi.profile.repository.TaskUserProfileRepository;
import com.rowel.taskapi.profile.service.TaskUserProfileService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

class TaskUserProfileServiceTests {

  private final TaskUserProfileRepository profiles = mock(TaskUserProfileRepository.class);
  private final TaskUserRepository users = mock(TaskUserRepository.class);
  private final TaskUserProfileService service = new TaskUserProfileService(
    new CurrentUserService(users), profiles, users, new TaskUserProfileMapper()
  );

  @AfterEach
  void clearAuthentication() {
    SecurityContextHolder.clearContext();
  }

  private TaskUser authenticate() {
    TaskUser user = TaskUser.builder().id(7L).username("rowel").build();
    SecurityContextHolder.getContext().setAuthentication(
      UsernamePasswordAuthenticationToken.authenticated(
        new TaskUserDetails(user), null, List.of()
      )
    );
    return user;
  }

  @Test
  void savesDetailsForAuthenticatedUserAndPreservesPhoto() {
    TaskUser user = authenticate();
    byte[] photo = {1, 2, 3};
    TaskUserProfile profile = TaskUserProfile.builder()
      .user(user)
      .photoData(photo)
      .photoContentType("image/png")
      .build();
    ReflectionTestUtils.setField(profile, "id", 12L);
    when(profiles.findByUser_Id(7L)).thenReturn(Optional.of(profile));
    when(profiles.save(any(TaskUserProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

    var result = service.saveUserProfile(new SaveTaskUserProfileRequest("Rowel", "Hello", "Asia/Manila"));

    assertEquals(12L, result.id());
    assertEquals("rowel", result.username());
    assertEquals("Rowel", result.displayName());
    assertEquals("Hello", result.bio());
    assertEquals("Asia/Manila", result.timeZone());
    assertTrue(result.hasPhoto());
    assertSame(photo, profile.getPhotoData());
    assertEquals("image/png", profile.getPhotoContentType());
    assertSame(user, profile.getUser());
    verify(profiles).save(profile);
  }

  @Test
  void saveCanClearOptionalFields() {
    TaskUserProfile profile = TaskUserProfile.builder()
      .user(authenticate()).displayName("Old").bio("Old").timeZone("UTC").build();
    when(profiles.findByUser_Id(7L)).thenReturn(Optional.of(profile));
    when(profiles.save(any(TaskUserProfile.class))).thenReturn(profile);

    var result = service.saveUserProfile(new SaveTaskUserProfileRequest(null, null, null));

    assertNull(result.displayName());
    assertNull(result.bio());
    assertNull(result.timeZone());
    assertFalse(result.hasPhoto());
  }

  @Test
  void requiresAuthenticationBeforeLookingUpAProfile() {
    assertThrows(AuthenticationCredentialsNotFoundException.class, () ->
      service.saveUserProfile(new SaveTaskUserProfileRequest("Rowel", null, null))
    );
    verifyNoInteractions(profiles);
  }

  @Test
  void firstSaveCreatesProfileForSignedInAccount() {
    TaskUser user = authenticate();
    when(profiles.findByUser_Id(7L)).thenReturn(Optional.empty());
    when(users.findById(7L)).thenReturn(Optional.of(user));
    when(profiles.save(any(TaskUserProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

    var result = service.saveUserProfile(new SaveTaskUserProfileRequest("Rowel", "Hello", "Asia/Manila"));

    assertEquals("rowel", result.username());
    assertEquals("Rowel", result.displayName());
    assertFalse(result.hasPhoto());
    verify(profiles).save(argThat(profile -> profile.getUser() == user));
  }

  @Test
  void cannotCreateProfileForMissingUser() {
    authenticate();
    when(profiles.findByUser_Id(7L)).thenReturn(Optional.empty());
    when(users.findById(7L)).thenReturn(Optional.empty());
    assertThrows(UserNotFoundException.class, () ->
      service.saveUserProfile(new SaveTaskUserProfileRequest("Rowel", null, null))
    );
    verify(profiles, never()).save(any());
  }
}
