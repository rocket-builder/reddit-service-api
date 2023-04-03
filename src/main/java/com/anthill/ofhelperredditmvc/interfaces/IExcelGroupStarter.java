package com.anthill.ofhelperredditmvc.interfaces;

import com.anthill.ofhelperredditmvc.domain.session.group.AbstractGroup;

import java.util.List;

public interface IExcelGroupStarter<G extends AbstractGroup> {

    G startGroup(G group, List<List<String>> rows);
}
