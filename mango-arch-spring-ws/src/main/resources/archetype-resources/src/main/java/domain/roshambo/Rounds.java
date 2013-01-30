package ${packageName}.domain.roshambo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface Rounds extends JpaRepository<Round, Long> {

}
