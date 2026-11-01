package com.rowel.taskapi.profile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.rowel.taskapi.common.exception.GlobalExceptionHandler;
import com.rowel.taskapi.profile.controller.TaskUserProfileController;
import com.rowel.taskapi.profile.dto.request.SaveTaskUserProfileRequest;
import com.rowel.taskapi.profile.dto.response.ProfilePhotoResponse;
import com.rowel.taskapi.profile.dto.response.TaskUserProfileResponse;
import com.rowel.taskapi.profile.exception.InvalidProfilePhotoException;
import com.rowel.taskapi.profile.exception.ProfileNotFoundException;
import com.rowel.taskapi.profile.exception.ProfilePhotoNotFoundException;
import com.rowel.taskapi.profile.exception.handler.ProfileExceptionHandler;
import com.rowel.taskapi.profile.service.TaskUserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class TaskUserProfileControllerTests {

  private final TaskUserProfileService profiles = mock(TaskUserProfileService.class);
  private MockMvc mvc;

  @BeforeEach
  void setup() {
    mvc = MockMvcBuilders.standaloneSetup(new TaskUserProfileController(profiles))
      .setControllerAdvice(new GlobalExceptionHandler(), new ProfileExceptionHandler())
      .build();
  }

  @Test
  void savesProfileDetailsAndReturnsTheResponse() throws Exception {
    var request = new SaveTaskUserProfileRequest("Rowel", "Hello", "Asia/Manila");
    when(profiles.saveUserProfile(request)).thenReturn(
      new TaskUserProfileResponse(1L, "rowel", "Rowel", "Hello", "Asia/Manila", true)
    );

    mvc.perform(put("/api/profile")
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\"displayName\":\"Rowel\",\"bio\":\"Hello\",\"timeZone\":\"Asia/Manila\"}"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.username").value("rowel"))
      .andExpect(jsonPath("$.data.displayName").value("Rowel"))
      .andExpect(jsonPath("$.data.hasPhoto").value(true));
    verify(profiles).saveUserProfile(request);
  }

  @Test
  void rejectsAFieldThatExceedsTheModelLimit() throws Exception {
    mvc.perform(put("/api/profile")
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\"displayName\":\"" + "a".repeat(101) + "\"}"))
      .andExpect(status().isBadRequest());
    verify(profiles, never()).saveUserProfile(any());
  }

  @Test
  void uploadsPhotoUsingTheExistingPath() throws Exception {
    var photo = new MockMultipartFile("photo", "photo.png", "image/png", new byte[] {1, 2});
    mvc.perform(multipart(org.springframework.http.HttpMethod.PUT, "/api/profile/photo").file(photo))
      .andExpect(status().isOk());
    verify(profiles).uploadUserProfilePhoto(photo);
  }

  @Test
  void retrievesPhotoAsAnImageUsingTheExistingPath() throws Exception {
    byte[] photo = {1, 2, 3};
    when(profiles.getUserProfilePhoto()).thenReturn(new ProfilePhotoResponse(photo, "image/png"));
    mvc.perform(get("/api/profile/photo").accept(MediaType.IMAGE_PNG))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.IMAGE_PNG))
      .andExpect(content().bytes(photo));
  }

  @Test
  void returnsNotFoundWhenProfileDoesNotExist() throws Exception {
    when(profiles.saveUserProfile(any())).thenThrow(new ProfileNotFoundException());
    mvc.perform(put("/api/profile").contentType(MediaType.APPLICATION_JSON).content("{}"))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.message").value("Profile not found."));
  }

  @Test
  void returnsBadRequestForInvalidPhoto() throws Exception {
    var photo = new MockMultipartFile("photo", "photo.png", "image/png", new byte[] {1});
    doThrow(new InvalidProfilePhotoException("Invalid photo.")).when(profiles).uploadUserProfilePhoto(any());
    mvc.perform(multipart(org.springframework.http.HttpMethod.PUT, "/api/profile/photo").file(photo))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("Invalid photo."));
  }

  @Test
  void returnsNotFoundForMissingPhoto() throws Exception {
    when(profiles.getUserProfilePhoto()).thenThrow(new ProfilePhotoNotFoundException());
    mvc.perform(get("/api/profile/photo").accept(MediaType.IMAGE_PNG))
      .andExpect(status().isNotFound());
  }
}
