package com.ddangme.sns.controller;

import com.ddangme.sns.controller.request.PostCommentRequest;
import com.ddangme.sns.controller.request.PostCreateRequest;
import com.ddangme.sns.controller.request.PostModifyRequest;
import com.ddangme.sns.controller.response.CommentResponse;
import com.ddangme.sns.controller.response.PostResponse;
import com.ddangme.sns.controller.response.Response;
import com.ddangme.sns.exception.ErrorCode;
import com.ddangme.sns.exception.SnsApplicationException;
import com.ddangme.sns.model.Post;
import com.ddangme.sns.model.User;
import com.ddangme.sns.service.PostService;
import com.ddangme.sns.util.ClassUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public Response<Void> create(@RequestBody PostCreateRequest request, Authentication authentication) {
        User loginUser = getLoginUser(authentication);
        postService.create(request.getTitle(), request.getBody(), loginUser);

        return Response.success();
    }

    @PutMapping("/{postId}")
    public Response<PostResponse> modify(@PathVariable Integer postId, @RequestBody PostModifyRequest request, Authentication authentication) {
        User loginUser = getLoginUser(authentication);
        Post post = postService.modify(postId, request.getTitle(), request.getBody(), loginUser);

        return Response.success(PostResponse.fromPost(post));
    }

    @DeleteMapping("/{postId}")
    public Response<Void> delete(@PathVariable Integer postId, Authentication authentication) {
        User loginUser = getLoginUser(authentication);
        postService.delete(loginUser, postId);

        return Response.success();
    }

    @GetMapping
    public Response<Page<PostResponse>> list(Pageable pageable) {
        return Response.success(postService.feedList(pageable).map(PostResponse::fromPost));
    }

    @GetMapping("my")
    public Response<Page<PostResponse>> my(Pageable pageable, Authentication authentication) {
        User loginUser = getLoginUser(authentication);
        return Response.success(postService.myFeedList(loginUser, pageable).map(PostResponse::fromPost));
    }

    @PostMapping("/{postId}/likes")
    public Response<Void> like(@PathVariable Integer postId, Authentication authentication) {
        User loginUser = getLoginUser(authentication);
        postService.like(postId, loginUser);

        return Response.success();
    }

    @GetMapping("/{postId}/likes")
    public Response<Long> likeCount(@PathVariable Integer postId) {
        return Response.success(postService.likeCount(postId));
    }

    @PostMapping("/{postId}/comments")
    public Response<Void> comment(@PathVariable Integer postId, @RequestBody PostCommentRequest request, Authentication authentication) {
        User loginUser = getLoginUser(authentication);
        postService.comment(postId, loginUser, request.getComment());

        return Response.success();
    }

    @GetMapping("/{postId}/comments")
    public Response<Page<CommentResponse>> comment(@PathVariable Integer postId, Pageable pageable) {
        Page<CommentResponse> comments = postService.getComments(postId, pageable).map(CommentResponse::fromComment);

        return Response.success(comments);
    }

    private User getLoginUser(Authentication authentication) {
        return ClassUtils.getSafeCastInstance(authentication.getPrincipal(), User.class)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "Casting to User class failed"));
    }
}
