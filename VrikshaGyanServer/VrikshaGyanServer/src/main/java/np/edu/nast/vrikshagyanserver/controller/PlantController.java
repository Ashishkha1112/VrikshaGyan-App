package np.edu.nast.vrikshagyanserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.util.Base64Utils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import np.edu.nast.vrikshagyanserver.entity.AuditLog;
import np.edu.nast.vrikshagyanserver.entity.ProfileImage;
import np.edu.nast.vrikshagyanserver.entity.User;
import np.edu.nast.vrikshagyanserver.entity.plants.PlantImage;
import np.edu.nast.vrikshagyanserver.entity.plants.Plants;
import np.edu.nast.vrikshagyanserver.repository.AuditRepository;
import np.edu.nast.vrikshagyanserver.repository.PlantImageRepository;
import np.edu.nast.vrikshagyanserver.repository.PlantRepository;
import np.edu.nast.vrikshagyanserver.repository.UserRepository;
import np.edu.nast.vrikshagyanserver.response.ResponseMessage;
import np.edu.nast.vrikshagyanserver.service.FileStorageService;

import java.nio.file.*;
import java.time.LocalDateTime;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

@RequestMapping("/api")
@CrossOrigin("*")
@RestController
public class PlantController {

	@Autowired
	private PlantRepository plantsRepository;
	
