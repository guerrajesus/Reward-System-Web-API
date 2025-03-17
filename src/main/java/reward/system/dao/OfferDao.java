package reward.system.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import reward.system.entity.Offer;

public interface OfferDao extends JpaRepository<Offer, Long> {

}
