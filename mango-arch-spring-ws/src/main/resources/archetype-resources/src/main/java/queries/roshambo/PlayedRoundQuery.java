package ${packageName}.queries.roshambo;

import ${packageName}.domain.roshambo.Gesture;
import ${packageName}.domain.roshambo.PlayerName;

public class PlayedRoundQuery {

	public final PlayerName player1;
	
	public final Gesture gesturePlayer1;
	
	public final PlayerName player2;
	
	public final Gesture gesturePlayer2;
	
	public final PlayerName winner;
	
	public PlayedRoundQuery(final PlayerName player1, final PlayerName player2, final Gesture gesture1, final Gesture gesture2, final PlayerName winner) {
		this.player1 = player1;
		this.player2 = player2;
		this.gesturePlayer1 = gesture1;
		this.gesturePlayer2 = gesture2;
		this.winner = winner;
	}
}
