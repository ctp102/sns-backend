package world.meta.sns.core.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import world.meta.sns.api.exception.CustomAccessDeniedException;
import world.meta.sns.api.exception.CustomNotFoundException;
import world.meta.sns.core.comment.dto.CommentRequestDto;
import world.meta.sns.core.comment.dto.CommentUpdateDto;
import world.meta.sns.core.board.entity.Board;
import world.meta.sns.core.comment.entity.Comment;
import world.meta.sns.core.member.entity.Member;
import world.meta.sns.core.board.repository.BoardRepository;
import world.meta.sns.core.comment.repository.CommentRepository;
import world.meta.sns.core.member.repository.MemberRepository;

import static world.meta.sns.api.common.enums.ErrorResponseCodes.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    /**
     * 댓글 등록
     *
     * @param commentRequestDto the request dto
     */
    public void saveComment(Long boardId, CommentRequestDto commentRequestDto) {

        Long memberId = commentRequestDto.getMemberId();
        Long parentCommentId = commentRequestDto.getParentCommentId();

        Member foundMember = memberRepository.findById(memberId).orElse(null);
        if (foundMember == null) {
            log.error("[saveComment] 사용자를 찾을 수 없습니다. memberId: {}", memberId);
            throw new CustomNotFoundException(MEMBER_NOT_FOUND.getNumber(), MEMBER_NOT_FOUND.getMessage());
        }

        Board foundBoard = boardRepository.findById(boardId).orElse(null);
        if (foundBoard == null) {
            log.error("[saveComment] 게시글을 찾을 수 없습니다. boardId: {}", boardId);
            throw new CustomNotFoundException(BOARD_NOT_FOUND.getNumber(), BOARD_NOT_FOUND.getMessage());
        }

        Comment parentComment = null;
        // 2. 자식 댓글인지 부모 댓글인지 체크
        if (parentCommentId != null) {
            parentComment = commentRepository.findById(parentCommentId).orElse(null);

            if (parentComment == null) {
                log.error("[saveComment] 부모 댓글을 찾을 수 없습니다. parentCommentId: {}", parentCommentId);
                throw new CustomNotFoundException(COMMENT_NOT_FOUND.getNumber(), COMMENT_NOT_FOUND.getMessage());
            }

            // 2-1. 부모댓글의 게시글 번호와 자식댓글의 게시글 번호 같은지 체크하기
            if (!parentComment.getBoard().getId().equals(boardId)) {
                log.error("[saveComment] 부모댓글의 게시글 번호와 자식댓글의 게시글 번호가 다릅니다. parentCommentId: {}, boardId: {}", parentCommentId, boardId);
                throw new CustomAccessDeniedException(DIFFERENT_BOARD_ID.getNumber(), DIFFERENT_BOARD_ID.getMessage());
            }
        }

        // 3. CommentRequestDto -> Comment
        Comment comment = Comment.builder()
                .board(foundBoard)
                .member(foundMember)
                .content(commentRequestDto.getContent())
                .build();

        if (parentComment != null) {
            comment.updateParentComment(parentComment);
        }

        commentRepository.save(comment);

        log.info("comment: {}", comment);
    }

    /**
     * 댓글 수정
     *
     * @param commentId        the comment id
     * @param commentUpdateDto the comment update dto
     */
    public void updateComment(Long commentId, CommentUpdateDto commentUpdateDto) {

        Comment comment = commentRepository.findByIdAndMemberId(commentId, commentUpdateDto.getMemberId());
        if (comment == null) {
            log.error("[updateComment] 해당 회원이 작성한 댓글을 찾을 수 없습니다. commentId: {}", commentId);
            throw new CustomNotFoundException(COMMENT_NOT_FOUND.getNumber(), COMMENT_NOT_FOUND.getMessage());
        }

        comment.update(commentUpdateDto);
    }

    /**
     * 댓글 삭제
     *
     * @param commentId the comment id
     */
    public void deleteComment(Long commentId, Long memberId) {

        if (!commentRepository.existsByIdAndMemberId(commentId, memberId)) {
            log.error("[deleteComment] 해당 회원이 작성한 댓글을 찾을 수 없습니다. commentId: {}, memberId: {}", commentId, memberId);
            throw new CustomNotFoundException(COMMENT_NOT_FOUND.getNumber(), COMMENT_NOT_FOUND.getMessage());
        }

        commentRepository.deleteChildCommentsByCommentId(commentId);  // 자식 댓글 삭제
        commentRepository.deleteCommentsByCommentId(commentId); // 부모 댓글 삭제
    }
}
