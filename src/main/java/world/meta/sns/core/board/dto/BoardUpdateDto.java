package world.meta.sns.core.board.dto;

import lombok.Data;
import world.meta.sns.core.board.enums.Category;

@Data
public class BoardUpdateDto {

    private String title;
    private String content;
    private Category category;

}
