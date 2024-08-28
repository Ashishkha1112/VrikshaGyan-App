package np.edu.nast.vrikshagyanserver.entity;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User implements UserDetails {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column(nullable = false)
	@NotBlank(message = "First name is mandatory")
	@Pattern(regexp="^[A-Za-z]*$",message = "Invalid Input")
	@Size(min = 2,max = 30, message = "First name cannot be longer than 30 characters")
	private String firstName;

//	@Pattern(regexp="^[A-Za-z]*$",message = "Invalid Input")
	@Column(nullable = true)
	private String middleName;

	@Pattern(regexp="^[A-Za-z]*$",message = "Invalid Input")
	@Column(nullable = false)
	@NotBlank(message = "Last name is mandatory")
	@Size(min = 2,max = 30, message = "Last name cannot be longer than 30 characters")
	private String lastName;

	@Embedded
	private Address address;

	@Column(unique = true, nullable = false, length = 10)
	@NotBlank(message = "Phone number is mandatory")
	@Digits(integer =10, fraction =0, message="Only contains Number")
    @Size(max = 10, message = "Phone number cannot be longer than 10 characters")
	private String phone;

	@Column(unique = true, nullable = false)
	@NotBlank(message = "Email is mandatory")
    @Size(max = 100, message = "Email cannot be longer than 100 characters")
	private String email;

	@Column(nullable = false)
	@NotBlank(message = "Password is mandatory")
	private String password;

//	@Pattern(regexp="^[A-Za-z]*$",message = "Invalid Input")
	@Column(nullable = false)
	@NotBlank(message = "Occupation is mandatory")
    @Size(max = 50, message = "Occupation cannot be longer than 50 characters")
	private String occupation;

	@Enumerated(EnumType.STRING)
    @Column(length = 20)
	private Role role;

	private boolean status;
	
	private boolean isDeleted;
 	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return status;
	}
}
