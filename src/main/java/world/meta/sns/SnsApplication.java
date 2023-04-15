package world.meta.sns;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import world.meta.sns.entity.Board;
import world.meta.sns.entity.Member;
import world.meta.sns.repository.BoardRepository;
import world.meta.sns.repository.MemberRepository;

@SpringBootApplication
@RequiredArgsConstructor
public class SnsApplication {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    public static void main(String[] args) {
        SpringApplication.run(SnsApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {

        // member
        Member member = new Member("member1");
        memberRepository.save(member);

        // board
        Board board = new Board("title1", "content1", member);
        boardRepository.save(board);
    }

}