	@Autowired
	private AuditRepository auditLogRepository;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private FileStorageService fileStorageService;
	  private static final Path IMAGES_DIRECTORY = Paths.get("C:\\Users\\rakes\\eclipse-workspace\\VrikshaGyanServer\\VrikshaGyanServer\\src\\main\\resources\\static");
	    private static final long MAX_IMAGE_SIZE_BYTES = 1 * 1024 * 1024; // 1 MB
	    static {
	        try {
	            Files.createDirectories(IMAGES_DIRECTORY);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	public PlantController(FileStorageService fileStorageService) {
		this.fileStorageService = fileStorageService;
	}

	@PostMapping(value = "/plants/single", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> createPlant(@Valid Plants plant, BindingResult bindingResult,
			@RequestParam("images") List<MultipartFile> images, Authentication authen) {
		String email = authen.getName();
		// Check for validation errors
		if (bindingResult.hasErrors()) {
			List<String> validationErrors = new ArrayList<>();
			bindingResult.getFieldErrors()
					.forEach(error -> validationErrors.add(error.getField() + ": " + error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(validationErrors);
		}

		try {
			List<String> imagePaths = new ArrayList<>();
			for (int i = 0; i < images.size(); i++) {
				MultipartFile image = images.get(i);
				String imagePath = fileStorageService.storeFile(image, plant.getEnglishName(), i + 1);
				imagePaths.add(imagePath);
			}

			plant.setImagePaths(imagePaths);

			Plants savedPlant = plantsRepository.save(plant);
			logAuditAction("Created", email, LocalDateTime.now(), savedPlant.getPlantId(), "Plant updated with ID: " + savedPlant.getPlantId());
			return ResponseEntity.ok(savedPlant);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to store images.");
		}
	}

	@PostMapping(value = "/plants/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//	public void uploadPlantsAndImages(@RequestParam("csvFile") MultipartFile csvFile,
//			@RequestParam("imageFile") MultipartFile imageFile, RedirectAttributes redirectAttributes) {
//		if (csvFile.isEmpty() || imageFile.isEmpty()) {
//			redirectAttributes.addFlashAttribute("message", "Please select both CSV and ZIP/RAR files.");
//			return;
//		}
//
//		try {
//			// Save plant data from CSV
//			Map<String, Plants> englishNameToPlantMap = savePlantsFromCsv(csvFile.getInputStream());
//
//			// Extract and save images from ZIP file to the specified directory
//			extractAndSaveImages(imageFile.getInputStream(),
//					"C:\\Users\\rakes\\eclipse-workspace\\VrikshaGyanServer\\VrikshaGyanServer\\src\\main\\resources\\static",
//					englishNameToPlantMap);
//
//			// Save all plants with updated image paths
//			plantsRepository.saveAll(englishNameToPlantMap.values());
//		//	logAuditAction("create", englishNameToPlantMap.getCreatedBy(), plant.getCreatedDate(), savedPlant.getPlantId(), "Plant created with ID: " + savedPlant.getPlantId());
//
//			redirectAttributes.addFlashAttribute("message", "Plants and images uploaded successfully!");
//		} catch (IOException e) {
//			e.printStackTrace();
//			redirectAttributes.addFlashAttribute("message", "Failed to upload plants and images: " + e.getMessage());
//		}
//	}
	public ResponseEntity<String> uploadPlantsAndImages(@RequestParam("csvFile") MultipartFile csvFile,
			@RequestParam("imageFile") MultipartFile imageFile) {
		if (csvFile.isEmpty() || imageFile.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please select both CSV and ZIP/RAR files.");
		}

		try {
			// Save plant data from CSV
			Map<String, Plants> englishNameToPlantMap = savePlantsFromCsv(csvFile.getInputStream());

			// Extract and save images from ZIP file to the specified directory
			extractAndSaveImages(imageFile.getInputStream(),
					"C:\\Users\\rakes\\eclipse-workspace\\VrikshaGyanServer\\VrikshaGyanServer\\src\\main\\resources\\static",
					englishNameToPlantMap);

			// Save all plants with updated image paths
			plantsRepository.saveAll(englishNameToPlantMap.values());

			return ResponseEntity.ok("Plants and images uploaded successfully!");
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to upload plants and images: " + e.getMessage());
		}
	}

	// Helper method to save plant data from CSV file and return a map of English
	// name to Plants
	private Map<String, Plants> savePlantsFromCsv(InputStream inputStream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		Map<String, Plants> englishNameToPlantMap = new HashMap<>();
		String line;
		boolean firstLine = true; // Flag to skip the header line

		while ((line = reader.readLine()) != null) {
			if (firstLine) {
				firstLine = false;
				continue; // Skip header line
			}

			String[] data = line.split(",");
			if (data.length > 0) {
				String englishName = data[0].trim();
				// Check if a plant with the same English name already exists
				if (!plantsRepository.existsByEnglishName(englishName)) {
					Plants plant = new Plants();
					// Set plant data from CSV columns
					plant.setEnglishName(englishName);
					plant.setNepaliName(getSafeValue(data, 1));
					plant.setTharuName(getSafeValue(data, 2));
					plant.setLocalName(getSafeValue(data, 3));
					plant.setScientificName(getSafeValue(data, 4));
					plant.setPlantCategory(getSafeValue(data, 5));
					plant.setPartUsed(getSafeValue(data, 6));
					plant.setNormalUses(getSafeValue(data, 7));
					plant.setTraditionalUse(getSafeValue(data, 8));
					plant.setMedicalUses(getSafeValue(data, 9));
					plant.setPreparationType(getSafeValue(data, 10));
					plant.setPlantHeight(getSafeValue(data, 11));
					plant.setDescription(getSafeValue(data, 12));
					plant.setStatus(1);
					plant.setDeleted(false);
	
					englishNameToPlantMap.put(englishName, plant);
				}
			}
		}
		return englishNameToPlantMap;
	}

	private String getSafeValue(String[] data, int index) {
		return index < data.length ? data[index].trim() : "";
	}

	private void extractAndSaveImages(InputStream inputStream, String targetDirectory,
			Map<String, Plants> englishNameToPlantMap) throws IOException {
		try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
			ZipEntry entry;
			Map<String, Integer> englishNameCountMap = new HashMap<>();

			while ((entry = zipInputStream.getNextEntry()) != null) {
				if (!entry.isDirectory()) {
					String entryName = entry.getName();
					Path fileName = Paths.get(entryName).getFileName();

					// Determine the English name from the entry name
					String[] parts = entryName.split("/");
					if (parts.length >= 2) {
						String englishName = parts[1]; // Assuming the second part as the English name folder

						// Increment the count for the English name
						englishNameCountMap.put(englishName, englishNameCountMap.getOrDefault(englishName, 0) + 1);
						int imageNumber = englishNameCountMap.get(englishName);

						// Create a new file name with English name and image number
						String newFileName = englishName + "_" + imageNumber + "."
								+ getFileExtension(fileName.toString());
						Path filePath = Paths.get(targetDirectory, newFileName);
						Files.createDirectories(filePath.getParent());

						// Save and resize image
						resizeAndSaveImage(zipInputStream, filePath);

						System.out.println("Extracted: " + filePath.toString());

						// Store the image path relative to the target directory
						String relativePath = filePath.toString().replace(targetDirectory + File.separator, "");

						// Find the matching plant by English name and add image path
						Plants plant = englishNameToPlantMap.get(englishName);
						if (plant != null) {
							if (plant.getImagePaths() == null) {
								plant.setImagePaths(new ArrayList<>());
							}
							plant.getImagePaths().add(relativePath);
						}
					}
				}
				zipInputStream.closeEntry();
			}
		}
	}

	private String getFileExtension(String fileName) {
		int lastIndexOfDot = fileName.lastIndexOf(".");
		if (lastIndexOfDot == -1) {
			return ""; // Empty extension
		}
		return fileName.substring(lastIndexOfDot + 1);
	}

	// Helper method to resize and save image
	private void resizeAndSaveImage(InputStream inputStream, Path filePath) throws IOException {
		Path tempFile = Files.createTempFile("tempImage", ".tmp");
		Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);

		// Check if the image size is greater than 1 MB
		if (Files.size(tempFile) > 1024 * 1024) {
			// Resize the image to reduce its size
			Thumbnails.of(tempFile.toFile()).size(1024, 1024) // Resize to maximum 1024x1024 dimensions
					.outputQuality(0.9) // Reduce quality to 90% to reduce file size
					.toFile(filePath.toFile());
		} else {
			// If file size is within limit, just save it
			Files.move(tempFile, filePath, StandardCopyOption.REPLACE_EXISTING);
		}

		Files.deleteIfExists(tempFile);
	}

	@GetMapping("/plants/list")
	public ResponseEntity<List<Plants>> getAllPlants() {
		List<Plants> plants = plantsRepository.findByisDeleted(false);
		return ResponseEntity.ok(plants);
	}

	@GetMapping("/plants/deleted")
	public ResponseEntity<List<Plants>> getAllDeletedPlants() {
		List<Plants> plants = plantsRepository.findByisDeleted(true);
		return ResponseEntity.ok(plants);
	}

	// search plant by id
	@GetMapping("/plants/editPlant/{id}")
	public ResponseMessage findplant(@PathVariable("id") Long id) {
		ResponseMessage re = new ResponseMessage();
		
		String path ="C:/Users/rakes/eclipse-workspace/VrikshaGyanServer/VrikshaGyanServer/src/main/resources/static/";
		if (plantsRepository.findById(id).isPresent()) {
			Plants plant = plantsRepository.findById(id).get();
			  // Convert image paths to Base64
	        List<String> base64Images = new ArrayList<>();
	        for (String imagePath : plant.getImagePaths()) {
	            try {
	                String fullImagePath =  path + imagePath; // Adjust if necessary
	                byte[] imageBytes = Files.readAllBytes(Paths.get(fullImagePath));
	                String base64Image = Base64Utils.encodeToString(imageBytes);
	                base64Images.add(base64Image);
	            } catch (Exception e) {
	                e.printStackTrace(); // Handle exception properly
	                base64Images.add(""); // Handle failed encoding
	            }
	        }
	        plant.setImagePaths(base64Images);
			re.setData(plant);
			re.setMessage("Success");
			return re;
		}
		re.setData(null);
		re.setMessage("Sorry Record Not Found");

		return re;
	}

	//convert to base 64
	  public static String encodeFileToBase64(String imagePath) throws IOException {
	        Resource resource = new ClassPathResource(imagePath);
	        byte[] imageBytes = Files.readAllBytes(resource.getFile().toPath());
	        return Base64.getEncoder().encodeToString(imageBytes);
	    }
	// update plants
	@PutMapping("/plants/update/{plantId}")
	public ResponseEntity<?> updatePlant(@PathVariable Long plantId, @Valid @RequestBody Plants plantDetails,
			BindingResult bindingResult, Authentication authen) {
		 String email = authen.getName();
		// Validate input
		if (bindingResult.hasErrors()) {
			// Collect all validation errors into a list of strings
			List<String> validationErrors = bindingResult.getFieldErrors().stream()
					.map(error -> error.getDefaultMessage()).collect(Collectors.toList());

			// Return bad request status with validation error messages
			return ResponseEntity.badRequest().body(validationErrors);
		}

		Optional<Plants> optionalPlant = plantsRepository.findById(plantId);
		if (optionalPlant.isPresent()) {
			Plants plant = optionalPlant.get();
			plant.setEnglishName(plantDetails.getEnglishName());
			plant.setNepaliName(plantDetails.getNepaliName());
			plant.setTharuName(plantDetails.getTharuName());
			plant.setLocalName(plantDetails.getLocalName());
			plant.setScientificName(plantDetails.getScientificName());
			plant.setPlantCategory(plantDetails.getPlantCategory());
			plant.setPartUsed(plantDetails.getPartUsed());
			plant.setNormalUses(plantDetails.getNormalUses());
			plant.setTraditionalUse(plantDetails.getTraditionalUse());
			plant.setMedicalUses(plantDetails.getMedicalUses());
			plant.setPreparationType(plantDetails.getPreparationType());
			plant.setPlantHeight(plantDetails.getPlantHeight());
			plant.setDescription(plantDetails.getDescription());

			Plants updatedPlant = plantsRepository.save(plant);
			logAuditAction("Updated", email, LocalDateTime.now(), updatedPlant.getPlantId(), "Plant updated with ID: " + updatedPlant.getPlantId());
			return ResponseEntity.ok(updatedPlant);
		} else {
			// Return not found status if user does not exist
			return ResponseEntity.notFound().build();
		}
	}

	private Plants updatePlantStatus(Long plantId, Consumer<Plants> statusUpdater) {
	    Optional<Plants> optionalPlant = plantsRepository.findById(plantId);
	    if (!optionalPlant.isPresent()) {
	        return null;
	    }

	    Plants plant = optionalPlant.get();
	    statusUpdater.accept(plant);
	    return plantsRepository.save(plant);
	}
	
	@PutMapping("/plants/delete/{plantId}")
	public ResponseEntity<Object> markAsDeleted(@PathVariable Long plantId, Authentication authen) {
	    Plants updatedPlant = updatePlantStatus(plantId, plant -> plant.setDeleted(true));
	    String email = authen.getName();
	    if (updatedPlant == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Plant not found with ID: " + plantId);
	    }

	    // Log the audit action
	    logAuditAction("Deleted", email, LocalDateTime.now(), updatedPlant.getPlantId(), "Plant  deleted with ID: " + updatedPlant.getPlantId());

	    return  ResponseEntity.ok().build();
	}

	@PutMapping("/plant/verify/{plantId}")
	public ResponseEntity<Object> markAsVerified(@PathVariable Long plantId,Authentication authen) {
	    Plants updatedPlant = updatePlantStatus(plantId, plant -> plant.setStatus(1));
	    String email = authen.getName();
	    
	    if (updatedPlant == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Plant not found with ID: " + plantId);
	    }
	    // Log the audit action
	    logAuditAction("Verified", email, LocalDateTime.now(), updatedPlant.getPlantId(), "Plant  Verified with ID: " + updatedPlant.getPlantId());

	    return  ResponseEntity.ok().build();
	}

	// list all plants
	@GetMapping("/plants/unverifed")
	public ResponseEntity<List<Plants>> unverifiedPalnts() {
		List<Plants> plantsWithStatus0 = plantsRepository.findByStatusAndIsDeleted(0, false);
		return ResponseEntity.ok(plantsWithStatus0);

	}


	@GetMapping("/plants/verified")
	public ResponseEntity<List<Plants>> verifiedPlants() {
	    List<Plants> plantsWithStatus1 = plantsRepository.findByStatusAndIsDeleted(1, false);
	    String path = "C:/Users/rakes/eclipse-workspace/VrikshaGyanServer/VrikshaGyanServer/src/main/resources/static/";

	    for (Plants plant : plantsWithStatus1) {
	        List<String> base64Images = new ArrayList<>();
	        
	        if (plant.getImagePaths() != null && !plant.getImagePaths().isEmpty()) {
	            for (String imagePath : plant.getImagePaths()) {
	                try {
	                    String fullImagePath = path + imagePath; // For each image path
	                    byte[] imageBytes = Files.readAllBytes(Paths.get(fullImagePath));
	                    String base64Image = Base64Utils.encodeToString(imageBytes);
	                    base64Images.add(base64Image); 
	                } catch (Exception e) {
	                    e.printStackTrace(); 
	                    base64Images.add(""); 
	                }
	            }
	            plant.setImagePaths(base64Images); 
	        }
	    }

	    return ResponseEntity.ok(plantsWithStatus1);
	}


	
	@GetMapping("/plants/count")
	public ResponseEntity<Long> getPlantCount() {
		long plantcount = plantsRepository.countTotalPlants();
		return ResponseEntity.ok(plantcount);
	}
	
	@GetMapping("/plants/verifiedcount")
	public ResponseEntity<Long> getVerifiedPlant() {
		long plantcount = plantsRepository.countVerifiedPlants();
		return ResponseEntity.ok(plantcount);
	}
	
	
	@GetMapping("/plants/unverifiedcount")
	public ResponseEntity<Long> getUnVerifiedPla() {
		long plantcount = plantsRepository.countUnverifiedPlants();
		return ResponseEntity.ok(plantcount);
	}
	
	//for android
	@GetMapping("/plants/editPlantByName/{englishName}")
	public ResponseEntity<ResponseMessage> findPlantByEnglishName(@PathVariable("englishName") String englishName) {
	    ResponseMessage responseMessage = new ResponseMessage();

	    try {
	        if (englishName == null || englishName.trim().isEmpty()) {
	            responseMessage.setData(null);
	            responseMessage.setMessage("English name cannot be null or empty");
	            return ResponseEntity.badRequest().body(responseMessage);
	        }
	        String path ="C:/Users/rakes/eclipse-workspace/VrikshaGyanServer/VrikshaGyanServer/src/main/resources/static/";
	        Plants plant = plantsRepository.findByEnglishName(englishName);

	        if (plant != null) {
	        	List<String> base64Images = new ArrayList<>();
		        for (String imagePath : plant.getImagePaths()) {
		            try {
		                String fullImagePath =  path + imagePath; // Adjust if necessary
		                byte[] imageBytes = Files.readAllBytes(Paths.get(fullImagePath));
		                String base64Image = Base64Utils.encodeToString(imageBytes);
		                base64Images.add(base64Image);
		            } catch (Exception e) {
		                e.printStackTrace(); // Handle exception properly
		                base64Images.add(""); // Handle failed encoding
		            }
		        }
		        plant.setImagePaths(base64Images);
	            responseMessage.setData(plant);
	            responseMessage.setMessage("Success");
	            return ResponseEntity.ok(responseMessage);
	        } else {
	            responseMessage.setData(null);
	            responseMessage.setMessage("Sorry, record not found");
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
	        }
	    } catch (Exception e) {
	        responseMessage.setData(null);
	        responseMessage.setMessage("An error occurred while processing your request");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
	    }
	}


	 // Method to log audit actions
    private void logAuditAction(String action, String performedBy, LocalDateTime performedAt, Long affectedRecordId, String details) {
        AuditLog auditLog = new AuditLog();
        System.out.println(auditLog);
        auditLog.setAction(action);
        auditLog.setPerformedBy(performedBy);
        auditLog.setPerformedAt(performedAt);
        auditLog.setAffectedRecordId(affectedRecordId);
        auditLog.setDetails(details);
        auditLogRepository.save(auditLog);
    }
  
//    @PostMapping("/single")
//    public ResponseEntity<String> savePlant(@RequestBody Plants plant) {
//        try {
//            // Extract plant data
//            String englishName = plant.getEnglishName();
//            List<String> imagePaths = plant.getImagePaths();
//
//            // Validate required fields
//            if (englishName == null || englishName.isEmpty() || imagePaths == null || imagePaths.isEmpty()) {
//                return new ResponseEntity<>("Required fields are missing", HttpStatus.BAD_REQUEST);
//            }
//
//            // Process images and generate file names
//            List<String> savedImagePaths = new ArrayList<>();
//            for (int i = 0; i < imagePaths.size(); i++) {
//                String base64Image = imagePaths.get(i);
//                String imageName = generateImageName(englishName, i + 1);
//                Path filePath = IMAGES_DIRECTORY.resolve(imageName);
//                processImage(base64Image, filePath);
//                savedImagePaths.add(imageName); // Add image name to list
//            }
//
//            // Save plant data to database
//            Plants plantToSave = new Plants();
//            plantToSave.setEnglishName(plant.getEnglishName());
//            plantToSave.setNepaliName(plant.getNepaliName());
//            plantToSave.setTharuName(plant.getTharuName());
//            plantToSave.setLocalName(plant.getLocalName());
//            plantToSave.setScientificName(plant.getScientificName());
//            plantToSave.setPlantCategory(plant.getPlantCategory());
//            plantToSave.setNormalUses(plant.getNormalUses());
//            plantToSave.setMedicalUses(plant.getMedicalUses());
//            plantToSave.setTraditionalUse(plant.getTraditionalUse());
//            plantToSave.setPartUsed(plant.getPartUsed());
//            plantToSave.setPreparationType(plant.getPreparationType());
//            plantToSave.setDescription(plant.getDescription());
//            plantToSave.setPlantHeight(plant.getPlantHeight());
//            plantToSave.setStatus(plant.getStatus());
//            plantToSave.setDeleted(plant.isDeleted());
//            plantToSave.setImagePaths(savedImagePaths); // Set the list of saved image paths
//
//            plantsRepository.save(plantToSave);
//
//            return new ResponseEntity<>("Plant saved successfully!", HttpStatus.OK);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return new ResponseEntity<>("Error processing plant data", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    private void processImage(String base64Image, Path filePath) throws IOException {
//        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
//        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes)) {
//            resizeAndSaveImage(byteArrayInputStream, filePath);
//        }
//    }
//
//    private void resizeAndSaveImage(ByteArrayInputStream inputStream, Path filePath) throws IOException {
//        Path tempFile = Files.createTempFile("tempImage", ".tmp");
//        Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
//
//        if (Files.size(tempFile) > MAX_IMAGE_SIZE_BYTES) {
//            // Resize the image if it exceeds the maximum size
//            Thumbnails.of(tempFile.toFile())
//                    .size(1024, 1024) // Resize to maximum 1024x1024 dimensions
//                    .outputQuality(0.7) // Reduce quality to 70%
//                    .toFile(filePath.toFile());
//        } else {
//            // If the file size is within limit, just save it
//            Files.move(tempFile, filePath, StandardCopyOption.REPLACE_EXISTING);
//        }
//
//        Files.deleteIfExists(tempFile);
//    }
//
//    private String generateImageName(String plantName, int index) {
//        // Replace any non-alphanumeric characters in plantName with underscores
//        String safePlantName = plantName.replaceAll("[^a-zA-Z0-9]", "_");
//        return String.format("%s_%d.jpg", safePlantName, index);
//    }
    @PostMapping("/single")
    public ResponseEntity<String> savePlant(@RequestBody Plants plant) {
        try {
            // Extract plant data
            String englishName = plant.getEnglishName();
            List<String> imagePaths = plant.getImagePaths();

            // Validate required fields
            if (englishName == null || englishName.isEmpty() || imagePaths == null || imagePaths.isEmpty()) {
                return new ResponseEntity<>("Required fields are missing", HttpStatus.BAD_REQUEST);
            }

            // Ensure the images directory exists
            if (!Files.exists(IMAGES_DIRECTORY)) {
                Files.createDirectories(IMAGES_DIRECTORY);
                System.out.println("Created directory: " + IMAGES_DIRECTORY.toAbsolutePath());
            }

            // Process images and generate file names
            List<String> savedImagePaths = new ArrayList<>();
            for (int i = 0; i < imagePaths.size(); i++) {
                String base64Image = imagePaths.get(i);
                String imageName = generateImageName(englishName, i + 1);
                Path filePath = IMAGES_DIRECTORY.resolve(imageName);
                System.out.println("Processing image: " + imageName);

                try {
                    processImage(base64Image, filePath);
                    savedImagePaths.add(imageName); // Add image name to list
                    System.out.println("Saved image: " + filePath.toAbsolutePath());
                } catch (IOException e) {
                    System.err.println("Error processing image: " + imageName);
                    e.printStackTrace();
                }
            }

            // Save plant data to database
            Plants plantToSave = new Plants();
            plantToSave.setEnglishName(plant.getEnglishName());
            plantToSave.setNepaliName(plant.getNepaliName());
            plantToSave.setTharuName(plant.getTharuName());
            plantToSave.setLocalName(plant.getLocalName());
            plantToSave.setScientificName(plant.getScientificName());
            plantToSave.setPlantCategory(plant.getPlantCategory());
            plantToSave.setNormalUses(plant.getNormalUses());
            plantToSave.setMedicalUses(plant.getMedicalUses());
            plantToSave.setTraditionalUse(plant.getTraditionalUse());
            plantToSave.setPartUsed(plant.getPartUsed());
            plantToSave.setPreparationType(plant.getPreparationType());
            plantToSave.setDescription(plant.getDescription());
            plantToSave.setPlantHeight(plant.getPlantHeight());
            plantToSave.setStatus(plant.getStatus());
            plantToSave.setDeleted(plant.isDeleted());
            plantToSave.setImagePaths(savedImagePaths); // Set the list of saved image paths

            // Assuming plantsRepository is properly initialized
            plantsRepository.save(plantToSave);

            return new ResponseEntity<>("Plant saved successfully!", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error processing plant data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void processImage(String base64Image, Path filePath) throws IOException {
        if (base64Image == null || base64Image.isEmpty()) {
            throw new IllegalArgumentException("Base64 image string is empty");
        }

        byte[] imageBytes;
        try {
            imageBytes = Base64.getDecoder().decode(base64Image);
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid Base64 encoding", e);
        }

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes)) {
            BufferedImage image = ImageIO.read(byteArrayInputStream);
            if (image == null) {
                throw new IOException("Failed to decode image from base64");
            }

            resizeAndSaveImage(image, filePath);
        }
    }


    private void resizeAndSaveImage(BufferedImage image, Path filePath) throws IOException {
        // Create a temporary file
        Path tempFilePath = Files.createTempFile("tempImage", ".jpg");
        
        // Write the original image to the temporary file
        ImageIO.write(image, "jpg", tempFilePath.toFile());

        // Check the size of the temporary file
        long tempFileSize = Files.size(tempFilePath);
        
        BufferedImage imageToSave = image;
        
        // Resize if the image is larger than the maximum size
        if (tempFileSize > MAX_IMAGE_SIZE_BYTES) {
            System.out.println("Resizing image: " + filePath.toAbsolutePath());
            imageToSave = Thumbnails.of(image)
                    .size(1024, 1024) // Resize to maximum 1024x1024 dimensions
                    .outputQuality(0.7) // Reduce quality to 70%
                    .asBufferedImage();
        }

        // Write the (possibly resized) image to the final file
        File outputFile = filePath.toFile();
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
            System.out.println("Created directory: " + outputFile.getParentFile().getAbsolutePath());
        }
        
        ImageIO.write(imageToSave, "jpg", outputFile);

        // Delete the temporary file
        Files.delete(tempFilePath);
    }


    private String generateImageName(String plantName, int index) {
        // Replace any non-alphanumeric characters in plantName with underscores
        String safePlantName = plantName.replaceAll("[^a-zA-Z0-9]", "_");
        return String.format("%s_%d.jpg", safePlantName, index);
    }
}
