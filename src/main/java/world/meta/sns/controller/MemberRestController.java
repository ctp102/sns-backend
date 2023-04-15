package world.meta.sns.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import world.meta.sns.dto.member.MemberDto;
import world.meta.sns.form.member.MemberForm;
import world.meta.sns.service.member.MemberService;

@RestController
@RequiredArgsConstructor
public class MemberRestController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public Page<MemberDto> findMemberList(MemberForm memberForm, Pageable pageable) {
        return memberService.findMemberList(memberForm, pageable);
    }

    @GetMapping("/api/v1/members/{memberId}")
    public MemberDto findBoard(@PathVariable("memberId") Long memberId) {
        return memberService.findMember(memberId);
    }

}
