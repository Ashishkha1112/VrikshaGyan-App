package np.edu.nast.vrikshagyanserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import np.edu.nast.vrikshagyanserver.entity.Address;
import np.edu.nast.vrikshagyanserver.entity.Role;
import np.edu.nast.vrikshagyanserver.entity.User;
import np.edu.nast.vrikshagyanserver.repository.UserRepository;

@Component
public class DataLoader implements CommandLineRunner {
	  @Autowired
	    private UserRepository userRepository;

	    @Autowired
	    private BCryptPasswordEncoder passwordEncoder;

	    @Override
	    public void run(String... args) throws Exception {
	        if (userRepository.count() == 0) {
	        	
	        	 Address address = new Address();
	             address.setProvince("7");
	             address.setDistrict("69");
	             address.setMunicipality("742");
	             address.setWardNumber(1);
	             address.setTole("Tole 1");
	            User user = new User();
	            user.setFirstName("Admin");
	            user.setMiddleName("");
	            user.setLastName("User");
	            user.setAddress(address);
	            user.setPhone("9842335233");
	            user.setEmail("admin@vrikshagyan.com");
	            user.setPassword(passwordEncoder.encode("admin"));
	            user.setOccupation("Administrator");
	            user.setRole(Role.ADMIN);
	            user.setStatus(true);
	            user.setDeleted(false);
	            userRepository.save(user);
	        }
	    }
}
