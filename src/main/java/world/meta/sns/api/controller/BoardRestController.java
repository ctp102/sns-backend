package world.meta.sns.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import world.meta.sns.api.common.mvc.CustomResponse;
import world.meta.sns.api.security.core.userdetails.PrincipalDetails;
import world.meta.sns.core.board.dto.BoardDto;
import world.meta.sns.core.board.dto.BoardRequestDto;
import world.meta.sns.core.board.dto.BoardUpdateDto;
import world.meta.sns.core.board.form.BoardForm;
import world.meta.sns.core.board.service.BoardService;

@RestController
@RequiredArgsConstructor
@Slf4j
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
    public CustomResponse saveBoard(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody BoardRequestDto requestDto) {

        requestDto.setMemberId(principalDetails.getMember().getId());
        boardService.saveBoard(requestDto);

        return new CustomResponse.Builder().build();
    }

    @PutMapping("/api/v1/boards/{boardId}")
    public CustomResponse updateBoard(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("boardId") Long boardId,
                                      @RequestBody BoardUpdateDto boardUpdateDto) {

        boardUpdateDto.setMemberId(principalDetails.getMember().getId());
        boardService.updateBoard(boardId, boardUpdateDto);

        return new CustomResponse.Builder().build();
    }

    @DeleteMapping("/api/v1/boards/{boardId}")
    public CustomResponse deleteBoard(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("boardId") Long boardId) {

        boardService.deleteBoard(boardId, principalDetails.getMember().getId());

        return new CustomResponse.Builder().build();
    }

}
