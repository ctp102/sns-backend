package world.meta.sns.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import world.meta.sns.dto.board.BoardRequestDto;
import world.meta.sns.dto.board.BoardUpdateDto;
import world.meta.sns.entity.Board;
import world.meta.sns.dto.board.BoardDto;
import world.meta.sns.form.board.BoardForm;
import world.meta.sns.mvc.view.CustomResponse;
import world.meta.sns.mvc.view.CustomResponseCodes;
import world.meta.sns.service.board.BoardService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardRestController {

    private final BoardService boardService;

    // TODO: [2023-04-28] N+1 문제 해결하기 
    @GetMapping("/api/v1/boards")
    public CustomResponse findBoardList(BoardForm boardForm, Pageable pageable) {
        Page<BoardDto> items = boardService.findBoardList(boardForm, pageable);

        return new CustomResponse.Builder().addItems(items).build();
    }

    @GetMapping("/api/v1/boards/{boardId}")
    public CustomResponse findBoard(@PathVariable("boardId") Long boardId) {
        BoardDto item = boardService.findBoard(boardId);

        return new CustomResponse.Builder().addItems(item).build();
    }

    @PostMapping("/api/v1/boards")
    public CustomResponse saveBoard(@RequestBody BoardRequestDto requestDto) {
        boardService.saveBoard(requestDto);

        return new CustomResponse.Builder().build();
    }

    @PutMapping("/api/v1/boards/{boardId}")
    public CustomResponse updateBoard(@PathVariable("boardId") Long boardId, @RequestBody BoardUpdateDto boardUpdateDto) {
        boardService.updateBoard(boardId, boardUpdateDto);

        return new CustomResponse.Builder().build();
    }

    @DeleteMapping("/api/v1/boards/{boardId}")
    public CustomResponse deleteBoard(@PathVariable("boardId") Long boardId) {
        boardService.deleteBoard(boardId);

        return new CustomResponse.Builder().build();
    }

}
