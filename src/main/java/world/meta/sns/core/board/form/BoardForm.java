package world.meta.sns.core.board.form;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardForm {

    private String title;
    private String writer;
    private LocalDateTime startDate; // 게시글 생성일 검색 시작 시각 (String으로 받아야 하나?)
    private LocalDateTime endDate; // 게시글 생성일 검색 종료 시각

}
