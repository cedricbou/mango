package ${packageName}.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.inject.Inject;

import org.joda.time.DateTimeUtils;
import org.joda.time.Period;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-init.xml")
// @ActiveProfiles(profiles = "choose a spring profile here...")
public class GameIntegration {

	@Inject
	private PlayGame game;
	
	@Inject MangoCqs cqs;
	
	@Test
	public void deposerPourUsageSpecifique() {
		game.startANewGame(new PartyName("test1"), 10);
		
		game.playARound(new PartyName("test1"), new PlayerName("tester"), Gesture.Rock);
		
		
	}

}
