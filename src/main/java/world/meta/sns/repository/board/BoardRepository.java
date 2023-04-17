package world.meta.sns.repository.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import world.meta.sns.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {

    @Query("select b, m, c from Board b " +
            "join fetch b.member m " +
            "join fetch b.comments c " +
            "where b.id = :id")
    Board findFetchJoinById(
            @Param("id") Long id
    );

}
