package world.meta.sns.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import world.meta.sns.entity.Board;
import world.meta.sns.dto.BoardDto;
import world.meta.sns.form.BoardForm;
import world.meta.sns.service.BoardService;

@RestController
@RequiredArgsConstructor
public class BoardRestController {

    private final BoardService boardService;

    // TODO: [2023-04-15] ResponseEntity 또는 커스텀 Response 생성해서 처리하기
    @GetMapping("/api/v1/boards")
    public Page<BoardDto> findBoardList(BoardForm boardForm, Pageable pageable) {
        return boardService.findBoardList(boardForm, pageable);
    }

    @GetMapping("/api/v1/boards/{boardId}")
    public BoardDto findBoard(@PathVariable("boardId") Long boardId) {
        return boardService.findBoard(boardId);
    }

    @PostMapping("/api/v1/boards/members/{memberId}")
    public void saveBoard(@PathVariable("memberId") Long memberId, Board board) {
        boardService.saveBoard(memberId, board);
    }

    @PutMapping("/api/v1/boards/{boardId}")
    public void updateBoard(@PathVariable("boardId") Long boardId, Board board) {
        boardService.updateBoard(boardId, board);
    }

    @DeleteMapping("/api/v1/boards/{boardId}")
    public void deleteBoard(@PathVariable("boardId") Long boardId) {
        boardService.deleteBoard(boardId);
    }

}
