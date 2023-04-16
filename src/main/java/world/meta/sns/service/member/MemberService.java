package world.meta.sns.service.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import world.meta.sns.dto.member.MemberDto;
import world.meta.sns.entity.Board;
import world.meta.sns.entity.Member;
import world.meta.sns.form.member.MemberForm;
import world.meta.sns.repository.member.MemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Page<MemberDto> findMemberList(MemberForm memberForm, Pageable pageable) {
        Page<Member> results = memberRepository.findAll(memberForm, pageable);

        List<MemberDto> memberDtos = results.getContent().stream().map(MemberDto::from).toList();

        return null;
    }

    @Transactional(readOnly = true)
    public MemberDto findMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        List<Board> boards = member.getBoards();
        log.info("boards: {}", boards);

        return MemberDto.from(member);
    }

}
