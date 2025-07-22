package com.example.noticeboard.post.attachment.repository;

import com.example.noticeboard.post.attachment.dto.AttachmentResponse;

import java.util.List;

public interface CustomAttachmentRepository {
    List<AttachmentResponse> findAttachmentsByPostId(long postId);

}
