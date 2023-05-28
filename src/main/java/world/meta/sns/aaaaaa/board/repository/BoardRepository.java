package world.meta.sns.aaaaaa.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import world.meta.sns.aaaaaa.board.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {

    // comments까지 가지고 오면 성능상 이슈가 있을 것 같아 비즈니스 로직에서 처리
    @Query("select b, m from Board b " +
            "join fetch b.member m " +
//            "join fetch b.comments c " +
            "where b.id = :id")
    Board findFetchJoinById(
            @Param("id") Long id
    );

    @Modifying(clearAutomatically = true)
    @Query("delete from Board b where b.member.id = :memberId")
    void deleteByMemberId(
            @Param("memberId") Long memberId
    );

    @Modifying(clearAutomatically = true)
    @Query("delete from Board b where b.id = :boardId")
    void deleteByBoardId(
            @Param("boardId") Long boardId
    );

}
