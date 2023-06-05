package world.meta.sns.core.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import world.meta.sns.api.exception.CustomAccessDeniedException;
import world.meta.sns.core.board.repository.BoardRepository;
import world.meta.sns.core.comment.repository.CommentRepository;
import world.meta.sns.core.member.dto.MemberDto;
import world.meta.sns.core.member.dto.MemberJoinDto;
import world.meta.sns.core.member.dto.MemberUpdateDto;
import world.meta.sns.core.member.entity.Member;
import world.meta.sns.core.member.form.MemberSearchForm;
import world.meta.sns.core.member.repository.MemberRepository;

import java.util.List;

import static world.meta.sns.api.common.enums.ErrorResponseCodes.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 회원 목록 조회
     *
     * @param memberSearchForm the member search form
     * @param pageable         the pageable
     * @return the page
     */
    @Transactional(readOnly = true)
    public Page<MemberDto> findMemberList(MemberSearchForm memberSearchForm, Pageable pageable) {

        Page<Member> results = memberRepository.findAll(memberSearchForm, pageable);
        List<Member> members = results.getContent();

        List<MemberDto> memberDtos = members.stream().map(MemberDto::from).toList();

        return new PageImpl<>(memberDtos, pageable, results.getTotalElements());
    }

    /**
     * 회원 단건 조회
     *
     * @param memberId the member id
     * @return the member dto
     */
    @Transactional(readOnly = true)
    public MemberDto findMember(Long memberId) {

        Member member = memberRepository.findById(memberId).orElse(null);

        if (member == null) {
            return null;
        }

        return MemberDto.from(member);
    }

    /**
     * 회원 등록
     *
     * @param memberJoinDto the member request dto
     */
    public void joinMember(MemberJoinDto memberJoinDto) {

        Long count = memberRepository.countMemberByEmail(memberJoinDto.getEmail());
        if (count > 0) {
            throw new CustomAccessDeniedException(MEMBER_ALREADY_EXISTED.getNumber(), MEMBER_ALREADY_EXISTED.getMessage());
        }

        memberJoinDto.setPassword(bCryptPasswordEncoder.encode(memberJoinDto.getPassword()));

        Member member = Member.from(memberJoinDto);
        memberRepository.save(member);
    }

    /**
     * 회원 수정
     *
     * @param memberId        the member id
     * @param memberUpdateDto the member update dto
     */
    public void updateMember(Long memberId, MemberUpdateDto memberUpdateDto) {

        Member member = memberRepository.findById(memberId).orElse(null);

        if (member == null) {
            return;
        }

        member.update(memberUpdateDto); // transaction 끝나면 더티체킹 후 자동으로 update 쿼리 실행
    }

    /**
     * 회원 삭제
     * 연관된 게시글, 부모/자식 댓글까지 삭제
     *
     * @param memberId the member id
     */
    public void deleteMember(Long memberId) {

        // 1. 회원이 작성한 모든 부모/자식 댓글 삭제
        List<Long> parentCommentIds = commentRepository.findParentCommentIdsByMemberId(memberId);
        commentRepository.deleteChildCommentsByParentCommentIds(parentCommentIds); // 자식 댓글부터 삭제해야 참조 무결성이 깨지지 않는다.
        commentRepository.deleteCommentsByMemberId(memberId); // 부모 댓글 삭제

        // 2. 회원이 작성한 모든 게시글 삭제
        boardRepository.deleteByMemberId(memberId);

        // 3. 회원 삭제
        memberRepository.deleteById(memberId);
    }


}
