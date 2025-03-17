package reward.system.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import reward.system.entity.RewardLog;

public interface RewardLogDao extends JpaRepository<RewardLog, Long> {

}
