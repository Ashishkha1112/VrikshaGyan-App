package np.edu.nast.vrikshagyanserver.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Base64Utils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import np.edu.nast.vrikshagyanserver.entity.ProfileImage;
import np.edu.nast.vrikshagyanserver.entity.ResetPassword;
import np.edu.nast.vrikshagyanserver.entity.Role;
import np.edu.nast.vrikshagyanserver.entity.User;
import np.edu.nast.vrikshagyanserver.entity.UserAuditLog;
import np.edu.nast.vrikshagyanserver.repository.ProfileImageRepository;
import np.edu.nast.vrikshagyanserver.repository.UserAuditLogRespository;
import np.edu.nast.vrikshagyanserver.repository.UserRepository;
import np.edu.nast.vrikshagyanserver.response.ResponseMessage;
import np.edu.nast.vrikshagyanserver.service.CustomUserDetailService;
import np.edu.nast.vrikshagyanserver.service.ProfileImageService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Value("${upload.directory}")
    private String directoryPath;

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private UserAuditLogRespository userAuditRepo;
	
	@Autowired
	ProfileImageService profileImageService;
	
	@Autowired
	ProfileImageRepository profileImageRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public UserController(CustomUserDetailService customUserDetailService) {
	}
	 
	// Listing User
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/admin/list")
//	public List<User> findAllUser() {
//		return userRepo.findByIsDeleted(false);
//	}

public List<User> findAllUser() {
    // Get the currently logged-in user's email (or username)
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String loggedInUserEmail = authentication.getName(); // Assuming email is the username

    // Fetch all non-deleted users
    List<User> allUsers = userRepo.findByIsDeleted(false);

    // Filter out the logged-in user
    return allUsers.stream()
                   .filter(user -> !user.getEmail().equals(loggedInUserEmail))
                   .collect(Collectors.toList());
}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/admin/deleteduser")
	public List<User> findAllDeletedUser() {
		return userRepo.findByIsDeleted(true);
	}

	// Fetching Role
	@GetMapping("/roles")
	public Role[] getRoles() {
		return Role.values();
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/signup")
	public ResponseEntity<?> signUpUser(@Valid @RequestBody User newUser, BindingResult bindingResult, Authentication authen) {
		String email = authen.getName();
		// Validate input fields
		if (bindingResult.hasErrors()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ResponseMessage("Validation error", bindingResult.getAllErrors()));
		}

		// Check if email already exists
		if (userRepo.existsByEmail(newUser.getEmail())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Email already exists!"));
		}

		// Check if phone number already exists
		if (userRepo.existsByPhone(newUser.getPhone())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ResponseMessage("Phone number already exists!"));
		}

		try {
			// Encode the password before saving
			newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

			// Save the user
			User savedUser = userRepo.save(newUser);
			logAuditAction("User Created", email, LocalDateTime.now(), savedUser.getUserId(), "User Created with ID: " + savedUser.getUserId());
			// Return success response
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(new ResponseMessage("User signed up successfully", savedUser));
		} catch (Exception e) {
			// Handle exceptions
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseMessage("Error occurred while signing up"));
		}
	}
