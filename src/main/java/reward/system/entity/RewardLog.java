package reward.system.entity;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
public class RewardLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rewardLogId;
	
	private Integer pointValue; 
	private String description;
	
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime rewardLogDate;
	
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id", nullable = false) //user PK column name 
	private User user;
}
