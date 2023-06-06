package world.meta.sns.core.comment.dto;

import lombok.Data;

@Data
public class CommentUpdateDto {

    private Long memberId;
    private String content;

}
