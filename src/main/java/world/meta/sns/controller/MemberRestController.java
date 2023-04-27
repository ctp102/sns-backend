package world.meta.sns.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import world.meta.sns.dto.member.MemberDto;
import world.meta.sns.dto.member.MemberRequestDto;
import world.meta.sns.dto.member.MemberUpdateDto;
import world.meta.sns.form.member.MemberSearchForm;
import world.meta.sns.mvc.view.CustomResponse;
import world.meta.sns.service.member.MemberService;

@RestController
@RequiredArgsConstructor
public class MemberRestController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public CustomResponse findMemberList(MemberSearchForm memberSearchForm, Pageable pageable) {
        Page<MemberDto> items = memberService.findMemberList(memberSearchForm, pageable);

        return new CustomResponse.Builder().addItems(items).build();
    }

    @GetMapping("/api/v1/members/{memberId}")
    public CustomResponse findMember(@PathVariable("memberId") Long memberId) {

        MemberDto item = memberService.findMember(memberId);

        return new CustomResponse.Builder().addItems(item).build();
    }

    @PostMapping("/api/v1/members")
    public CustomResponse saveMember(@RequestBody MemberRequestDto memberRequestDto) {

        memberService.saveMember(memberRequestDto);

        return new CustomResponse.Builder().build();
    }

    @PutMapping("/api/v1/members/{memberId}")
    public CustomResponse updateMember(@PathVariable("memberId") Long memberId, @RequestBody MemberUpdateDto memberUpdateDto) {

        memberService.updateMember(memberId, memberUpdateDto);

        return new CustomResponse.Builder().build();
    }

    @DeleteMapping("/api/v1/members/{memberId}")
    public CustomResponse deleteMember(@PathVariable("memberId") Long memberId) {

        memberService.deleteMember(memberId);

        return new CustomResponse.Builder().build();
    }

}
