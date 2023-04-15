package world.meta.sns.repository;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import world.meta.sns.entity.Member;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void memberTest() throws Exception {

        // given
        Member member = new Member("memberA");

        // when
        Member saveMember = memberRepository.save(member);

        // then
        memberRepository.findById(saveMember.getId()).ifPresent(findMember -> {
            assertThat(findMember.getId()).isEqualTo(saveMember.getId());
            assertThat(findMember.getMemberName()).isEqualTo(saveMember.getMemberName());
            assertThat(findMember).isEqualTo(saveMember);
        });
    }

}