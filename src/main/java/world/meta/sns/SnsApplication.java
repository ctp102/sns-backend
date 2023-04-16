package world.meta.sns;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import world.meta.sns.entity.Board;
import world.meta.sns.entity.Comment;
import world.meta.sns.entity.Member;
import world.meta.sns.enums.Category;
import world.meta.sns.repository.board.BoardRepository;
import world.meta.sns.repository.comment.CommentRepository;
import world.meta.sns.repository.member.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@SpringBootApplication
@RequiredArgsConstructor
public class SnsApplication {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public static void main(String[] args) {
        SpringApplication.run(SnsApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() throws InterruptedException {

        for (int i = 1; i <= 10; i++) {
            // 회원 생성
            Member member = new Member("member" + i);
            memberRepository.save(member);

            for (int j = 1; j <= 5; j++) {
                // 게시글 생성
                Board board = new Board("title" + j, "content" + j, Category.PUBLIC, member);
                boardRepository.save(board);
            }
        }

        // 댓글 생성
        Comment parentComment = new Comment();

        memberRepository.findById(1L).ifPresent(parentComment::setMember);
        boardRepository.findById(1L).ifPresent(parentComment::setBoard);

        parentComment.setContent("부모 댓글 1");

        commentRepository.save(parentComment);

        for (int i = 1; i <= 3; i++) {
            Comment childComment = new Comment();

            memberRepository.findById(1L).ifPresent(childComment::setMember);
            boardRepository.findById(1L).ifPresent(childComment::setBoard);

            childComment.setParentComment(parentComment);
            childComment.setContent("자식 댓글 " + i);

            commentRepository.save(childComment);
        }

//        entityManager.flush();
//        entityManager.clear();
    }

}
