package com.anthill.ofhelperredditmvc.interfaces;

import com.anthill.ofhelperredditmvc.domain.session.excel.AbstractSessionExcel;
import com.anthill.ofhelperredditmvc.exceptions.IncorrectInputDataException;

import java.util.List;

public interface IExcelParser<S extends AbstractSessionExcel> {

    S parseFromExcel(long userOwnerId, List<String> columns, int rowIndex) throws IncorrectInputDataException;
}
