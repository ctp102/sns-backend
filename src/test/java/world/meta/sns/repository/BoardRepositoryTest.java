package world.meta.sns.repository;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import world.meta.sns.entity.Board;
import world.meta.sns.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
public class BoardRepositoryTest {

    @Autowired BoardRepository boardRepository;
    @Autowired MemberRepository memberRepository;

    @Test
    public void boardTest() throws Exception {

        Member member = memberRepository.findByMemberName("member1");
    }

}
