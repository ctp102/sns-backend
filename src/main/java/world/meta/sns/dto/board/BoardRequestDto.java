package world.meta.sns.dto.board;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import world.meta.sns.enums.Category;

@Setter
@Getter
public class BoardRequestDto {

    private Long memberId;
    private String title;
    private String content;
    private Category category;


}
