package world.meta.sns.core.board.dto;

import lombok.Getter;
import lombok.Setter;
import world.meta.sns.core.board.enums.Category;

@Setter
@Getter
public class BoardRequestDto {

    private Long memberId;
    private String title;
    private String content;
    private Category category;

}
