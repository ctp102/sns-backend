package world.meta.sns.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import world.meta.sns.api.common.mvc.CustomResponse;
import world.meta.sns.api.security.core.userdetails.PrincipalDetails;
import world.meta.sns.core.comment.dto.CommentRequestDto;
import world.meta.sns.core.comment.dto.CommentUpdateDto;
import world.meta.sns.core.comment.service.CommentService;

@RestController
@RequiredArgsConstructor
public class CommentRestController {

    private final CommentService commentService;

    @PostMapping("/api/v1/boards/{boardId}/comments")
    public CustomResponse saveComment(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("boardId") Long boardId, 
                                      @RequestBody CommentRequestDto commentRequestDto) {

        commentRequestDto.setMemberId(principalDetails.getMember().getId());
        commentRequestDto.setBoardId(boardId);

        commentService.saveComment(commentRequestDto);

        return new CustomResponse.Builder().build();
    }

    @PutMapping("/api/v1/comments/{commentId}")
    public CustomResponse updateComment(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("commentId") Long commentId,
                                        @RequestBody CommentUpdateDto commentUpdateDto) {

        commentUpdateDto.setMemberId(principalDetails.getMember().getId());
        commentService.updateComment(commentId, commentUpdateDto);

        return new CustomResponse.Builder().build();
    }

    @DeleteMapping("/api/v1/comments/{commentId}")
    public CustomResponse deleteComment(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("commentId") Long commentId) {

        commentService.deleteComment(commentId, principalDetails.getMember().getId());

        return new CustomResponse.Builder().build();
    }

}
