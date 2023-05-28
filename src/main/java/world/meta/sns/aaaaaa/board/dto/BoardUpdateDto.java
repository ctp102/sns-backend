package world.meta.sns.aaaaaa.board.dto;

import lombok.Data;
import world.meta.sns.aaaaaa.board.enums.Category;

@Data
public class BoardUpdateDto {

    private String title;
    private String content;
    private Category category;

}
