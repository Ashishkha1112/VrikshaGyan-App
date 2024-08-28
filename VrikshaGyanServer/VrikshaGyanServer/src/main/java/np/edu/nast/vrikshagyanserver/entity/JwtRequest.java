package np.edu.nast.vrikshagyanserver.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class JwtRequest {

    private String username;
    private String password;
}
