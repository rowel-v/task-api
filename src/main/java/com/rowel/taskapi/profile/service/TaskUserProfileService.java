package com.rowel.taskapi.profile.service;

import com.rowel.taskapi.auth.exception.UserNotFoundException;
import com.rowel.taskapi.auth.repository.TaskUserRepository;
import com.rowel.taskapi.auth.security.CurrentUserService;
import com.rowel.taskapi.auth.security.TaskUserDetails;
import com.rowel.taskapi.profile.dto.request.SaveTaskUserProfileRequest;
import com.rowel.taskapi.profile.dto.response.ProfilePhotoResponse;
import com.rowel.taskapi.profile.dto.response.TaskUserProfileResponse;
import com.rowel.taskapi.profile.exception.InvalidProfilePhotoException;
import com.rowel.taskapi.profile.exception.ProfileNotFoundException;
import com.rowel.taskapi.profile.exception.ProfilePhotoNotFoundException;
import com.rowel.taskapi.profile.mapper.TaskUserProfileMapper;
import com.rowel.taskapi.profile.model.TaskUserProfile;
import com.rowel.taskapi.profile.repository.TaskUserProfileRepository;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.springframework.modulith.NamedInterface;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@NamedInterface("task_user_profile_service")
@Service
public class TaskUserProfileService {

  private final CurrentUserService authenticatedUser;
  private final TaskUserProfileRepository taskUserProfileRepository;
  private final TaskUserRepository taskUserRepository;
  private final TaskUserProfileMapper taskUserProfileMapper;

  public TaskUserProfileService(
    CurrentUserService authenticatedUser,
    TaskUserProfileRepository taskUserProfileRepository,
    TaskUserRepository taskUserRepository,
    TaskUserProfileMapper taskUserProfileMapper
  ) {
    this.authenticatedUser = authenticatedUser;
    this.taskUserProfileRepository = taskUserProfileRepository;
    this.taskUserRepository = taskUserRepository;
    this.taskUserProfileMapper = taskUserProfileMapper;
  }

  private TaskUserProfile findUserProfile() {
    return taskUserProfileRepository
      .findByUser_Id(authenticatedUser.getId())
      .orElseThrow(ProfileNotFoundException::new);
  }

  private TaskUserProfile findOrCreateUserProfile(Long userId) {
    return taskUserProfileRepository
      .findByUser_Id(userId)
      .orElseGet(() ->
        TaskUserProfile.builder()
          .user(
            taskUserRepository
              .findById(userId)
              .orElseThrow(UserNotFoundException::new)
          )
          .build()
      );
  }

  @Transactional
  public TaskUserProfileResponse saveUserProfile(
    SaveTaskUserProfileRequest req
  ) {
    TaskUserProfile profile = findOrCreateUserProfile(
      authenticatedUser.getId()
    );

    taskUserProfileMapper.updateProfile(req, profile);
    return taskUserProfileMapper.toResponse(
      taskUserProfileRepository.save(profile)
    );
  }

  @Transactional(readOnly = true)
  public ProfilePhotoResponse getUserProfilePhoto() {
    TaskUserProfile profile = findUserProfile();
    if (
      profile.getPhotoData() == null || profile.getPhotoContentType() == null
    ) {
      throw new ProfilePhotoNotFoundException();
    }
    return new ProfilePhotoResponse(
      profile.getPhotoData(),
      profile.getPhotoContentType()
    );
  }

  private static final long MAX_PHOTO_BYTES = 2 * 1024 * 1024;
  private static final int MAX_DIMENSION = 4096;

  @Transactional
  public void uploadUserProfilePhoto(MultipartFile file) {
    Long userId = authenticatedUser.getId();
    if (file.isEmpty() || file.getSize() > MAX_PHOTO_BYTES) {
      throw new InvalidProfilePhotoException(
        "Choose a JPEG or PNG image up to 2 MB."
      );
    }

    try (
      InputStream stream = file.getInputStream();
      ImageInputStream input = ImageIO.createImageInputStream(stream)
    ) {
      if (input == null) throw new InvalidProfilePhotoException();
      Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
      if (!readers.hasNext()) throw new InvalidProfilePhotoException();
      ImageReader reader = readers.next();
      try {
        reader.setInput(input, true, true);
        String format = reader
          .getFormatName()
          .toLowerCase(java.util.Locale.ROOT);
        if (
          !format.equals("jpeg") && !format.equals("png")
        ) throw new InvalidProfilePhotoException();
        int width = reader.getWidth(0);
        int height = reader.getHeight(0);
        if (
          width < 1 ||
          height < 1 ||
          width > MAX_DIMENSION ||
          height > MAX_DIMENSION
        ) {
          throw new InvalidProfilePhotoException();
        }
        BufferedImage image = reader.read(0);
        if (image == null) throw new InvalidProfilePhotoException();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        if (
          !ImageIO.write(image, format, output) ||
          output.size() > MAX_PHOTO_BYTES
        ) throw new InvalidProfilePhotoException();
        TaskUserProfile profile = findOrCreateUserProfile(userId);
        profile.setPhotoData(output.toByteArray());
        profile.setPhotoContentType(
          format.equals("png") ? "image/png" : "image/jpeg"
        );
        taskUserProfileRepository.save(profile);
      } finally {
        reader.dispose();
      }
    } catch (IOException | IllegalArgumentException ex) {
      throw new InvalidProfilePhotoException();
    }
  }
}
