package ${packageName}.application.roshambo;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import ${packageName}.domain.roshambo.Game;
import ${packageName}.domain.roshambo.Games;
import ${packageName}.domain.roshambo.Gesture;
import ${packageName}.domain.roshambo.PartyName;
import ${packageName}.domain.roshambo.PlayerName;
import ${packageName}.domain.roshambo.Round;
import ${packageName}.domain.roshambo.Rounds;

@Service
public class PlayGame {

	private final Games games;
	
	private final Rounds rounds;
	
	@Inject
	private PlatformTransactionManager transactionManager;
	
	@Inject
	public PlayGame(final Games games, final Rounds rounds) {
		this.games = games;
		this.rounds = rounds;
	}
	
	@Transactional
	public void startANewGame(final PartyName partyName, int maxRounds) {
		games.save(new Game(partyName, maxRounds));
	}
	
	public void playARound(final PartyName name, final PlayerName player, final Gesture gesture) {
		playedRound(name); // write ahead.
		playTheRound(name, player, gesture);
	}
	
	private void playTheRound(final PartyName name, final PlayerName player, final Gesture gesture) {
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
	    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
	        @Override
	        protected void doInTransactionWithoutResult(TransactionStatus status) {
	    		rounds.save(new Round(name, player, gesture));
	        }});
	}
	
	private void playedRound(final PartyName name) {
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
	    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
	        @Override
	        protected void doInTransactionWithoutResult(TransactionStatus status) {
	        	final Game game = games.findByPartyName(name);
	        	game.aRoundHasBeenPlayed();
	        }});
	}
}
