package com.anthill.ofhelperredditmvc.domain.session.excel;

import com.anthill.ofhelperredditmvc.domain.session.WorkStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
public abstract class AbstractSessionExcel {

    protected long sessionId;
    protected int rowNumber;

    protected WorkStatus status;
    protected String message;

    public boolean isError(){
        return getStatus().equals(WorkStatus.ERROR);
    }

    public void setError(String message, int rowNumber){
        setMessage(message);
        setRowNumber(rowNumber);
        setStatus(WorkStatus.ERROR);
    }
}
