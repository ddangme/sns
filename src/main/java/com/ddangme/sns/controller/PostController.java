package com.ddangme.sns.controller;

import com.ddangme.sns.controller.request.PostCreateRequest;
import com.ddangme.sns.controller.request.PostModifyRequest;
import com.ddangme.sns.controller.response.PostResponse;
import com.ddangme.sns.controller.response.Response;
import com.ddangme.sns.model.Post;
import com.ddangme.sns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public Response<Void> create(@RequestBody PostCreateRequest request, Authentication authentication) {
        postService.create(request.getTitle(), request.getBody(), authentication.getName());

        return Response.success();
    }

    @PutMapping("/{postId}")
    public Response<PostResponse> modify(@PathVariable Integer postId, @RequestBody PostModifyRequest request, Authentication authentication) {
        Post post = postService.modify(authentication.getName(), postId, request.getTitle(), request.getBody());

        return Response.success(PostResponse.formPost(post));
    }

    @DeleteMapping("/{postId}")
    public Response<Void> delete(@PathVariable Integer postId, Authentication authentication) {
        postService.delete(authentication.getName(), postId);

        return Response.success();
    }

}
