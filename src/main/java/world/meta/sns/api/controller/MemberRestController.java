package world.meta.sns.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import world.meta.sns.api.common.mvc.CustomResponse;
import world.meta.sns.api.exception.CustomAccessDeniedException;
import world.meta.sns.api.security.core.userdetails.PrincipalDetails;
import world.meta.sns.core.member.dto.MemberDto;
import world.meta.sns.core.member.dto.MemberJoinDto;
import world.meta.sns.core.member.dto.MemberUpdateDto;
import world.meta.sns.core.member.form.MemberSearchForm;
import world.meta.sns.core.member.service.MemberService;

import static world.meta.sns.api.common.enums.ErrorResponseCodes.*;

@RestController
@RequiredArgsConstructor
@Slf4j
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

    // TODO: [2023-06-06] 패스워드 변경 추가하기
    @PutMapping("/api/v1/members/{memberId}")
    public CustomResponse updateMember(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("memberId") Long memberId,
                                       @RequestBody MemberUpdateDto memberUpdateDto) {

        if (principalDetails.getMember().getId().equals(memberId)) {
            log.error("[updateMember] 해당 사용자는 접근 권한이 없습니다.");
            throw new CustomAccessDeniedException(MEMBER_RESOURCE_FORBIDDEN.getNumber(), MEMBER_RESOURCE_FORBIDDEN.getMessage());
        }

        memberService.updateMember(memberId, memberUpdateDto);

        return new CustomResponse.Builder().build();
    }

    @DeleteMapping("/api/v1/members/{memberId}")
    public CustomResponse deleteMember(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("memberId") Long memberId) {

        if (principalDetails.getMember().getId().equals(memberId)) {
            log.error("[deleteMember] 해당 사용자는 접근 권한이 없습니다.");
            throw new CustomAccessDeniedException(MEMBER_RESOURCE_FORBIDDEN.getNumber(), MEMBER_RESOURCE_FORBIDDEN.getMessage());
        }

        memberService.deleteMember(memberId);

        return new CustomResponse.Builder().build();
    }

}
