package com.anthill.ofhelperredditmvc.domain.dto;

import com.anthill.ofhelperredditmvc.domain.session.group.UpVoteGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpVoteGroupDto {

    private String login;
    private UpVoteGroup group;
}
