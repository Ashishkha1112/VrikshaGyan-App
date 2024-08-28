package np.edu.nast.vrikshagyanserver.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable

public class Address {
		@NotNull(message = "Province is Mandatory")
		private String province;
		@NotNull(message = "District is Mandatory")
	    private String district;
		@NotNull(message = "Municipality is Mandatory")
	    private String municipality;
		@NotNull(message = "Ward Number is Mandatory")
		@Digits(integer = 10, fraction = 0, message = "Ward Number must be a numeric value")
	    private Integer wardNumber;
		
		@Pattern(regexp="^[A-Za-z]*$",message = "Invalid Input")
		@NotNull(message = "Tole is Mandatory")
	    private String tole;
}