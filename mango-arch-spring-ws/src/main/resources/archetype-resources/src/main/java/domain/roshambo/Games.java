package ${packageName}.domain.roshambo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface Games extends JpaRepository<Game, Long> {

	public Game findByPartyName(final PartyName name);
}
