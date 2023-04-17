package world.meta.sns.dto.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import world.meta.sns.dto.comment.CommentDto;
import world.meta.sns.entity.Board;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class BoardDto {

    private Long boardId;
    private String title;
    private String content;
    private String writer;
    private List<CommentDto> commentDtos;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public BoardDto(Long boardId, String title, String content, String writer, LocalDateTime createdDate, LocalDateTime updatedDate) {
        this.boardId = boardId;
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

    public static void setCommentDtos(Board board, BoardDto boardDto) {

        List<CommentDto> commentDtos = new ArrayList<>();
        Map<Long, CommentDto> map = new HashMap<>();

        board.getComments().forEach(comment -> {
            CommentDto commentDto = CommentDto.from(comment);
            if (comment.getParentComment() != null) {
                commentDto.setParentCommentId(comment.getParentComment().getId());
            }

            map.put(commentDto.getId(), commentDto);

            if (comment.getParentComment() != null ) {
                map.get(comment.getParentComment().getId()).getChildComments().add(commentDto);
            } else {
                commentDtos.add(commentDto);
            }
        });

        boardDto.setCommentDtos(commentDtos);
    }

}
