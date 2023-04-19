package world.meta.sns.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import world.meta.sns.entity.Board;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class MemberBoardDto {

    private Long boardId;
    private String title;
    private String content;
    private String writer;

    public static MemberBoardDto from(Board board) {
        return MemberBoardDto.builder()
                .boardId(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getMember().getMemberName())
                .build();
    }

}
