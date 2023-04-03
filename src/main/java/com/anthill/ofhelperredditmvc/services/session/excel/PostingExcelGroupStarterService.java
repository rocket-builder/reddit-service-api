package com.anthill.ofhelperredditmvc.services.session.excel;

import com.anthill.ofhelperredditmvc.domain.session.Session;
import com.anthill.ofhelperredditmvc.domain.session.WorkStatus;
import com.anthill.ofhelperredditmvc.domain.session.bot.PostingBotSession;
import com.anthill.ofhelperredditmvc.domain.session.excel.PostingSessionExcel;
import com.anthill.ofhelperredditmvc.domain.session.fields.StatusFields;
import com.anthill.ofhelperredditmvc.domain.session.group.PostingGroup;
import com.anthill.ofhelperredditmvc.domain.session.group.bot.PostingBotGroup;
import com.anthill.ofhelperredditmvc.exceptions.GoogleSheetsAccessException;
import com.anthill.ofhelperredditmvc.exceptions.GoogleSheetsRowsNotFoundException;
import com.anthill.ofhelperredditmvc.exceptions.RedditBotServiceException;
import com.anthill.ofhelperredditmvc.exceptions.SessionServiceParseSessionException;
import com.anthill.ofhelperredditmvc.interfaces.IExcelGroupStarter;
import com.anthill.ofhelperredditmvc.interfaces.IExcelParser;
import com.anthill.ofhelperredditmvc.repos.PostingGroupRepos;
import com.anthill.ofhelperredditmvc.repos.SessionRepos;
import com.anthill.ofhelperredditmvc.services.GoogleSheetsService;
import com.anthill.ofhelperredditmvc.services.RedditBotService;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostingExcelGroupStarterService implements IExcelGroupStarter<PostingGroup> {

    private final SessionRepos sessionRepos;
    private final PostingGroupRepos repos;
    private final RedditBotService botService;
    private final GoogleSheetsService sheetsService;
    private final ModelMapper mapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final IExcelParser<PostingSessionExcel> excelParser;

    public PostingExcelGroupStarterService(SessionRepos sessionRepos,
                                           PostingGroupRepos repos,
                                           RedditBotService botService,
                                           GoogleSheetsService sheetsService,
                                           ModelMapper mapper,
                                           SimpMessagingTemplate messagingTemplate,
                                           IExcelParser<PostingSessionExcel> excelParser) {
        this.sessionRepos = sessionRepos;
        this.repos = repos;
        this.botService = botService;
        this.sheetsService = sheetsService;
        this.mapper = mapper;
        this.messagingTemplate = messagingTemplate;
        this.excelParser = excelParser;
    }

    @Modifying
    @Transactional
    @Override
    public PostingGroup startGroup(PostingGroup group, List<List<String>> rows)
            throws GoogleSheetsAccessException {
        if (!group.getSessions().isEmpty()) {
            sessionRepos.deleteAllByGroup_Id(group.getId());
            group.setSessions(new TreeSet<>());
        }

        if(rows.size() <= 1){
            throw new GoogleSheetsRowsNotFoundException();
        }

        List<PostingBotSession> botSessions = new ArrayList<>();
        for (int rowIndex = 1; rowIndex < rows.size(); rowIndex++) {
            var sessionFromExcel = excelParser.parseFromExcel(group.getUser().getId(), rows.get(rowIndex), rowIndex);

            if (sessionFromExcel.isError()) {
                sheetsService.setRange(
                        group.getSheetUrl(),
                        StatusFields.MESSAGE.getRange(sessionFromExcel.getRowNumber()), sessionFromExcel.getMessage()
                );
                sheetsService.setRange(
                        group.getSheetUrl(),
                        StatusFields.STATUS.getRange(sessionFromExcel.getRowNumber()), sessionFromExcel.getStatus().name()
                );

                messagingTemplate.convertAndSendToUser(
                        group.getUser().getLogin(), "/user/updates/groups", group);

                throw new SessionServiceParseSessionException(sessionFromExcel.getMessage());
            } else {
                sheetsService.setRange(
                        group.getSheetUrl(),
                        StatusFields.STATUS.getRange(sessionFromExcel.getRowNumber()),
                        WorkStatus.CREATED.name());

                group.getSessions().add(
                        new Session(group, sessionFromExcel.getProfile(), sessionFromExcel.getRowNumber()));

                botSessions.add(
                        mapper.map(sessionFromExcel, PostingBotSession.class));
            }
        }

        group.setStart(new Date());
        group.setEnd(null);
        group.setStatus(WorkStatus.WORK);

        var savedGroup = repos.save(group);

        var sessions = group.getSessions();

        var botGroup = mapper.map(savedGroup, PostingBotGroup.class);
        botSessions = botSessions.stream().peek(botSession -> {
            sessions.stream()
                    .filter(s -> s.getProfile().getId() == botSession.getProfile().getId())
                    .findFirst()
                    .ifPresent(session -> {
                        botSession.setId(session.getId());
                    });
        }).collect(Collectors.toList());
        botGroup.setSessions(botSessions);

        try {
            botService.sendGroup(botGroup);

            //TODO make range
            Set<Integer> rowNumbers = sessions.stream()
                    .mapToInt(Session::getRowNumber)
                    .boxed()
                    .collect(Collectors.toCollection(HashSet::new));

            for(var rowNumber : rowNumbers) {
                var range = StatusFields.STATUS.getRange(rowNumber);

                sheetsService.setRange(group.getSheetUrl(), range, WorkStatus.WORK.name());
            }
        } catch (RedditBotServiceException ex) {
            savedGroup.getSessions().forEach(session -> {
                sheetsService.setRange(
                        group.getSheetUrl(),
                        StatusFields.STATUS.getRange(session.getRowNumber()), WorkStatus.ERROR.name()
                );
                sheetsService.setRange(
                        group.getSheetUrl(),
                        StatusFields.MESSAGE.getRange(session.getRowNumber()), ex.getMessage()
                );
            });

            PostingGroup.setError(savedGroup, ex.getMessage());
            savedGroup = repos.save(savedGroup);
            sessionRepos.deleteAllByGroup_Id(savedGroup.getId());
        }

        return savedGroup;
    }
}
