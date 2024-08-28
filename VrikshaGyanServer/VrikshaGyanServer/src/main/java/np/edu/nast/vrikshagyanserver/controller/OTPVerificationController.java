package np.edu.nast.vrikshagyanserver.controller;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import jakarta.mail.internet.InternetAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import np.edu.nast.vrikshagyanserver.entity.OTPVerification;
import np.edu.nast.vrikshagyanserver.entity.User;
import np.edu.nast.vrikshagyanserver.repository.OTPVerificationRepository;
import np.edu.nast.vrikshagyanserver.repository.UserRepository;
import np.edu.nast.vrikshagyanserver.response.Sender;

@RestController
@RequestMapping("/api")
public class OTPVerificationController {

	@Autowired
    private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private OTPVerificationRepository otpVerificationRepository;

	@Autowired
	private UserRepository userRepository;

	private SecureRandom random = new SecureRandom();

	public String generateOTP() {
		StringBuilder otp = new StringBuilder(6);
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		for (int i = 0; i < 6; i++) {
			otp.append(characters.charAt(random.nextInt(characters.length())));
		}
		return otp.toString();
	}

	public boolean isValidEmail(String email) {
		try {
			new InternetAddress(email).validate();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@PostMapping("/send/{email}")
	public ResponseEntity<?> sendOTP(@PathVariable String email) {
		System.out.println(email);
		if (!isValidEmail(email)) {
			return ResponseEntity.badRequest().body("Invalid email format!");
		}

		User user = userRepository.findByEmail(email);
		if (user == null) {
			return ResponseEntity.notFound().build();
		}

		String otp = generateOTP();
		OTPVerification otpVerification = new OTPVerification();
		otpVerification.setOtp(otp);
		otpVerification.setEmail(email);

		String subject = "VrikshaGyan Verification Code";
		String body = "Dear VrikshaGyan User, \n\nYour verification code is: \n" + otp
				+ "\n\nThank you for using VrikshaGyan!";

		try {
			Sender.send(email, subject, body);
			otpVerificationRepository.save(otpVerification);
			// Return a JSON response with a success message
			return ResponseEntity.ok().body("{\"message\": \"Email sent successfully!\"}");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to send OTP: " + e.getMessage());
		}
	}

	@Scheduled(fixedRate = 60000)
	@Transactional
	public void cleanupExpiredOTPs() {
		LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(2);
		otpVerificationRepository.deleteByCreatedAtBefore(expiryTime);
	}
	@PostMapping("/verify")
    public ResponseEntity<String> verifyOTP(@RequestParam String email, @RequestParam String otp) {
        List<OTPVerification> otpVerifications = otpVerificationRepository.findByEmail(email);
        System.out.println(email + otp);

        if (!otpVerifications.isEmpty()) {
            boolean isValid = false;
            for (OTPVerification otpVerification : otpVerifications) {
                if (otpVerification.getOtp().equals(otp)) {
                    isValid = true;
                    otpVerificationRepository.deleteById(otpVerification.getId());
                    break;
                }
            }
            if (isValid) {
                return ResponseEntity.ok("OTP verified successfully");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("OTP not found for the provided email");
        }
    }
	
	 @PutMapping("/change")
	    @Transactional
	    public ResponseEntity<String> changePassword(@RequestBody Map<String, String> request) {
	        // Extract email and newPassword from the request
	        String email = request.get("email");
	        String newPassword = request.get("newPassword");

	        // Validate request parameters
	        if (email == null || email.isEmpty() || newPassword == null || newPassword.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email and new password must be provided.");
	        }
	       
	        // Retrieve user by email
	        User user = userRepository.findByEmail(email);
	        if (user == null) {
	        	 System.out.println("hello" +email + newPassword);
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with the provided email does not exist.");
	        }

	        // Update user password
	      
	        user.setPassword(passwordEncoder.encode(newPassword)); 
	        userRepository.save(user);
	        System.out.println("hello" +email + newPassword);
	        return ResponseEntity.ok("Password changed successfully.");
	    }
}
