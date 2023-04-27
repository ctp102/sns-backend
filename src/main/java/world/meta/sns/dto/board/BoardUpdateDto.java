package world.meta.sns.dto.board;

import lombok.Data;
import world.meta.sns.enums.Category;

@Data
public class BoardUpdateDto {

    private String title;
    private String content;
    private Category category;

}
