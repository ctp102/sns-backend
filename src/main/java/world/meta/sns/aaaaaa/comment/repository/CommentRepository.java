package world.meta.sns.aaaaaa.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import world.meta.sns.aaaaaa.comment.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    /**
     * member id로 작성한 부모 댓글 조회
     */
    @Query(
            "select c.parentComment.id from Comment c " +
            "where c.member.id = :memberId and (c.parentComment.id is not null and c.parentComment.id = :memberId)"
    )
    List<Long> findParentCommentIdsByMemberId(
            @Param("memberId") Long memberId
    );

    /**
     * board id로 작성한 부모 댓글 조회
     */
    @Query(
            "select c.parentComment.id from Comment c " +
            "where c.board.id = :boardId and (c.parentComment.id is not null and c.parentComment.id = :boardId)"
    )
    List<Long> findParentCommentIdsByBoardId(
            @Param("boardId") Long boardId
    );

    /**
     * parent comment ids에 포함되는 자식 댓글 삭제
     */
    @Modifying(clearAutomatically = true)
    @Query("delete from Comment c where c.parentComment.id in :parentCommentIds")
    void deleteChildCommentsByParentCommentIds(
            @Param("parentCommentIds") List<Long> parentCommentIds
    );

    /**
     * member id로 작성한 댓글 삭제
     */
    @Modifying(clearAutomatically = true)
    @Query("delete from Comment c where c.member.id = :memberId")
    void deleteCommentsByMemberId(
            @Param("memberId") Long memberId
    );

    /**
     * board id로 작성한 댓글 삭제
     */
    @Modifying(clearAutomatically = true)
    @Query("delete from Comment c where c.board.id = :boardId")
    void deleteCommentsByBoardId(
            @Param("boardId") Long boardId
    );

    /**
     * comment id로 자식 댓글 삭제
     */
    @Modifying(clearAutomatically = true)
    @Query("delete from Comment c where c.parentComment.id = :commentId")
    void deleteChildCommentsByCommentId(
            @Param("commentId") Long commentId
    );

    /**
     * comment id로 작성한 댓글 삭제
     */
    @Modifying(clearAutomatically = true)
    @Query("delete from Comment c where c.id = :commentId")
    void deleteCommentsByCommentId(
            @Param("commentId") Long commentId
    );

}
