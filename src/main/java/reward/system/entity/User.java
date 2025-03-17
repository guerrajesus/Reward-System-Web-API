package reward.system.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data  //Lombok getters and setters 
@Entity
public class User {  //UserData 
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) //Database will handle 
	private Long userId;
	private String userFirstName;
	private String userLastName;
	private String userEmail;
	private String userPhone;
	
	@Temporal(TemporalType.TIMESTAMP) //Date/Time 
	private LocalDateTime dateCreated;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TierStatus tierStatus = TierStatus.SILVER;
	
	private Integer pointBalance;
	
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL) //java name
	private Set<RewardLog> rewardLog = new HashSet<>();
	
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@ManyToMany(cascade = CascadeType.PERSIST) //Wont delete offers if we delete a user .All would delete offer linked with user
	@JoinTable(
			name = "user_offer",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "offer_id")
	)                                       //Only time we use table names
	private Set<Offer> offers = new HashSet<>();
}

	
