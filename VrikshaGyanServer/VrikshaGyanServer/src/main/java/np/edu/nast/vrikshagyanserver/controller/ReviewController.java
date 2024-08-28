package np.edu.nast.vrikshagyanserver.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import np.edu.nast.vrikshagyanserver.entity.Review;
import np.edu.nast.vrikshagyanserver.entity.User;
import np.edu.nast.vrikshagyanserver.entity.plants.Plants;
import np.edu.nast.vrikshagyanserver.repository.PlantRepository;
import np.edu.nast.vrikshagyanserver.repository.ReviewRepository;
import np.edu.nast.vrikshagyanserver.repository.UserRepository;

@RestController
@RequestMapping("/api")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlantRepository plantsRepository;

    @PostMapping("/comment")
    public ResponseEntity<String> addComment(
            @RequestParam Long plantId,
            @RequestBody Review review) {
    	System.out.println(plantId);

        try {
            // Validate plantId and review
            if (plantId == null || review == null) {
                return ResponseEntity.badRequest().body("Invalid input: plantId or review is null");
            }

            // Extract the user from the security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByEmail(username);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            // Find the plant by ID
            Plants plant = plantsRepository.findById(plantId).orElse(null);
            if (plant == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Plant not found");
            }

            // Set user and plant on the review
            review.setUser(user);
            review.setPlant(plant);

            // Save the review
            reviewRepository.save(review);
            return ResponseEntity.ok("Comment added successfully");

        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @GetMapping("/comments/{plantId}")
    public ResponseEntity<?> getCommentsByPlantId(@PathVariable Long plantId) {
    	System.out.println("plants "+plantId);
        try {
            // Validate plantId
            if (plantId == null) {
                return ResponseEntity.badRequest().body("Invalid plantId");
            }

            // Find the plant by ID
            Plants plant = plantsRepository.findById(plantId).orElse(null);
            if (plant == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Plant not found");
            }

            // Retrieve the comments for the plant
            List<Review> reviews = reviewRepository.findByPlant(plant);

            // Map the reviews to a List of Maps containing fullName and comment
            List<Map<String, Object>> response = reviews.stream()
                    .map(review -> {
                        Map<String, Object> map = new HashMap<>();
                        User user = review.getUser();
                        String fullName = String.format("%s %s %s",
                                user.getFirstName(),
                                user.getMiddleName() != null ? user.getMiddleName() : "",
                                user.getLastName()).trim();
                        map.put("userName", fullName);
                        map.put("comment", review.getComment());
                        return map;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

}
