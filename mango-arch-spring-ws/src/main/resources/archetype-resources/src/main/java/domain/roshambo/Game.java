package ${packageName}.domain.roshambo;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.emo.mango.spring.MangoUtils;

@Entity
public class Game {

	@Embedded
	private/* should be final */PartyName partyName;

	@Column(nullable = false)
	private/* should be final */int maxRounds = 10;

	@Column(nullable = false)
	private int playedRound = 0;

	@Column(nullable = false)
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private/* should be final */DateTime creationDate;

	public Game(final PartyName name, final int maxRounds) {
		this.partyName = name;
		this.maxRounds = maxRounds;
	}
	
	public void aRoundHasBeenPlayed() {
		playedRound++;
	}
	
	public boolean someRoundLeft() {
		return playedRound < maxRounds;
	}
	
	public PartyName getPartyName() {
		return partyName;
	}

	public int getMaxRounds() {
		return maxRounds;
	}

	public int getPlayedRound() {
		return playedRound;
	}

	public DateTime getCreationDate() {
		return creationDate;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Game && same((Game) obj);
	}

	public boolean same(final Game other) {
		return maxRounds == other.maxRounds
				&& partyName.equals(other.partyName);
	}

	@Override
	public int hashCode() {
		return maxRounds + playedRound + creationDate.hashCode()
				+ partyName.hashCode();
	}

	@Override
	public String toString() {
		return MangoUtils.toJson(this);
	}

	// For JPA use.
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long eid;

	@Deprecated
	protected Long getEid() {
		return eid;
	}

	@Deprecated
	protected Game() {

	}
}
