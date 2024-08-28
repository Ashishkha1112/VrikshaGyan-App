	package np.edu.nast.vrikshagyanserver.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter

public class ResponseMessage {
	private String message;
	private Object data;
	
	   // Constructor with message
    public ResponseMessage(String message) {
        this.message = message;
    }
    // Constructor with message and data
  
  

}