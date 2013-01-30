package ${packageName}.queries.roshambo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;

import com.emo.mango.spring.jpa.annotations.QueryWithJpa;
import com.emo.mango.spring.jpa.support.QueryWithJpaExecutor;

@Component
@QueryWithJpa(value = PlayedRoundQuery.class, jpql = "select new queries.PlayedRoundQuery(r.player1, r.player2, r.gesturePlayer1, r.gesturePlayer2, r.winner) from Round r where r.partyName = :partyName")
public class PlayedRoundQueryExecutor extends QueryWithJpaExecutor<PlayedRoundQuery> {

	@Override
	protected EntityManager entityManager() {
		return em;
	}

	@PersistenceContext
	private EntityManager em;
}
