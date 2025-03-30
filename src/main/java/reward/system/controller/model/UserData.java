package reward.system.controller.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.NoArgsConstructor;
import reward.system.entity.Offer;
import reward.system.entity.RewardLog;
import reward.system.entity.User;

@Data //Getters and setter
@NoArgsConstructor 
public class UserData { 
	
	private Long userId;
	private String userFirstName;
	private String userLastName;
	private String userEmail;
	private String userPhone;
	private LocalDateTime dateCreated;
	private String tierStatus; 
	private Integer pointBalance;
	
	private Set<RewardLogData> rewardLogs = new HashSet<>();
	private Set<OfferData> offers = new LinkedHashSet<>();
					
	public UserData(User user) { //Constructor - New UserData(userData);
		userId = user.getUserId();
		userFirstName = user.getUserFirstName();
		userLastName = user.getUserLastName();
		userEmail = user.getUserEmail();
		userPhone = user.getUserPhone();
		tierStatus = user.getTierStatus().name(); 
		pointBalance = user.getPointBalance();
		dateCreated = user.getDateCreated();
		
		if(user.getRewardLog() != null) {
			for(RewardLog rewardLog : user.getRewardLog()) {
				this.rewardLogs.add(new RewardLogData(rewardLog));
			}
		}
		
		if(user.getOffers() != null) {
			this.offers = user.getOffers().stream()
					.map(OfferData::new).collect(Collectors.toSet());
		}
				
		
	}
	
	@Data
	@NoArgsConstructor
	public static class OfferData {
		private Long offerId;
		private String offerName;
		private Integer pointsRequired;  
		//private String description; removed 
		//no user linking from that
		
		public OfferData(Offer offer) {
			offerId = offer.getOfferId();
			offerName = offer.getOfferName();
			pointsRequired = offer.getPointsRequired();
		}
	}
	
	@Data
	@NoArgsConstructor
	public static class RewardLogData {
		private Long rewardLogId;
		private Integer pointValue; 
		private String description;
		private LocalDateTime rewardLogDate;
		//no user
		
		public RewardLogData(RewardLog rewardLog) {
			rewardLogId = rewardLog.getRewardLogId();
			pointValue = rewardLog.getPointValue();
			description = rewardLog.getDescription();
			rewardLogDate = rewardLog.getRewardLogDate();
		}
	}
	
	
	
}