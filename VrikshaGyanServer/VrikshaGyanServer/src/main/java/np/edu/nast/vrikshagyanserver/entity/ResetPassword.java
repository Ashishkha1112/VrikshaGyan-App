package np.edu.nast.vrikshagyanserver.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPassword {
	private String email;
	private String password;
}
