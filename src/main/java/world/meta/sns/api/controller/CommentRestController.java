package world.meta.sns.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import world.meta.sns.api.common.mvc.CustomResponse;
import world.meta.sns.api.security.core.userdetails.PrincipalDetails;
import world.meta.sns.core.comment.dto.CommentDeleteDto;
import world.meta.sns.core.comment.dto.CommentRequestDto;
import world.meta.sns.core.comment.dto.CommentUpdateDto;
import world.meta.sns.core.comment.service.CommentService;

import static world.meta.sns.api.common.enums.ErrorResponseCodes.MEMBER_RESOURCE_FORBIDDEN;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentRestController {

    private final CommentService commentService;

    @PostMapping("/api/v1/boards/{boardId}/comments")
    public CustomResponse saveComment(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("boardId") Long boardId, 
                                      @RequestBody CommentRequestDto commentRequestDto) {

        if (!principalDetails.getMember().getId().equals(commentRequestDto.getMemberId())) {
            log.error("[saveComment] 해당 사용자는 접근 권한이 없습니다.");
            return new CustomResponse.Builder(MEMBER_RESOURCE_FORBIDDEN).build();
        }

        commentService.saveComment(boardId, commentRequestDto);

        return new CustomResponse.Builder().build();
    }

    @PutMapping("/api/v1/comments/{commentId}")
    public CustomResponse updateComment(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("commentId") Long commentId,
                                        @RequestBody CommentUpdateDto commentUpdateDto) {

        if (!principalDetails.getMember().getId().equals(commentUpdateDto.getMemberId())) {
            log.error("[updateComment] 해당 사용자는 접근 권한이 없습니다.");
            return new CustomResponse.Builder(MEMBER_RESOURCE_FORBIDDEN).build();
        }

        commentService.updateComment(commentId, commentUpdateDto);

        return new CustomResponse.Builder().build();
    }

    @DeleteMapping("/api/v1/comments/{commentId}")
    public CustomResponse deleteComment(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("commentId") Long commentId,
                                        @RequestBody CommentDeleteDto commentDeleteDto) {

        if (!principalDetails.getMember().getId().equals(commentDeleteDto.getMemberId())) {
            log.error("[deleteComment] 해당 사용자는 접근 권한이 없습니다.");
            return new CustomResponse.Builder(MEMBER_RESOURCE_FORBIDDEN).build();
        }

        commentService.deleteComment(commentId, principalDetails.getMember().getId());

        return new CustomResponse.Builder().build();
    }

}
