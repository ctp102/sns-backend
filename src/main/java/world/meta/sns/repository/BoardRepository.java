package world.meta.sns.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import world.meta.sns.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {

    @EntityGraph(attributePaths = {"member"})
    @Query("select b from Board b where b.title = :title")
    Board findFetchJoinByBoardTitle(
            @Param("title") String title
    );

}
