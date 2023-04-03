package com.anthill.ofhelperredditmvc.services.group.actions;

import com.anthill.ofhelperredditmvc.domain.session.SessionType;
import com.anthill.ofhelperredditmvc.domain.session.WorkStatus;
import com.anthill.ofhelperredditmvc.domain.session.bot.AbstractBotSession;
import com.anthill.ofhelperredditmvc.domain.session.group.PostingGroup;
import com.anthill.ofhelperredditmvc.domain.session.group.bot.AbstractBotGroup;
import com.anthill.ofhelperredditmvc.domain.session.group.bot.PostingBotGroup;
import com.anthill.ofhelperredditmvc.exceptions.*;
import com.anthill.ofhelperredditmvc.interfaces.IExcelGroupStarter;
import com.anthill.ofhelperredditmvc.interfaces.IRepository;
import com.anthill.ofhelperredditmvc.repos.PostingGroupRepos;
import com.anthill.ofhelperredditmvc.services.GoogleSheetsService;
import com.anthill.ofhelperredditmvc.services.RedditBotService;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PostingGroupActionsService extends AbstractGroupActionsService<PostingGroup> {

    private final GoogleSheetsService sheetsService;
    private final IExcelGroupStarter<PostingGroup> postingStarter;

    public PostingGroupActionsService(RedditBotService botService,
                                      PostingGroupRepos repos,
                                      GoogleSheetsService sheetsService,
                                      IExcelGroupStarter<PostingGroup> postingStarter,
                                      ModelMapper mapper) {
        super(botService, repos, mapper);
        this.sheetsService = sheetsService;
        this.postingStarter = postingStarter;
    }

    public PostingGroup start(PostingGroup group)
            throws IncorrectSessionTemplateException, GoogleSheetsAccessException,
            GoogleSheetsReadException, GroupAlreadyStartedException {
        try {
            if(!group.getStatus().equals(WorkStatus.WORK)){
                var rows = sheetsService.getRows(group.getSheetUrl());

                var firstCell = rows.get(0).get(0);
                if(firstCell.contains(SessionType.POSTING.name().toLowerCase())) {

                    return postingStarter.startGroup(group, rows);
                } else {
                    throw new IncorrectSessionTemplateException();
                }
            } else {
                throw new GroupAlreadyStartedException();
            }
        } catch (GoogleJsonResponseException ex){
            throw new GoogleSheetsReadException(ex.getDetails().getMessage() + " (code: " + ex.getDetails().getCode() + ")");
        } catch (IOException ex) {
            if (ex.getMessage().contains(String.valueOf(HttpStatus.FORBIDDEN.value()))){
                throw new GoogleSheetsAccessException();
            } else {
                throw new GoogleSheetsReadException(ex.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <GB extends AbstractBotGroup<SB>, SB extends AbstractBotSession> Class<GB> getTargetBotClass() {
        return (Class<GB>) PostingBotGroup.class;
    }
}
