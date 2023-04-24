package world.meta.sns.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import world.meta.sns.dto.member.MemberDto;
import world.meta.sns.dto.member.MemberRequestDto;
import world.meta.sns.entity.Member;
import world.meta.sns.form.member.MemberSearchForm;
import world.meta.sns.mvc.view.CustomResponse;
import world.meta.sns.mvc.view.CustomResponseCodes;
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
    public CustomResponse findBoard(@PathVariable("memberId") Long memberId) {

        MemberDto item = memberService.findMember(memberId);

        return new CustomResponse.Builder().addItems(item).build();
    }

    @PostMapping("/api/v1/members")
    public CustomResponse saveMember(@RequestBody MemberRequestDto requestDto) {

        Member item = memberService.saveMember(requestDto);

        return new CustomResponse.Builder().addItems(item).build();
    }

    @PutMapping("/api/v1/members/{memberId}")
    public CustomResponse updateMember(@PathVariable("memberId") Long memberId, @RequestBody MemberRequestDto requestDto) {

        Member item = memberService.updateMember(memberId, requestDto);
        
        // TODO: [2023-04-25] item 결과에 따라 response 응답 코드를 다르게 줘야함 

        return new CustomResponse.Builder().addItems(item).build();
    }

    @DeleteMapping("/api/v1/members/{memberId}")
    public CustomResponse deleteMember(@PathVariable("memberId") Long memberId) {

        memberService.deleteMember(memberId);

        return new CustomResponse.Builder().build();
    }

}
