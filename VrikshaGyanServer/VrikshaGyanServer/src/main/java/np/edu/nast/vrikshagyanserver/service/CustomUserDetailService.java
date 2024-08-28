package np.edu.nast.vrikshagyanserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import np.edu.nast.vrikshagyanserver.entity.User;
import np.edu.nast.vrikshagyanserver.repository.UserRepository;

@Service
@CrossOrigin(origins = "*")
public class CustomUserDetailService implements UserDetailsService {

	@Autowired
	private UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		  User user = userRepo.findByEmail(email);
	        if (user == null) {
	            throw new UsernameNotFoundException("User not found with email: " + email);
	        }
	        return user;
	}
	  public void saveUser(User user) {
	        userRepo.save(user);
	    }
} 