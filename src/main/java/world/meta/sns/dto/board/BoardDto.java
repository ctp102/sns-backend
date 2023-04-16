package world.meta.sns.dto.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import world.meta.sns.dto.comment.CommentDto;
import world.meta.sns.entity.Board;
import world.meta.sns.entity.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class BoardDto {

    private String title;
    private String content;
    private String writer;
//    private List<Comment> comments;
    private List<CommentDto> commentDtos;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public BoardDto(String title, String content, String writer, LocalDateTime createdDate, LocalDateTime updatedDate) {
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    public static BoardDto from(Board board) {
        return BoardDto.builder()
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getMember().getMemberName())
                .commentDtos(new ArrayList<>())
                .createdDate(board.getCreatedDate())
                .updatedDate(board.getUpdatedDate())
                .build();
    }

}
