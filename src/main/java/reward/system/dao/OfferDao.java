package reward.system.dao;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import reward.system.entity.Offer;

public interface OfferDao extends JpaRepository<Offer, Long> {

	
	List<Offer> findAllByOrderByPointsRequiredAsc();
	
	@Query("SELECT o FROM Offer o")
	Set<Offer> findAllAsASet();

	Optional<Offer> findByOfferName(String offerName);

	// @Query("SELECT r FROM RewardLog r WHERE u.userId = ?1")
}
