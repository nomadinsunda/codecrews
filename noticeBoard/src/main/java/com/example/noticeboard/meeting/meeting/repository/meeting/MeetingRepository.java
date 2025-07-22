package com.example.noticeboard.meeting.meeting.repository.meeting;

import com.example.noticeboard.meeting.meeting.domain.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<Meeting, Long> , CustomMeetingRepository {


}
