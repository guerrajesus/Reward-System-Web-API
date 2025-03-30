package reward.system.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import reward.system.controller.model.UserData;
import reward.system.controller.model.UserData.OfferData;
import reward.system.controller.model.UserData.RewardLogData;
import reward.system.entity.Offer;
import reward.system.entity.User;
import reward.system.service.RewardService;

@RestController 				 //expects and returns JSON in request/response bodies
@RequestMapping("/reward_system/")//tell Spring URI for every HTTP request must start with 
@Slf4j 							 //Logger log.info for the console
public class RewardSystemController {
	
	@Autowired
	private RewardService rewardService;
	
	@PostMapping("register")
	@ResponseStatus(code = HttpStatus.CREATED)
	public UserData createUser(@RequestBody UserData userData) {
		log.info("Creating new user {}", userData);
		return rewardService.saveUser(userData);
	}
	
	@PutMapping("user/update/{userId}")
	public UserData updateUser(@RequestBody UserData userData, @PathVariable Long userId) {
		log.info("updating user with ID= {}", userId);
		userData.setUserId(userId);
		return rewardService.saveUser(userData);
	}
	
	@GetMapping("users")
	public List<User> retrieveAllUsers(){
		log.info("Retrieving all users");
		return rewardService.retrieveAllUsers();
	}
	
	@GetMapping("user/{userId}")
	public UserData retrieveUserById(@PathVariable Long userId) {
		log.info("Retrieve info for user ID= {}", userId);
		return rewardService.retrieveUserById(userId);
	}
	
	@DeleteMapping("user/delete/{userId}")
	public Map<String,String> deleteUserById(@PathVariable Long userId) {
		log.info("Deleting user ID = {}", userId);
		 rewardService.deleteUserById(userId);
		 
		 return Map.of("Message: ", "User at ID=" + userId + " was deleted");
	}
	
	@DeleteMapping("/user/delete")  
	public void deleteAllUsers() {  
		log.info("Attempting to delete all Users");
		throw new UnsupportedOperationException(
				"Deleting all Users is not allowed");
	}
	
	@ResponseStatus(code = HttpStatus.CREATED)
	@PostMapping("user/{userId}/rewardLog")
	public RewardLogData createRewardLog(@PathVariable Long userId, @RequestBody RewardLogData rewardLogData) {
		log.info("User ID={} added a new Reward Log {}", userId, rewardLogData);
		return rewardService.saveRewardLog(userId, rewardLogData);
	}
		
	@GetMapping("user/{userId}/history")
	public List<RewardLogData> retrieveAllRewardLogsById(@PathVariable Long userId){
		log.info("Retrieving all Reward Logs from User ID= {}",userId);
		return rewardService.retrieveAllRewardLogsByUserId(userId);
	}
		
	@GetMapping("user/{userId}/offers")
	public List<OfferData> retrieveAllOffersUserQualifiesFor(@PathVariable Long userId) {
		log.info("Retrieving all qualifing Offers User ID= {}", userId);	
		return rewardService.retrieveAllOffersUserIdQualifiesFor(userId);
	}
	
	@ResponseStatus(code = HttpStatus.CREATED)
	@PostMapping("offer/create")
	public OfferData insertOffer(@RequestBody OfferData offerData) {
		log.info("Creating new offer all users can qualify for");
		return rewardService.saveoffer(offerData);
	}
	
	@PutMapping("offer/{offerId}/")
	public OfferData updateOffer(@RequestBody OfferData offerData, 
			@PathVariable Long offerId) {
		offerData.setOfferId(offerId);
		log.info("Updating offer ID={}", offerId);
		return rewardService.saveoffer(offerData);
	}
	
	@GetMapping("offers")
	public List<OfferData> retrieveAllOffers(){
		log.info("Retrieving all Offers available");
		return rewardService.retrieveAllOffers();
	}
	
	@PutMapping("user/{userId}/redeem/{offerId}")
	public RewardLogData redeemOffer (@PathVariable Long userId, @PathVariable Long offerId) {
		log.info("User ={} is redeeming offer {}", userId, offerId);
		return rewardService.redeemOffer(userId, offerId);
	}

	
	
	
	
}
