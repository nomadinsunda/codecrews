package com.example.noticeboard.admin.report.content.repository;

import com.example.noticeboard.account.user.domain.QUser;
import com.example.noticeboard.admin.report.content.dto.ContentReportResponse;
import com.example.noticeboard.admin.report.content.dto.ReportDataResponse;
import com.example.noticeboard.admin.report.content.entity.QContentReport;
import com.example.noticeboard.admin.report.content.entity.QReportData;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class ContentReportRepositoryImpl implements CustomContentReportRepository {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<ContentReportResponse> findAllContentReport(Pageable pageable) {
        QUser targetUser = new QUser("user2");

        return jpaQueryFactory.select(
                        Projections.constructor(
                                ContentReportResponse.class,
                                QContentReport.contentReport.contentReportId,
                                QUser.user.id,
                                QContentReport.contentReport.reportTime,
                                QContentReport.contentReport.content,
                                QContentReport.contentReport.isAction,
                                QContentReport.contentReport.reportType,
                                Projections.constructor(
                                        ReportDataResponse.class,
                                        QReportData.reportData.reportDataId,
                                        targetUser.id,
                                        QReportData.reportData.title,
                                        QReportData.reportData.content
                                )
                        )
                )
                .from(QContentReport.contentReport)
                .innerJoin(QContentReport.contentReport.reporter, QUser.user)
                .innerJoin(QContentReport.contentReport.reportData, QReportData.reportData)
                .innerJoin(QReportData.reportData.target, targetUser)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(QContentReport.contentReport.reportTime.desc())
                .fetch();
    }
}
