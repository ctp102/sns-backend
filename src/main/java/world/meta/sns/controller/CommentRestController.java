package world.meta.sns.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import world.meta.sns.dto.comment.CommentRequestDto;
import world.meta.sns.entity.Comment;
import world.meta.sns.service.comment.CommentService;

@RestController
@RequiredArgsConstructor
public class CommentRestController {

    private final CommentService commentService;



    // TODO: [2023-04-16] 원래 세션에서 memberId를 가져와야 함. 그게 구현이 안되어 있으므로 RequestParam으로 잠시 받음.
    @PostMapping("/api/v1/boards/{boardId}/comments")
    public void createComment(@PathVariable("boardId") Long boardId, @RequestParam("memberId") Long memberId, @RequestBody CommentRequestDto commentRequestDto) {

        commentRequestDto.setBoardId(boardId);
        commentRequestDto.setMemberId(memberId);

        commentService.saveComment(commentRequestDto);
    }

    @PutMapping("/api/v1/comments/{commentId}")
    public void updateComment(@PathVariable("commentId") Long commentId, @RequestBody CommentRequestDto commentRequestDto) {
//        commentRequestDto.setCommentId(commentId);
//        commentService.updateComment(commentRequestDto);
    }

    @DeleteMapping("/api/v1/comments/{commentId}")
    public void deleteComment(@PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId);
    }

}
