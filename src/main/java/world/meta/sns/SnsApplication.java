package world.meta.sns;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import world.meta.sns.core.board.entity.Board;
import world.meta.sns.core.comment.entity.Comment;
import world.meta.sns.core.member.entity.Member;
import world.meta.sns.core.board.enums.Category;
import world.meta.sns.core.board.repository.BoardRepository;
import world.meta.sns.core.comment.repository.CommentRepository;
import world.meta.sns.core.member.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@SpringBootApplication
@RequiredArgsConstructor
public class SnsApplication {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    public static void main(String[] args) {
        SpringApplication.run(SnsApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() throws InterruptedException {

        String password = bCryptPasswordEncoder.encode("test");

        for (int i = 1; i <= 10; i++) {
            // 회원 생성
            Member member = new Member("member" + i + "@gmail.com", password, "member" + i);
            memberRepository.save(member);

            for (int j = 1; j <= 5; j++) {
                // 게시글 생성
                Board board = new Board("title" + j, "content" + j, Category.PUBLIC, member);
                boardRepository.save(board);
            }
        }

        // 1번 회원 댓글 생성
        Comment parentComment = new Comment();

        memberRepository.findById(1L).ifPresent(parentComment::setMember);
        boardRepository.findById(1L).ifPresent(parentComment::setBoard);

        parentComment.setContent("부모 댓글 1");

        commentRepository.save(parentComment);

        for (int i = 1; i <= 3; i++) {

            for (int j = 1; j <= 2; j++) {
                Comment childComment = new Comment();

                memberRepository.findById((long) j).ifPresent(childComment::setMember);
                boardRepository.findById(1L).ifPresent(childComment::setBoard);

                childComment.setParentComment(parentComment);
                childComment.setContent("자식 댓글 " + i);

                commentRepository.save(childComment);
            }

        }

//        entityManager.flush();
//        entityManager.clear();
    }

}
