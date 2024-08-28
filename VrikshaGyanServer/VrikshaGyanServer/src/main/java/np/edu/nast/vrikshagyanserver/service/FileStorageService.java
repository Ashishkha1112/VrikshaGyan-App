//package np.edu.nast.vrikshagyanserver.service;
//
//import net.coobird.thumbnailator.Thumbnails;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import np.edu.nast.vrikshagyanserver.repository.PlantRepository;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//@Service
//public class FileStorageService {
//
//	private final String uploadDir = "src/main/resources/static/";
//
//    public FileStorageService(PlantRepository plantRepository) {
//        // Constructor injection if you plan to use PlantRepository in the future
//    }
//
//    public String storeFile(MultipartFile file, String englishName, int imageNumber) throws IOException {
//    	File fn = new File(uploadDir);
//		String fullPath=fn.getAbsolutePath();
//		
//        String fileExtension = getFileExtension(file.getOriginalFilename());
//        String fileName = englishName + "_" + imageNumber + "." + fileExtension;
//        Path uploadPath = Paths.get(fullPath);
//        
//        // Create upload directory if it doesn't exist
//        if (!Files.exists(uploadPath)) {
//            Files.createDirectories(uploadPath);
//        }
//
//        Path filePath = uploadPath.resolve(fileName);
//
//        // Resize image if necessary
//        resizeImageIfNeeded(file, filePath);
//
//        // Log the file path to ensure it's being saved correctly
//        System.out.println("File saved at: " + filePath.toString());
//
//        return filePath.toString();
//    }
//
//    private void resizeImageIfNeeded(MultipartFile file, Path filePath) throws IOException {
//        // Check if the file size is greater than 1 MB (you can adjust this limit as needed)
//        if (file.getSize() > 1024 * 1024) { // 1 MB = 1024 * 1024 bytes
//            // Resize the image to reduce its size
//            Thumbnails.of(file.getInputStream())
//                      .size(1024, 1024) // Resize to maximum 1024x1024 dimensions
//                      .outputQuality(0.9) // Reduce quality to 90% to reduce file size
//                      .toFile(filePath.toFile());
//        } else {
//            // If file size is within limit, just save it
//            Files.copy(file.getInputStream(), filePath);
//        }
//    }
//
//    private String getFileExtension(String filename) {
//        int dotIndex = filename.lastIndexOf('.');
//        if (dotIndex == -1) {
//            throw new IllegalArgumentException("No extension found for filename: " + filename);
//        }
//        return filename.substring(dotIndex + 1).toLowerCase();
//    }
//}
package np.edu.nast.vrikshagyanserver.service;

import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import np.edu.nast.vrikshagyanserver.repository.PlantRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    private final PlantRepository plantRepository;

    @Value("${file.upload-dir:src/main/resources/static/}")
    private String uploadDir;

    public FileStorageService(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }

    public String storeFile(MultipartFile file, String englishName, int imageNumber) {
        try {
            String fileExtension = getFileExtension(file.getOriginalFilename());
            String fileName = englishName + "_" + imageNumber + "." + fileExtension;
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

            // Create upload directory if it doesn't exist
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);

            // Resize image if necessary
            resizeImageIfNeeded(file, filePath);

            // Log the file path to ensure it's being saved correctly
            logger.info("File saved at: {}", filePath.toString());

            return fileName; // Return only the file name
        } catch (IOException ex) {
            logger.error("Error storing file", ex);
            throw new RuntimeException("Could not store file. Please try again!", ex);
        }
    }

    private void resizeImageIfNeeded(MultipartFile file, Path filePath) throws IOException {
        if (file.getSize() > 1024 * 1024) { // 1 MB = 1024 * 1024 bytes
            Thumbnails.of(file.getInputStream())
                      .size(1024, 1024)
                      .outputQuality(0.9)
                      .toFile(filePath.toFile());
        } else {
            Files.copy(file.getInputStream(), filePath);
        }
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1) {
            throw new IllegalArgumentException("No extension found for filename: " + filename);
        }
        return filename.substring(dotIndex + 1).toLowerCase();
    }
    

}
