package com.example.noticeboard.post.comment.dto;

import lombok.*;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequest {
    private String content;
    private long postId;
}
