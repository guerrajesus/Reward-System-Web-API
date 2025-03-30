package reward.system.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import reward.system.controller.model.UserData;
import reward.system.controller.model.UserData.OfferData;
import reward.system.controller.model.UserData.RewardLogData;
import reward.system.dao.OfferDao;
import reward.system.dao.RewardLogDao;
import reward.system.dao.UserDao;
import reward.system.entity.Offer;
import reward.system.entity.RewardLog;
import reward.system.entity.TierStatus;
import reward.system.entity.User;

@Service
public class RewardService {
	@Autowired
	private UserDao userDao;

	@Autowired
	private OfferDao offerDao;

	@Autowired
	private RewardLogDao rewardLogDao;
	// private TierStatus currentStatus;

	@Transactional(readOnly = false)
	public UserData saveUser(UserData userData) {
		Long userId = userData.getUserId(); // Post this will be null, not created yet
		User user = findOrCreateUser(userId);
		
		//user.setOffers(retrieveAllOffersUserQualifiesFor(user));
		
		setFieldsInUser(user, userData);
		return new UserData(userDao.save(user));
	}

	@Transactional(readOnly = true)
	public UserData retrieveUserById(Long userId) {
		return new UserData(findUserById(userId));
	}

	@Transactional(readOnly = true)
	public List<User> retrieveAllUsers() {
		List<User> users = userDao.findAll();
		//List<UserData> usersDto = new ArrayList<>();

		if (users.isEmpty()) {
			return Collections.emptyList();
		}

		for (User user : users) {
			user.getOffers().clear(); 
			user.getRewardLog().clear();
			//usersDto.add(new UserData(user));
		}
		return users;
	}

	@Transactional(readOnly = false)
	public void deleteUserById(Long userId) {
		User user = findUserById(userId);
		userDao.delete(user);
	}

	@Transactional(readOnly = false)
	public RewardLogData saveRewardLog(Long userId, RewardLogData logData) {
		User user = findUserById(userId);												   
		RewardLog rewardLog = findOrCreateRewardLog(userId, logData.getRewardLogId());
		
		setUserPointsAndTier(user,logData.getPointValue());
		rewardLog.setUser(user);
		
		setFieldsInRewardLog(rewardLog, logData);
		return new RewardLogData(rewardLogDao.save(rewardLog));
	}
	
	private void setUserPointsAndTier(User user, Integer pointValue) {
		Integer logPoints = setValueToZeroIfNull(pointValue);
		Integer userPointTotal = user.getPointBalance() + logPoints;
		
		user.setPointBalance(userPointTotal);
		user.setTierStatus(setStatusBasedOffUsersPoints(userPointTotal));
	}
	
	@Transactional(readOnly = false)
	public RewardLogData redeemOffer(Long userId, Long offerId) {
		User user = findUserById(userId);
		Offer offer = findOfferById(offerId);
		RewardLog rewardLog = setFieldsInRewardLogUsingOffer(offer);
		
		setUserPointsAndTier(user, rewardLog.getPointValue());
		rewardLog.setUser(user);
		
		return new RewardLogData(rewardLogDao.save(rewardLog));
	}

	private RewardLog setFieldsInRewardLogUsingOffer(Offer offer) {
		LocalDateTime dT = LocalDateTime.now();
		RewardLog rewardLog = new RewardLog();
		rewardLog.setRewardLogDate(dT);
		rewardLog.setPointValue(- offer.getPointsRequired()); //negative value of offer
		rewardLog.setDescription("Redeeming offer: " + offer.getOfferName());
		return rewardLog;
	}

	@Transactional(readOnly = true)
	public List<RewardLogData> retrieveAllRewardLogsByUserId(Long userId) {
		User user = findUserById(userId);
	
		return new UserData(user).getRewardLogs().stream()
				.sorted(Comparator.comparingLong(RewardLogData::getRewardLogId)).toList();
	}			

	@Transactional(readOnly = false) //Tested and has to be false to save to join table. 
	public List<OfferData> retrieveAllOffersUserIdQualifiesFor(Long userId) {
		User user = findUserById(userId);
		if(user.getPointBalance() == null) { //if null then don't qualify for any offers
			return Collections.emptyList();
		}
		
		List<Offer> offers = offerDao.findAllByOrderByPointsRequiredAsc();
		List<Offer> results = new ArrayList<>();
		
		for (Offer offer : offers) {
			if(user.getPointBalance() >= offer.getPointsRequired()) {
				offer.getUsers().add(user);  //user stays the same for join table
				user.getOffers().add(offer); //Adds offer user qualifies for
				results.add(offer);
			}
		}
		return results.stream().map(OfferData::new).toList();
				
//				.sorted(Comparator.comparingInt(Offer::getPointsRequired))
//				.toList(); // return in Asc order
		
	}
	
	/*
	 * Transactions above ^
	 */

