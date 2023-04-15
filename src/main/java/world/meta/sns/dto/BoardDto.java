package world.meta.sns.dto;

import lombok.Data;
import world.meta.sns.entity.Board;

import java.time.LocalDateTime;

@Data
public class BoardDto {

    private String title;
    private String content;
    private String writer;
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
