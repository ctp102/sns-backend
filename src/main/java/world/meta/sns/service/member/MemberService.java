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
import world.meta.sns.repository.board.BoardRepository;
import world.meta.sns.repository.comment.CommentRepository;
import world.meta.sns.repository.member.MemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public Page<MemberDto> findMemberList(MemberSearchForm memberSearchForm, Pageable pageable) {

        Page<Member> results = memberRepository.findAll(memberSearchForm, pageable);
        List<Member> members = results.getContent();

        List<MemberDto> memberDtos = members.stream().map(MemberDto::from).toList();

        return new PageImpl<>(memberDtos, pageable, results.getTotalElements());
    }

    @Transactional(readOnly = true)
    public MemberDto findMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElse(null);

        if (member == null) {
            return null;
        }

        return MemberDto.from(member);
    }

    public void saveMember(MemberRequestDto requestDto) {

        Long count = memberRepository.countMemberByMemberName(requestDto.getMemberName());
        if (count > 0) {
            throw new IllegalStateException("이미 존재하는 회원입니다."); // TODO: [2023-04-25] Exception 공통 처리하기. 현재는 500 에러뜬다
        }

        Member member = Member.from(requestDto);
        memberRepository.save(member);
    }

    public void updateMember(Long memberId, MemberRequestDto requestDto) {
        Member member = memberRepository.findById(memberId).orElse(null);

        if (member == null) {
            return;
        }

        member.update(requestDto); // transaction 끝나면 더티체킹 후 자동으로 update 쿼리 실행
    }

    public void deleteMember(Long memberId) {

        // 1. 회원이 작성한 모든 부모/자식 댓글 삭제
        List<Long> parentCommentIds = commentRepository.findParentCommentIdsByMemberId(memberId);
        commentRepository.deleteChildCommentsByParentCommentIds(parentCommentIds); // 자식 댓글부터 삭제해야 참조 무결성이 깨지지 않는다.
        commentRepository.deleteParentCommentsByMemberId(memberId); // 부모 댓글 삭제

        // 2. 회원이 작성한 모든 게시글 삭제
        boardRepository.deleteAllByMemberId(memberId);

        // 3. 회원 삭제
        memberRepository.deleteAllById(memberId);
    }


}
