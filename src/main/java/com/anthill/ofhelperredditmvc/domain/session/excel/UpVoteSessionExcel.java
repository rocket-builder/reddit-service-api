package com.anthill.ofhelperredditmvc.domain.session.excel;

import com.anthill.ofhelperredditmvc.domain.Proxy;
import lombok.*;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@Data
public class UpVoteSessionExcel extends AbstractSessionExcel {

    private String postUrl;

    private int upVoteCount;

    private Proxy proxy;
}
