package world.meta.sns.repository.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import world.meta.sns.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {

    // comments까지 가지고 오면 성능상 이슈가 있을 것 같아 비즈니스 로직에서 처리
    @Query("select b, m from Board b " +
            "join fetch b.member m " +
//            "join fetch b.comments c " +
            "where b.id = :id")
    Board findFetchJoinById(
            @Param("id") Long id
    );

}
