package np.edu.nast.vrikshagyanserver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import np.edu.nast.vrikshagyanserver.entity.District;
import np.edu.nast.vrikshagyanserver.entity.Municipality;
import np.edu.nast.vrikshagyanserver.entity.Province;
import np.edu.nast.vrikshagyanserver.repository.DistrictRepository;
import np.edu.nast.vrikshagyanserver.repository.MunicipalityRepository;
import np.edu.nast.vrikshagyanserver.repository.ProvinceRepository;
import np.edu.nast.vrikshagyanserver.response.ResponseMessage;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins  = "*")
public class AddressController {
	
	@Autowired
	private ProvinceRepository provinceRepository;
	
	@Autowired	
	private DistrictRepository districtRepository;
	@Autowired   
	private MunicipalityRepository municipalityRepository;
	
//	//Listing Province
	  @GetMapping("/provinces")
	    public List<Province> getAllProvinces() {
	        return provinceRepository.findAll();
	    }
	  @PostMapping("/addProvince")
		public ResponseMessage addProvince(@RequestBody Province province) {
			
			System.out.println(province);
			ResponseMessage re = new ResponseMessage();
			re.setData(provinceRepository.save(province));
			re.setMessage("Successfully save");
			return re;
		}
	 
	  @GetMapping("/provinces/{id}")
		public List<District> getDistrictsByProvince(@PathVariable("id") Long id){
		  return districtRepository.findByProvinceProvinceId(id);
		 
		}
	  @PostMapping("/addDistrict")
		public ResponseMessage addDistrict(@RequestBody District district) {
			
			System.out.println(district);
			ResponseMessage re = new ResponseMessage();
			re.setData(districtRepository.save(district));
			re.setMessage("Successfully save");
			return re;
		}
	 
	  @GetMapping("/district/{id}")
		public List<Municipality> getMunicipalityByDistrict(@PathVariable("id") Long id){
		  return municipalityRepository.findByDistrictDistrictId(id);
		 
		}
	  @PostMapping("/addMunicipality")
		public ResponseMessage addMunicipality (@RequestBody Municipality municipality) {
			
			System.out.println(municipality);
			ResponseMessage re = new ResponseMessage();
			re.setData(municipalityRepository.save(municipality));
			re.setMessage("Successfully save");
			return re;
		}
}
