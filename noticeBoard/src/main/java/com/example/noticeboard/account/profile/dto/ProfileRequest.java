package com.example.noticeboard.account.profile.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRequest {
    private String userId;
    private String phone;
    private Integer options;

} 
