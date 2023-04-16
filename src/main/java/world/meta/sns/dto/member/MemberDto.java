package world.meta.sns.dto.member;

import lombok.Data;
import world.meta.sns.dto.board.BoardDto;
import world.meta.sns.entity.Board;
import world.meta.sns.entity.Member;

import java.util.List;

@Data
public class MemberDto {

    private String memberName;
//    private List<BoardDto> boardDtos;
    private List<Board> boards;

    public static MemberDto from(Member member) {
        MemberDto memberDto = new MemberDto();
        memberDto.setMemberName(member.getMemberName());
        memberDto.setBoards(member.getBoards());
        return memberDto;
    }
}
