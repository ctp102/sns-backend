package world.meta.sns.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import world.meta.sns.api.common.mvc.CustomResponse;
import world.meta.sns.api.security.core.userdetails.PrincipalDetails;
import world.meta.sns.core.board.dto.BoardDeleteDto;
import world.meta.sns.core.board.dto.BoardDto;
import world.meta.sns.core.board.dto.BoardRequestDto;
import world.meta.sns.core.board.dto.BoardUpdateDto;
import world.meta.sns.core.board.form.BoardForm;
import world.meta.sns.core.board.service.BoardService;

import static world.meta.sns.api.common.enums.ErrorResponseCodes.MEMBER_RESOURCE_FORBIDDEN;

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
    public CustomResponse saveBoard(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody BoardRequestDto boardRequestDto) {

        if (!principalDetails.getMember().getId().equals(boardRequestDto.getMemberId())) {
            log.error("[saveBoard] 해당 사용자는 접근 권한이 없습니다.");
            return new CustomResponse.Builder(MEMBER_RESOURCE_FORBIDDEN).build();
        }
        boardService.saveBoard(boardRequestDto);

        return new CustomResponse.Builder().build();
    }

    @PutMapping("/api/v1/boards/{boardId}")
    public CustomResponse updateBoard(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("boardId") Long boardId,
                                      @RequestBody BoardUpdateDto boardUpdateDto) {

        if (!principalDetails.getMember().getId().equals(boardUpdateDto.getMemberId())) {
            log.error("[updateBoard] 해당 사용자는 접근 권한이 없습니다.");
            return new CustomResponse.Builder(MEMBER_RESOURCE_FORBIDDEN).build();
        }

        boardUpdateDto.setMemberId(principalDetails.getMember().getId());
        boardService.updateBoard(boardId, boardUpdateDto);

        return new CustomResponse.Builder().build();
    }

    @DeleteMapping("/api/v1/boards/{boardId}")
    public CustomResponse deleteBoard(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("boardId") Long boardId,
                                      @RequestBody BoardDeleteDto boardDeleteDto) {

        if (!principalDetails.getMember().getId().equals(boardDeleteDto.getMemberId())) {
            log.error("[deleteBoard] 해당 사용자는 접근 권한이 없습니다.");
            return new CustomResponse.Builder(MEMBER_RESOURCE_FORBIDDEN).build();
        }

        boardService.deleteBoard(boardId, principalDetails.getMember().getId());

        return new CustomResponse.Builder().build();
    }

}
