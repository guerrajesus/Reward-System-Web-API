package reward.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import reward.system.controller.model.UserData;
import reward.system.service.RewardService;

@RestController 				 //expects and returns JSON in request/response bodies
@RequestMapping("/reward_system")//tell Spring URI for every HTTP request must start with 
@Slf4j 							 //Logger log.info for the console
public class RewardSystemController {
	
	@Autowired
	private RewardService rewardService;
	
	
	@PostMapping("/user")
	@ResponseStatus(code = HttpStatus.CREATED)
	public UserData createUser(@RequestBody UserData userData) {
		log.info("Creating new user {}", userData);
		return rewardService.saveUser(userData);
		
	}
	
	
}
