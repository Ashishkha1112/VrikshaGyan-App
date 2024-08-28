package np.edu.nast.vrikshagyanserver.service;

import np.edu.nast.vrikshagyanserver.entity.ProfileImage;
import np.edu.nast.vrikshagyanserver.entity.User;
import np.edu.nast.vrikshagyanserver.repository.ProfileImageRepository;
import np.edu.nast.vrikshagyanserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import net.coobird.thumbnailator.Thumbnails;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class ProfileImageService {

    @Autowired
    private ProfileImageRepository profileImageRepository;

    @Autowired
    private UserRepository userRepo;

    @Value("${upload.directory}")
    private String uploadDir;

    public ProfileImage uploadProfileImage(MultipartFile file, Long userId) throws IOException {
        // Get absolute path of the upload directory
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        // Ensure the directory exists
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Retrieve the user
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Check for existing profile image and delete it
        Optional<ProfileImage> existingProfileImage = profileImageRepository.findByUserUserId(userId);
        if (existingProfileImage.isPresent()) {
            deleteFile(existingProfileImage.get().getProfileImage());
            profileImageRepository.delete(existingProfileImage.get());
        }

        // Save the new file locally with a unique filename
        String fileName = userId + "_" + file.getOriginalFilename();
        Path targetLocation = uploadPath.resolve(fileName);

        // Resize the image if needed and save it
        resizeImageIfNeeded(file, targetLocation);

        // Create a new ProfileImage entity
        ProfileImage profileImage = new ProfileImage();
        profileImage.setUser(user);
        profileImage.setProfileImage(fileName); // Store only the relative path

        // Save and return the new profile image entity
        return profileImageRepository.save(profileImage);
    }

    private void deleteFile(String fileName) {
        if (fileName != null) {
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            File file = filePath.toFile();
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private void resizeImageIfNeeded(MultipartFile file, Path targetLocation) throws IOException {
        // Check if the file size exceeds 1 MB
        if (file.getSize() > 1024 * 1024) { // 1 MB = 1024 * 1024 bytes
            // Resize the image to a maximum of 1024x1024 pixels and reduce quality to 90%
            Thumbnails.of(file.getInputStream())
                      .size(1024, 1024)
                      .outputQuality(0.9)
                      .toFile(targetLocation.toFile());
        } else {
            // Simply copy the file if resizing is not needed
            Files.copy(file.getInputStream(), targetLocation);
        }
    }
}
