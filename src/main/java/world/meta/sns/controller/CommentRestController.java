package world.meta.sns.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import world.meta.sns.dto.comment.CommentRequestDto;
import world.meta.sns.dto.comment.CommentUpdateDto;
import world.meta.sns.mvc.view.CustomResponse;
import world.meta.sns.service.comment.CommentService;

@RestController
@RequiredArgsConstructor
public class CommentRestController {

    private final CommentService commentService;

    // 원래 세션에서 memberId를 가져와야 함. 그게 구현이 안되어 있으므로 RequestParam으로 잠시 받음.
    @PostMapping("/api/v1/boards/{boardId}/comments")
    public CustomResponse saveComment(@PathVariable("boardId") Long boardId, @RequestParam("memberId") Long memberId, @RequestBody CommentRequestDto commentRequestDto) {

        commentRequestDto.setBoardId(boardId);
        commentRequestDto.setMemberId(memberId);

        commentService.saveComment(commentRequestDto);

        return new CustomResponse.Builder().build();
    }

    @PutMapping("/api/v1/comments/{commentId}")
    public CustomResponse updateComment(@PathVariable("commentId") Long commentId, @RequestBody CommentUpdateDto commentUpdateDto) {
        commentService.updateComment(commentId, commentUpdateDto);

        return new CustomResponse.Builder().build();
    }

    @DeleteMapping("/api/v1/comments/{commentId}")
    public CustomResponse deleteComment(@PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId);

        return new CustomResponse.Builder().build();
    }

}
