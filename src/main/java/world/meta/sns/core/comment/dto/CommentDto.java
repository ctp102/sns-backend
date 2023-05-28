package world.meta.sns.core.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import world.meta.sns.core.comment.entity.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class CommentDto {

    private Long parentCommentId;
    private Long id;

    private String content;
    private List<CommentDto> childComments;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public static CommentDto from(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .childComments(new ArrayList<>())
                .createdDate(comment.getCreatedDate())
                .updatedDate(comment.getUpdatedDate())
                .build();
    }

}
