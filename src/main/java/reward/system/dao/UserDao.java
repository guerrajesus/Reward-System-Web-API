package reward.system.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import reward.system.entity.User;

public interface UserDao extends JpaRepository<User, Long> {

}
