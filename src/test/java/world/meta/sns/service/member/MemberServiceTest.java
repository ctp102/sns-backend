package world.meta.sns.service.member;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Slf4j
public class MemberServiceTest {

    @Autowired private MemberService memberService;

    @Test
    public void aa() throws Exception {
        memberService.findMember(1L);
    }

}
