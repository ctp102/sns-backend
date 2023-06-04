package world.meta.sns.core.comment.dto;

import lombok.Data;

@Data
public class CommentRequestDto {

    private Long boardId;
    private Long memberId;
    private Long parentCommentId;
    private String content;

}