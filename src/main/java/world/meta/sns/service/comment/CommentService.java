package world.meta.sns.service.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import world.meta.sns.dto.comment.CommentRequestDto;
import world.meta.sns.entity.Board;
import world.meta.sns.entity.Comment;
import world.meta.sns.entity.Member;
import world.meta.sns.repository.board.BoardRepository;
import world.meta.sns.repository.comment.CommentRepository;
import world.meta.sns.repository.member.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    public void saveComment(Long boardId, Long memberId, Comment comment) {
        // 1. board 존재 유무 체크
        Board foundBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid board id"));

        Comment parentComment = null;
        // 2. 자식 댓글인지 부모 댓글인지 체크
            // 2-1. 자식 댓글인 경우 부모 댓글의 boardId와 자식 댓글의 boardId가 일치하는지 체크


    }

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
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

}
