package world.meta.sns.core.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import world.meta.sns.core.comment.entity.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private Long parentCommentId; // 부모 댓글 ID
    private Long id; // 현재 댓글 ID

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
