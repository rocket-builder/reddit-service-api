package com.anthill.ofhelperredditmvc.domain.dto;

import com.anthill.ofhelperredditmvc.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserProfileDto {

    private String login;
    private List<Role> roles;
    private long totalKarma, totalAccounts, totalPosts, totalUpVotes;
}
