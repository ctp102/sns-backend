package world.meta.sns.repository.member;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import world.meta.sns.core.member.entity.Member;
import world.meta.sns.core.member.repository.MemberRepository;

import static org.assertj.core.api.Assertions.assertThat;

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
            assertThat(findMember.getName()).isEqualTo(saveMember.getName());
            assertThat(findMember).isEqualTo(saveMember);
        });
    }

    @Test
    public void oneToMany() throws Exception {

        // given


        // when

        // then
    }

}