package com.example.noticeboard.post.comment.repository;

import com.example.noticeboard.account.user.domain.User;
import com.example.noticeboard.post.comment.domain.Comment;
import com.example.noticeboard.post.comment.dto.CommentResponse;

import java.util.List;
import java.util.Optional;

public interface CustomCommentRepository {

    Optional<Comment> findCommentByCommentIdAndUserId(long commentId , String userId);

    List<CommentResponse> findCommentByPostId(long postId);

    List<CommentResponse> findCommentByCommentPostWithoutMe(User user);
}
