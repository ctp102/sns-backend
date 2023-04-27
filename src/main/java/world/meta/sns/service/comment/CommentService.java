package world.meta.sns.service.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import world.meta.sns.dto.comment.CommentRequestDto;
import world.meta.sns.dto.comment.CommentUpdateDto;
import world.meta.sns.entity.Board;
import world.meta.sns.entity.Comment;
import world.meta.sns.entity.Member;
import world.meta.sns.repository.board.BoardRepository;
import world.meta.sns.repository.comment.CommentRepository;
import world.meta.sns.repository.member.MemberRepository;

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
     * @param requestDto the request dto
     */
    public void saveComment(CommentRequestDto requestDto) {
        // 1. board 존재 유무 체크
        Board foundBoard = boardRepository.findById(requestDto.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid board id"));

        Member foundMember = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid member id"));

        Comment parentComment = null;
        // 2. 자식 댓글인지 부모 댓글인지 체크
        if (requestDto.getParentCommentId() != null) {
            parentComment = commentRepository.findById(requestDto.getParentCommentId()).orElse(null);

            if (parentComment == null) {
                throw new IllegalArgumentException("DB에 parentCommentId가 존재하지 않음");
            }

            // 2-1. 부모댓글의 게시글 번호와 자식댓글의 게시글 번호 같은지 체크하기
            if (!parentComment.getBoard().getId().equals(requestDto.getBoardId())) {
                throw new IllegalArgumentException("부모댓글의 게시글 번호와 자식댓글의 게시글 번호가 다름");
            }
        }

        // 3. CommentRequestDto -> Comment
        Comment comment = Comment.builder()
                .board(foundBoard)
                .member(foundMember)
                .content(requestDto.getContent())
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

        Comment comment = commentRepository.findById(commentId).orElse(null);

        if (comment == null) {
            return;
        }

        comment.update(commentUpdateDto);
    }

    /**
     * 댓글 삭제
     *
     * @param commentId the comment id
     */
    public void deleteComment(Long commentId) {
        commentRepository.deleteChildCommentsByCommentId(commentId);  // 자식 댓글 삭제
        commentRepository.deleteCommentsByCommentId(commentId); // 부모 댓글 삭제
    }
}
