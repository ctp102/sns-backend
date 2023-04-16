package world.meta.sns.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import world.meta.sns.entity.Comment;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class CommentDto {

    private Long parentCommentId;
    private Long id;
    private List<CommentDto> childComments;
    private String content;

    public static CommentDto from(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .childComments(new ArrayList<>())
                .build();
    }

}
