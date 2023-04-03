package com.anthill.ofhelperredditmvc.domain.dto.bot;

import com.anthill.ofhelperredditmvc.domain.session.WorkStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StatusDto {

    private String message;
    private WorkStatus status;
}
