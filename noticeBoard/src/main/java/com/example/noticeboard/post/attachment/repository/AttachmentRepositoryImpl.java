package com.example.noticeboard.post.attachment.repository;

import com.example.noticeboard.post.attachment.dto.AttachmentResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.noticeboard.post.attachment.domain.QAttachment.attachment;
import static com.example.noticeboard.post.post.domain.QPost.post;

@RequiredArgsConstructor
public class AttachmentRepositoryImpl implements CustomAttachmentRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<AttachmentResponse> findAttachmentsByPostId(long postId) {
        return queryFactory.select(Projections.constructor(AttachmentResponse.class, attachment.attachmentId
                        , attachment.realFileName , attachment.s3Url , attachment.fileSize , attachment.uuidFileName))
                .from(attachment)
                .innerJoin(attachment.post , post).on(post.postId.eq(postId))
                .fetch();
    }
}
