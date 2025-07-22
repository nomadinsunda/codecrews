package com.example.noticeboard.post.comment.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentRequest {
    private String content;
    private long postId;
}