	private void setFieldsInUser(User user, UserData userData) {
		user.setUserId(userData.getUserId()); // set by MySQL
		user.setUserFirstName(userData.getUserFirstName());
		user.setUserLastName(userData.getUserLastName());
		user.setUserEmail(userData.getUserEmail());
		user.setUserPhone(userData.getUserPhone());

		user.setPointBalance(setValueToZeroIfNull(userData.getPointBalance()));

		user.setTierStatus(getUserDataTierStatus(userData));
		
	}
	
	private Integer setValueToZeroIfNull(Integer value) {
		return (value == null) ? 0 : value;
	}

	private TierStatus getUserDataTierStatus(UserData userData) {
		TierStatus status = null;
		String tierStatusString = userData.getTierStatus();
		
		Integer pointBalance = setValueToZeroIfNull(userData.getPointBalance());

		if (tierStatusString == null) { 
			return setStatusBasedOffUsersPoints(pointBalance);
			
		}else {
		
			try {
				status = TierStatus.valueOf(tierStatusString.toUpperCase().trim());
			} catch (IllegalArgumentException ex) {
				throw new IllegalArgumentException(tierStatusString + " is not a valid TierStatus ");
			}
		}
		return status;
	}
	
	private TierStatus setStatusBasedOffUsersPoints(Integer pointBalance) {
		TierStatus status;
		if (pointBalance < 1000) { 
			status = TierStatus.SILVER;
		}else if (pointBalance < 2000) { //1000-1999
			status = TierStatus.GOLD;
		}else if (pointBalance < 4000) { //2000-3999
			status = TierStatus.EMERALD;
		}else {
			status = TierStatus.DIAMOND; //Higher than 4,000 == Diamond
		}
		return status;
	}
	

	private User findOrCreateUser(Long userId) {
		User user;

		if (userId == null) {
			LocalDateTime dT = LocalDateTime.now();
			user = new User(); // Post/Create
			user.setDateCreated(dT);
		} else {
			user = findUserById(userId);// Put/Update
		}
		return user;

	}

	private User findUserById(Long userId) {
		return userDao.findById(userId)
				.orElseThrow(() -> new NoSuchElementException("user with ID= " + userId + " does not exist."));
	}

	private void setFieldsInRewardLog(RewardLog rewardLog, RewardLogData rewardLogData) {
		rewardLog.setRewardLogId(rewardLogData.getRewardLogId());
		rewardLog.setPointValue(setValueToZeroIfNull(rewardLogData.getPointValue()));
		rewardLog.setDescription(rewardLogData.getDescription());
		
	}

	private RewardLog findOrCreateRewardLog(Long userId, Long rewardLogId) {
		RewardLog rewardLog;

		if (rewardLogId == null) {
			LocalDateTime dT = LocalDateTime.now();
			rewardLog = new RewardLog();
			rewardLog.setRewardLogDate(dT);
		} else {
			rewardLog = findRewardLogById(userId, rewardLogId);
		}
		return rewardLog;

	}

	private RewardLog findRewardLogById(Long userId, Long rewardLogId) {
		RewardLog rewardLog = rewardLogDao.findById(rewardLogId).orElseThrow(
		() -> new NoSuchElementException("Reward Log with ID= " + rewardLogId + " does not exist."));

		if (rewardLog.getUser().getUserId() != rewardLogId) {
			throw new IllegalArgumentException("User ID= " + userId + " does not have reward log =" + rewardLogId);
		}
		return rewardLog;
	}

	@Transactional(readOnly = false)
	public OfferData saveoffer(OfferData offerData) {
		Long offerId = offerData.getOfferId();
		Offer offer = findOrCreateOffer(offerId, offerData.getOfferName());

		setOfferFields(offer, offerData);

		return new OfferData(offerDao.save(offer));
	}

	private void setOfferFields(Offer offer, OfferData offerData) {
		offer.setOfferId(offerData.getOfferId());
		offer.setOfferName(offerData.getOfferName());
		offer.setPointsRequired(offerData.getPointsRequired());
	}

	private Offer findOrCreateOffer(Long offerId, String offerName) {
		Offer offer;

		if (offerId == null) {
			  Optional<Offer> opOffer = offerDao.findByOfferName(offerName);
			
			if(opOffer.isPresent()) {
				throw new DuplicateKeyException("Offer with name =" + offerName + " already exist");
			}
			offer = new Offer(); 
			
		} else {
			offer = findOfferById(offerId);
		}

		return offer;
	}

	private Offer findOfferById(Long offerId) {
		return offerDao.findById(offerId).orElseThrow(
				() -> new NoSuchElementException(
						"Offer with ID=" + offerId + " does not exist"));
	}
	
	@Transactional(readOnly = true)
	public List<OfferData> retrieveAllOffers() {
		return offerDao.findAllByOrderByPointsRequiredAsc().stream().map(OfferData::new).toList();
	}




	

}
