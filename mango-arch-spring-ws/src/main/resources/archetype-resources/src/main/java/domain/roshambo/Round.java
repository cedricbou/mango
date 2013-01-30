package ${packageName}.domain.roshambo;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.emo.mango.spring.MangoUtils;

@Entity
public class Round {

	@Embedded
	private /* should be final */ PartyName partyName;

	@Embedded
	private /* should be final */ PlayerName player1;

	@Embedded
	private /* should be final */ PlayerName player2;

	@Embedded
	private /* should be final */ Gesture gesturePlayer1;

	@Embedded
	private /* should be final */ Gesture gesturePlayer2;

	@Embedded
	private /* should be final */ PlayerName winner;

	public PartyName getPartyName() {
		return partyName;
	}

	public PlayerName getPlayer1() {
		return player1;
	}

	public PlayerName getPlayer2() {
		return player2;
	}

	public Gesture getGesturePlayer1() {
		return gesturePlayer1;
	}

	public Gesture getGesturePlayer2() {
		return gesturePlayer2;
	}

	public PlayerName getWinner() {
		return winner;
	}

	private final static Gesture[] gestures = new Gesture[] { Gesture.Paper,
			Gesture.Rock, Gesture.Scissors };

	public Round(final PartyName partyName, final PlayerName player1,
			final PlayerName player2, final Gesture gesturePlayer1,
			final Gesture gesturePlayer2) {
		this.partyName = partyName;
		this.player1 = player1;
		this.player2 = player2;
		this.gesturePlayer1 = gesturePlayer1;
		this.gesturePlayer2 = gesturePlayer2;

		if (gesturePlayer1 == gesturePlayer2) {
			winner = new PlayerName("Nobody");
		} else {
			if (gesturePlayer1.strongerThan(gesturePlayer2)) {
				winner = player1;
			} else {
				winner = player2;
			}
		}
	}

	public Round(final PartyName partyName, final PlayerName player1,
			final Gesture gesturePlayer1) {
		this(partyName, player1, new PlayerName("Computer"), gesturePlayer1,
				gestures[(int) (Math.random() * 100) % 3]);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Round && same((Round) obj);
	}

	public boolean same(final Round other) {
		return player1.equals(other.player1) && player2.equals(other.player2)
				&& gesturePlayer1.equals(other.gesturePlayer1)
				&& gesturePlayer2.equals(other.gesturePlayer2)
				&& partyName.equals(other.partyName);
	}

	@Override
	public int hashCode() {
		return player1.hashCode() + player2.hashCode()
				+ gesturePlayer1.hashCode() + gesturePlayer2.hashCode()
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
	protected Round() {

	}
}
