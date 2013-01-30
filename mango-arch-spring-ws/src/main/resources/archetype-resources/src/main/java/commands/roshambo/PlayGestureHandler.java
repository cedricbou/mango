package ${packageName}.commands.roshambo;

import javax.inject.Inject;

import org.joda.time.Period;
import org.springframework.stereotype.Component;

import com.emo.mango.cqs.Handler;
import com.emo.mango.spring.cqs.annotations.CommandHandler;

@Component
@CommandHandler(${packageName}.PlayGesture)
class PlayGestureHandler implements Handler<PlayGesture> {

	private @Inject PlayGame game;
	
	@Override
	public void handle(PlayGesture gesture) {
		game.playARound(new PartyName(gesture.partyName), new PlayerName(gesture.playerName), gesture.gesture);
	}
}
