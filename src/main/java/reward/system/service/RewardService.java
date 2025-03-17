package reward.system.service;

import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import reward.system.controller.model.UserData;
import reward.system.dao.UserDao;
import reward.system.entity.User;

public class RewardService {
	@Autowired
	UserDao userDao;

	@Transactional(readOnly = false)
	public UserData saveUser(UserData userData) {
		Long userId = userData.getUserId();
		User user = findOrCreateUser(userId);
		
		
		return null;
	}

	private User findOrCreateUser(Long userId) {
		User user;
		
		if(Objects.isNull(userId)) {
			user = new User();
		} else {
			user = findUserById(userId);
		}
		return user;
		
	}

	private User findUserById(Long userId) {
		return userDao.findById(userId)
		.orElseThrow(() -> new NoSuchElementException(
				"user with ID= " + userId + "does not exist."));
	}

}
