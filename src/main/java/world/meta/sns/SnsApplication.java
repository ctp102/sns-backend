package world.meta.sns;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import world.meta.sns.entity.Board;
import world.meta.sns.entity.Member;
import world.meta.sns.enums.Category;
import world.meta.sns.repository.board.BoardRepository;
import world.meta.sns.repository.member.MemberRepository;

@SpringBootApplication
@RequiredArgsConstructor
public class SnsApplication {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    public static void main(String[] args) {
        SpringApplication.run(SnsApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() throws InterruptedException {

        for (int i = 1; i <= 10; i++) {
            // member
            Member member = new Member("member" + i);
            memberRepository.save(member);

            for (int j = 1; j <= 5; j++) {
                // board
                Board board = new Board("title" + j, "content" + j, Category.PUBLIC, member);
                boardRepository.save(board);
//                Thread.sleep(500);
            }
        }

    }

}
