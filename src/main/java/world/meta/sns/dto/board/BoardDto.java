package world.meta.sns.dto.board;

import lombok.Data;
import world.meta.sns.entity.Board;
import world.meta.sns.entity.Comment;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BoardDto {

    private String title;
    private String content;
    private String writer;
    private List<Comment> comments;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public BoardDto() {
    }

    public BoardDto(String title, String content, String writer, LocalDateTime createdDate, LocalDateTime updatedDate) {
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    // TODO: [2023-04-16] 빌더 패턴으로 변경하기
    public static BoardDto from(Board board) {
        BoardDto boardDto = new BoardDto();
        boardDto.setTitle(board.getTitle());
        boardDto.setContent(board.getContent());
        boardDto.setWriter(board.getMember().getMemberName());
        boardDto.setCreatedDate(board.getCreatedDate());
        boardDto.setUpdatedDate(board.getUpdatedDate());
        return boardDto;
    }

}
