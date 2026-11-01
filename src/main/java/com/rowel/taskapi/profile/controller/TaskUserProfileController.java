package com.rowel.taskapi.profile.controller;

import com.rowel.taskapi.common.dto.ApiResponse;
import com.rowel.taskapi.profile.dto.request.SaveTaskUserProfileRequest;
import com.rowel.taskapi.profile.dto.response.ProfilePhotoResponse;
import com.rowel.taskapi.profile.dto.response.TaskUserProfileResponse;
import com.rowel.taskapi.profile.service.TaskUserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/profile", produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskUserProfileController {

  private final TaskUserProfileService profileService;

  public TaskUserProfileController(TaskUserProfileService profileService) {
    this.profileService = profileService;
  }

  @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<TaskUserProfileResponse>> save(
    @Valid @RequestBody SaveTaskUserProfileRequest request
  ) {
    TaskUserProfileResponse profile = profileService.saveUserProfile(request);
    return ResponseEntity.ok(
      ApiResponse.successful("Profile saved successfully", profile)
    );
  }

  @PutMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<Void>> uploadPhoto(
    @RequestPart("photo") MultipartFile photo
  ) {
    profileService.uploadUserProfilePhoto(photo);
    return ResponseEntity.ok(
      ApiResponse.successful("Profile photo updated successfully", null)
    );
  }

  @GetMapping(value = "/photo", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
  public ResponseEntity<byte[]> getPhoto() {
    ProfilePhotoResponse photo = profileService.getUserProfilePhoto();
    return ResponseEntity.ok()
      .cacheControl(CacheControl.noStore())
      .contentType(MediaType.parseMediaType(photo.contentType()))
      .body(photo.data());
  }
}
