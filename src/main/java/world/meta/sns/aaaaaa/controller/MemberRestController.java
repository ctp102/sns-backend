package world.meta.sns.aaaaaa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import world.meta.sns.aaaaaa.member.dto.MemberDto;
import world.meta.sns.aaaaaa.member.dto.MemberJoinDto;
import world.meta.sns.aaaaaa.member.dto.MemberUpdateDto;
import world.meta.sns.aaaaaa.member.form.MemberSearchForm;
import world.meta.sns.aaaaaa.common.mvc.CustomResponse;
import world.meta.sns.aaaaaa.member.service.MemberService;

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

    // TODO: [2023-05-28] 추후 @Vaild 적용하기
    @PostMapping("/api/v1/members/join")
    public CustomResponse joinMember(@RequestBody MemberJoinDto memberJoinDto) {   

        memberService.joinMember(memberJoinDto);

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
