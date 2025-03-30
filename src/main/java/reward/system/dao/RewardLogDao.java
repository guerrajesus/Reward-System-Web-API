package reward.system.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import reward.system.entity.RewardLog;
import reward.system.entity.User;

public interface RewardLogDao extends JpaRepository<RewardLog, Long> {

	//@Query("SELECT r FROM RewardLog r WHERE u.userId = ?1")
	List<RewardLog> findAllByUser(User user);

}
