package reward.system.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
public class Offer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long offerId;
	
	private String offerName;
	private Integer pointsRequired;  
	//private String description; removed 
	
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@ManyToMany(mappedBy = "offers")
	private Set<User> users = new HashSet<>();
} 
