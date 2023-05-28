package world.meta.sns.aaaaaa.member.dto;

import lombok.Data;
import world.meta.sns.aaaaaa.member.entity.Member;

import java.util.ArrayList;
import java.util.List;

@Data
public class MemberDto {

    private String email;
    private String name;
    private List<MemberBoardDto> memberBoardDtos = new ArrayList<>(); // 회원이 작성한 게시글 목록만 조회하면 되므로 comments 제외

    public static MemberDto from(Member member) {
        MemberDto memberDto = new MemberDto();
        memberDto.setEmail(member.getEmail());
        memberDto.setName(member.getName());

        member.getBoards().forEach(board -> {
            MemberBoardDto memberBoardDto = MemberBoardDto.from(board);
            memberDto.getMemberBoardDtos().add(memberBoardDto);
        });

        return memberDto;
    }
}
