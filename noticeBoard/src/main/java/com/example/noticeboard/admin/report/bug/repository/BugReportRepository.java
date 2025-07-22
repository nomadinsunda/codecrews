package com.example.noticeboard.admin.report.bug.repository;

import com.example.noticeboard.admin.report.bug.entity.BugReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BugReportRepository extends JpaRepository<BugReport,Long>, CustomBugReportRepository {

    Optional<BugReport> findByBugReportId(long reportId);

    long countBy();

}
