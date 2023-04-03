package com.anthill.ofhelperredditmvc.services.rest;

import com.anthill.ofhelperredditmvc.domain.dto.bot.StatusDto;
import com.anthill.ofhelperredditmvc.domain.session.Session;
import com.anthill.ofhelperredditmvc.domain.session.WorkStatus;
import com.anthill.ofhelperredditmvc.repos.SessionRepos;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SessionService extends AbstractRestService<Session, SessionRepos> {

    public SessionService(SessionRepos repos) {
        super(repos);
    }

    public Session updateStatus(Session session, StatusDto status) {
        session.setStatus(status.getStatus());
        session.setMessage(status.getMessage());

        if(status.getStatus().equals(WorkStatus.DONE) || status.getStatus().equals(WorkStatus.ERROR)){
            session.setEnd(new Date());
        }

        return repos.save(session);
    }
}
