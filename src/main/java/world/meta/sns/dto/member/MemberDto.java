package world.meta.sns.dto.member;

import lombok.Data;
import world.meta.sns.entity.Member;

import java.util.ArrayList;
import java.util.List;

@Data
public class MemberDto {

    private String memberEmail;
    private String memberName;
    private List<MemberBoardDto> memberBoardDtos = new ArrayList<>(); // 회원이 작성한 게시글 목록만 조회하면 되므로 comments 제외

    public static MemberDto from(Member member) {
        MemberDto memberDto = new MemberDto();
        memberDto.setMemberEmail(member.getMemberEmail());
        memberDto.setMemberName(member.getMemberName());

        member.getBoards().forEach(board -> {
            MemberBoardDto memberBoardDto = MemberBoardDto.from(board);
            memberDto.getMemberBoardDtos().add(memberBoardDto);
        });

        return memberDto;
    }
}
