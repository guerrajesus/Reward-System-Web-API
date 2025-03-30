package reward.system.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.ColumnDefault;

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
	@Column(nullable = false, updatable = false)
	private LocalDateTime dateCreated;
	
	@ColumnDefault("0")
	private Integer pointBalance;
	
	@ColumnDefault("'SILVER'")
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TierStatus tierStatus;
	
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
	//private Set<Offer> offers = new LinkedHashSet<>();
	private List<Offer> offers = new ArrayList<>();
	
//	@PrePersist
//	private void prePersist() {
//		if(pointBalance == null) {
//			pointBalance = 0;
//		}
//	}
}

	
