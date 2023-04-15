package world.meta.sns.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import world.meta.sns.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
