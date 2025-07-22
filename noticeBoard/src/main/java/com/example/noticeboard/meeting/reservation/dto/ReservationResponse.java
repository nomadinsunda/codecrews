package com.example.noticeboard.meeting.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResponse {

    private long reservationId;

    @JsonFormat(shape = JsonFormat.Shape.STRING , pattern = "yyyy년MM월dd일 HH시MM분" , timezone = "Asia/Seoul")
    private LocalDateTime date;

    private String address;

    private String description;

    private String detailAddress;

    private String locateX;

    private String locateY;

    private int maxParticipants;

    private long userCount;

}