//	for android
	@PostMapping("/signupUser")
    public ResponseEntity<?> signUpUsers(@Valid @RequestBody User newUser, BindingResult bindingResult) {
        // Validate input fields
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(new ResponseMessage("Validation error", bindingResult.getAllErrors()));
        }

        // Check if email already exists
        if (userRepo.existsByEmail(newUser.getEmail()) || userRepo.existsByPhone(newUser.getPhone())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(new ResponseMessage("Email or phone number already exists!"));
        }


        try {
            // Encode the password before saving
            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

            // Save the user
            User savedUser = userRepo.save(newUser);

            // Return success response
            return ResponseEntity.status(HttpStatus.CREATED)
                                 .body(new ResponseMessage("User signed up successfully", savedUser));
        } catch (Exception e) {
            // Handle exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new ResponseMessage("Error occurred while signing up"));
        }
    }
		// Update delete status
		@PreAuthorize("hasRole('ROLE_ADMIN')")
		@PutMapping("/admin/verify/{userId}")
		public ResponseEntity<Object> markAsVerified(@PathVariable Long userId, Authentication authen) {
		    String email = authen.getName();
		    Optional<User> userOptional = userRepo.findById(userId);

		    if (!userOptional.isPresent()) {
		        return ResponseEntity.notFound().build();
		    }

		    User user = userOptional.get();
		    
		    // Check if the status is already true
		    if (user.isStatus()) {
		        return ResponseEntity.status(HttpStatus.CONFLICT)
		                             .body("User is already verified.");
		    }
		    // Update status to true if it's currently false
		    user.setStatus(true);
		    userRepo.save(user);
		    
		    logAuditAction("User Verified", email, LocalDateTime.now(), user.getUserId(), "User Verified with ID: " + user.getUserId());
		    
		    return ResponseEntity.ok().build();
		}


	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("/admin/update/{userId}")
	public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User userDetails,
			BindingResult bindingResult, Authentication authentication) {
		
		String email = authentication.getName();
		// Validate input
		if (bindingResult.hasErrors()) {
			// Collect all validation errors into a list of strings
			List<String> validationErrors = bindingResult.getFieldErrors().stream()
					.map(error -> error.getDefaultMessage()).collect(Collectors.toList());

			// Return bad request status with validation error messages
			return ResponseEntity.badRequest().body(validationErrors);
		}

		// Find user by ID
		Optional<User> optionalUser = userRepo.findById(userId);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();

			// Check if the logged-in user is an admin or the owner of the profile
			boolean isAdmin = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
			String loggedInUserEmail = authentication.getName();
			User loggedInUser = userRepo.findByEmail(loggedInUserEmail);
			if (loggedInUser != null) {
				if (isAdmin || loggedInUser.getUserId().equals(userId)) {
					// Update fields
					user.setFirstName(userDetails.getFirstName());
					user.setMiddleName(userDetails.getMiddleName());
					user.setLastName(userDetails.getLastName());
					user.setAddress(userDetails.getAddress());
					user.setPhone(userDetails.getPhone());
					user.setEmail(userDetails.getEmail());
					user.setOccupation(userDetails.getOccupation());
					user.setRole(userDetails.getRole());
					user.setStatus(userDetails.isStatus());

					// Update password only if provided
					if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
						user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
					} else {
						user.setPassword(user.getPassword());
					}

					// Save updated user
					User updatedUser = userRepo.save(user);
					logAuditAction("User Updated", email, LocalDateTime.now(), updatedUser.getUserId(), "User updated with ID: " + updatedUser.getUserId());
					return ResponseEntity.ok(updatedUser);
				} else {
					// Return forbidden status if the user is not authorized to update the profile
					return ResponseEntity.status(HttpStatus.FORBIDDEN)
							.body("You are not authorized to update this profile");
				}
			} else {
				// Return unauthorized status if the logged-in user does not exist
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Logged-in user not found");
			}
		} else {
			// Return not found status if user does not exist
			return ResponseEntity.notFound().build();
		}
	}

	// Update delete status
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("/admin/delete/{userId}")
	public ResponseEntity<Object> markAsDeleted(@PathVariable Long userId, Authentication authen) {
		String email = authen.getName();
		Optional<User> userOptional = userRepo.findById(userId);
		if (!userOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		User user = userOptional.get();
		user.setDeleted(true);
		userRepo.save(user);
		logAuditAction("User Deleted", email, LocalDateTime.now(), user.getUserId(), "User Deleted with ID: " + user.getUserId());
		return ResponseEntity.ok().build();
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("admin/editUser/{id}")
	public ResponseMessage findStudent(@PathVariable("id") Long id) {
		ResponseMessage re = new ResponseMessage();

		Optional<User> userOptional = userRepo.findById(id);
		Optional<ProfileImage> profileImageOptional = profileImageRepository.findByUserUserId(id);

		java.util.Map<String, Object> responseData = new HashMap<>();

		if (userOptional.isPresent()) {
			User user = userOptional.get();
			// Masking the password before sending the user object
			user.setPassword(null);
			responseData.put("user", user);
		}

		if (profileImageOptional.isPresent()) {
			ProfileImage profileImg = profileImageOptional.get();
			responseData.put("profileImage", profileImg);
		} else {
			responseData.put("profileImage", "");
		}

		if (responseData.isEmpty()) {
			re.setData("");
			re.setMessage("Sorry, record not found");
		} else {
			re.setData(responseData);
			re.setMessage("Success");
		}

		return re;
	}

	// for updating profilepicture
	@PutMapping("/uploadProfilePicture")
	public ResponseEntity<ProfileImage> uploadProfileImage(@RequestParam("file") MultipartFile file,
			Authentication authentication) {
		String email = authentication.getName();
		User user = userRepo.findByEmail(email);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		try {
			ProfileImage profileImage = profileImageService.uploadProfileImage(file, user.getUserId());
			logAuditAction("User Profile Picture Updated", email, LocalDateTime.now(), user.getUserId(), "User Profile Picture Updated with ID: " + user.getUserId());
			return ResponseEntity.ok(profileImage);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}

	}
	
	// Getting profile data to myprofile
	@GetMapping("/profile")
	public ResponseEntity<ResponseMessage> getUserProfile(Authentication authentication) {
		ResponseMessage re = new ResponseMessage();

		String email = authentication.getName();
		User user = userRepo.findByEmail(email);

		if (user == null) {
			re.setData("");
			re.setMessage("User not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(re);
		}

		Optional<ProfileImage> profileImageOptional = profileImageRepository.findByUserUserId(user.getUserId());
		Map<String, Object> responseData = new HashMap<>();

		if (user != null) {
			responseData.put("user", user);
		}

		if (profileImageOptional.isPresent()) {
			ProfileImage profileImg = profileImageOptional.get();
			String imageName = profileImg.getProfileImage();
			String imagePath = "C:\\Users\\rakes\\eclipse-workspace\\VrikshaGyanServer\\VrikshaGyanServer\\src\\main\\resources\\static\\profileImages\\" + imageName;
			try {

		            byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
		            String base64Image = Base64Utils.encodeToString(imageBytes);
		            responseData.put("profileImage", base64Image);
			}catch(Exception e){
				responseData.put("profileImage", "");
			}
			//responseData.put("profileImage", profileImg); // Assuming this is the image path
		} else {
			responseData.put("profileImage", ""); // No image found
		}

		if (responseData.isEmpty()) {
			re.setData("");
			re.setMessage("Sorry, record not found");
		} else {
			re.setData(responseData);
			re.setMessage("Success");
		}

		return ResponseEntity.ok(re);
	}

	// Changing Password inside the profile
	@PostMapping("/changePassword")
	public ResponseEntity<Map<String, String>> changePassword(@RequestBody Map<String, String> passwordData,
			Authentication authentication) {
		String email = authentication.getName();
		User user = userRepo.findByEmail(email);

		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}

		String currentPassword = passwordData.get("currentPassword");
		String newPassword = passwordData.get("newPassword");

		if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
			Map<String, String> response = new HashMap<>();
			response.put("message", "Current password is incorrect");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		user.setPassword(passwordEncoder.encode(newPassword));
		userRepo.save(user);
		logAuditAction("User password Updated", email, LocalDateTime.now(), user.getUserId(), "User Password Updated with ID: " + user.getUserId());
		Map<String, String> response = new HashMap<>();
		response.put("message", "Password changed successfully");
		return ResponseEntity.ok(response);
	}

	// undo delete
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("/admin/restore/{userId}")
	public ResponseEntity<Object> restoreUser(@PathVariable Long userId, Authentication authen) {
		String email = authen.getName();
		Optional<User> userOptional = userRepo.findById(userId);
		if (!userOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		User user = userOptional.get();
		user.setDeleted(false);
		userRepo.save(user);
		logAuditAction("Deleted User Restored", email, LocalDateTime.now(), user.getUserId(), "Deleted User Restored with ID: " + user.getUserId());
		return ResponseEntity.ok().build();
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/admin/count")
	public ResponseEntity<Long> getUserCount() {
		long userCount = userRepo.countUsers();
		return ResponseEntity.ok(userCount);
	}
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/admin/verified")
	public ResponseEntity<Long> getUserVerified() {
		long userCount = userRepo.countVerifiedUsers();
		return ResponseEntity.ok(userCount);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/admin/unverified")
	public ResponseEntity<Long> getUserNonVerified() {
		long userCount = userRepo.countUnverifiedUsers();
		return ResponseEntity.ok(userCount);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/admin/activeUser")
	public ResponseEntity<Long> getActiveUser() {
		long userCount = userRepo.countActiveUsers();
		return ResponseEntity.ok(userCount);
	}
	
	 // Method to log audit actions
    private void logAuditAction(String action, String performedBy, LocalDateTime performedAt, Long affectedRecordId, String details) {
        UserAuditLog auditLog = new UserAuditLog();
        System.out.println(auditLog);
        auditLog.setAction(action);
        auditLog.setPerformedBy(performedBy);
        auditLog.setPerformedAt(performedAt);
        auditLog.setAffectedRecordId(affectedRecordId);
        auditLog.setDetails(details);
        userAuditRepo.save(auditLog);
    }
		
    @PutMapping("/uploadProfilePictures")
    public ResponseEntity<String> uploadProfileImages(@RequestParam("base64Image") String base64Image,
            Authentication authentication) {
        if (base64Image == null || base64Image.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No image data provided");
        }
        String email = authentication.getName();
        User user = userRepo.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        String fileName = user.getFirstName() + "_" + user.getUserId() + ".jpg";
        String directoryPath = "src/main/resources/static/profileImages/"; // Ensure this is correct
        String imagePath = directoryPath + fileName;
        try {
            if (base64Image.contains("data:image/jpeg;base64,")) {
                base64Image = base64Image.split(",")[1];
            }
            base64Image = base64Image.replaceAll("[^A-Za-z0-9+/=]", ""); // Remove non-base64 characters
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(bis);
            if (image == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid image data");
            }
            Optional<ProfileImage> existingProfileImage = profileImageRepository.findByUserUserId(user.getUserId());
            if (existingProfileImage.isPresent()) {
                String oldFileName = existingProfileImage.get().getProfileImage();
                deleteFile(directoryPath + oldFileName);
                profileImageRepository.delete(existingProfileImage.get());
            }
            File outputfile = new File(imagePath);
            if (!outputfile.getParentFile().exists()) {
                outputfile.getParentFile().mkdirs();
            }
            ImageIO.write(image, "jpg", outputfile);
            // Save new profile image info to the database
            ProfileImage profileImage = new ProfileImage();
            profileImage.setUser(user);
            profileImage.setProfileImage(fileName);
            profileImageRepository.save(profileImage);
            return ResponseEntity.ok("Profile image uploaded successfully");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid base64 input");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving image");
        }
    }
		private void deleteFile(String fileName) {
		    if (fileName != null && !fileName.isEmpty()) {
		        Path filePath = Paths.get(directoryPath).resolve(fileName).normalize();
		        File file = filePath.toFile();
		        System.out.println("Attempting to delete file: " + filePath.toAbsolutePath());
		        
		        if (file.exists()) {
		            if (file.delete()) {
		                System.out.println("File deleted successfully: " + filePath.toAbsolutePath());
		            } else {
		                System.out.println("Failed to delete file: " + filePath.toAbsolutePath());
		            }
		        } else {
		            System.out.println("File does not exist: " + filePath.toAbsolutePath());
		        }
		    } else {
		        System.out.println("Invalid file name provided for deletion: " + fileName);
		    }
		}

	
}
