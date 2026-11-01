package com.rowel.taskapi.profile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.rowel.taskapi.auth.model.TaskUser;
import com.rowel.taskapi.auth.repository.TaskUserRepository;
import com.rowel.taskapi.auth.security.TaskUserDetails;
import com.rowel.taskapi.profile.exception.InvalidProfilePhotoException;
import com.rowel.taskapi.profile.exception.ProfilePhotoNotFoundException;
import com.rowel.taskapi.profile.mapper.TaskUserProfileMapper;
import com.rowel.taskapi.profile.model.TaskUserProfile;
import com.rowel.taskapi.profile.repository.TaskUserProfileRepository;
import com.rowel.taskapi.profile.service.TaskUserProfileService;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class ProfilePhotoTests {
  private final TaskUserProfileRepository profiles = mock(TaskUserProfileRepository.class);
  private final TaskUserRepository users = mock(TaskUserRepository.class);
  private final TaskUserProfileService service = new TaskUserProfileService(profiles, users, new TaskUserProfileMapper());

  @BeforeEach
  void authenticate() {
    TaskUser user = TaskUser.builder().id(7L).username("user").build();
    SecurityContextHolder.getContext().setAuthentication(
      UsernamePasswordAuthenticationToken.authenticated(
        new TaskUserDetails(user), null, List.of()
      )
    );
  }

  @AfterEach
  void clearAuthentication() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void uploadsAndRetrievesPhotoForTheRequestedUser() throws Exception {
    TaskUser user = TaskUser.builder().id(7L).username("user").build();
    TaskUserProfile profile = TaskUserProfile.builder().user(user).build();
    when(profiles.findByUser_Id(7L)).thenReturn(Optional.of(profile));
    ByteArrayOutputStream image = new ByteArrayOutputStream();
    ImageIO.write(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), "png", image);
    service.uploadUserProfilePhoto(new MockMultipartFile("photo", "photo.png", "image/png", image.toByteArray()));
    assertEquals("image/png", service.getUserProfilePhoto().contentType());
    assertNotNull(ImageIO.read(new java.io.ByteArrayInputStream(service.getUserProfilePhoto().data())));
    verify(profiles).save(profile);
  }

  @Test
  void uploadingFirstPhotoCreatesProfileForSignedInUser() throws Exception {
    TaskUser user = TaskUser.builder().id(7L).username("user").build();
    when(profiles.findByUser_Id(7L)).thenReturn(Optional.empty());
    when(users.findById(7L)).thenReturn(Optional.of(user));
    ByteArrayOutputStream image = new ByteArrayOutputStream();
    ImageIO.write(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), "png", image);

    service.uploadUserProfilePhoto(new MockMultipartFile("photo", "photo.png", "image/png", image.toByteArray()));

    verify(profiles).save(argThat(profile ->
      profile.getUser() == user && profile.getPhotoData() != null &&
      "image/png".equals(profile.getPhotoContentType())
    ));
  }

  @Test
  void rejectsInvalidAndOversizedUploads() {
    var invalid = new MockMultipartFile("photo", "fake.png", "image/png", new byte[] {1, 2, 3});
    assertThrows(InvalidProfilePhotoException.class, () -> service.uploadUserProfilePhoto(invalid));
    var large = new MockMultipartFile("photo", "large.png", "image/png", new byte[2 * 1024 * 1024 + 1]);
    assertThrows(InvalidProfilePhotoException.class, () -> service.uploadUserProfilePhoto(large));
    verifyNoInteractions(profiles);
  }

  @Test
  void returnsNotFoundForMissingPhoto() {
    TaskUserProfile profile = TaskUserProfile.builder().user(TaskUser.builder().id(7L).build()).build();
    when(profiles.findByUser_Id(7L)).thenReturn(Optional.of(profile));
    assertThrows(ProfilePhotoNotFoundException.class, () -> service.getUserProfilePhoto());
  }

  @Test
  void photoOperationsRequireAuthentication() {
    SecurityContextHolder.clearContext();
    var photo = new MockMultipartFile("photo", "photo.png", "image/png", new byte[] {1});
    assertThrows(AuthenticationCredentialsNotFoundException.class, () -> service.uploadUserProfilePhoto(photo));
    assertThrows(AuthenticationCredentialsNotFoundException.class, service::getUserProfilePhoto);
    verifyNoInteractions(profiles);
  }
}
