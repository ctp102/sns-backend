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
        
        // TODO: [2023-04-20] save 성공 시, 아닐 시 나눠서 처리 
//        if (item == null) {
//            return new CustomResponse.Builder().add(CustomResponseCodes.FAIL).build();
//        }

        return new CustomResponse.Builder().addItems(item).build();
    }

}
