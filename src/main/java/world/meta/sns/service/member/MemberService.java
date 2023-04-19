package world.meta.sns.service.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import world.meta.sns.dto.member.MemberDto;
import world.meta.sns.dto.member.MemberRequestDto;
import world.meta.sns.entity.Member;
import world.meta.sns.form.member.MemberSearchForm;
import world.meta.sns.repository.member.MemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Page<MemberDto> findMemberList(MemberSearchForm memberSearchForm, Pageable pageable) {

        Page<Member> results = memberRepository.findAll(memberSearchForm, pageable);
        List<Member> members = results.getContent();

        List<MemberDto> memberDtos = members.stream().map(MemberDto::from).toList();

        return new PageImpl<>(memberDtos, pageable, results.getTotalElements());
    }

    @Transactional(readOnly = true)
    public MemberDto findMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        return MemberDto.from(member);
    }

    public Member saveMember(MemberRequestDto requestDto) {
        Member member = Member.from(requestDto);
        return memberRepository.save(member);
    }

//    public Member updateMember(MemberRequestDto requestDto) {
//        Member member = Member.from(requestDto);
//        return memberRepository.save(member);
//    }

//    public void deleteMember(Long memberId) {
//        memberRepository.deleteById(memberId);
//    }

}
