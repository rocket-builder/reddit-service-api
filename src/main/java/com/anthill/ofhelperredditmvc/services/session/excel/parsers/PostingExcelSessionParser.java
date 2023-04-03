package com.anthill.ofhelperredditmvc.services.session.excel.parsers;

import com.anthill.ofhelperredditmvc.domain.Proxy;
import com.anthill.ofhelperredditmvc.domain.session.WorkStatus;
import com.anthill.ofhelperredditmvc.domain.session.excel.PostingSessionExcel;
import com.anthill.ofhelperredditmvc.domain.session.fields.PostingFields;
import com.anthill.ofhelperredditmvc.exceptions.IncorrectInputDataException;
import com.anthill.ofhelperredditmvc.exceptions.ResourceNotFoundedException;
import com.anthill.ofhelperredditmvc.interfaces.IExcelParser;
import com.anthill.ofhelperredditmvc.repos.RedditAccountProfileRepos;
import com.anthill.ofhelperredditmvc.utils.ParseHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostingExcelSessionParser implements IExcelParser<PostingSessionExcel> {

    private final RedditAccountProfileRepos profileRepos;
    private final ParseHelper parser;

    public PostingExcelSessionParser(RedditAccountProfileRepos profileRepos,
                                     ParseHelper parser) {
        this.profileRepos = profileRepos;
        this.parser = parser;
    }

    @Override
    public PostingSessionExcel parseFromExcel(long userOwnerId, List<String> columns, int rowIndex) throws IncorrectInputDataException {
        try {
            var username = columns.get(PostingFields.REDDIT_ACCOUNT.getIndex());
            var profile = profileRepos.findByUserIdAndRedditAccountLogin(userOwnerId, username)
                    .orElseThrow(ResourceNotFoundedException::new);

            if(profile.getRedditAccount().isBanned()){
                throw new IncorrectInputDataException("Passed reddit account banned, please choose another one");
            }
            if(profile.getProxy() == null){
                throw new IncorrectInputDataException("Passed reddit account dont have proxy, please set it");
            }

            var proxyString = parser.parseIfPresent(columns, PostingFields.PROXY.getIndex());

            var proxy = proxyString == null? profile.getProxy() : new Proxy(proxyString);

            return PostingSessionExcel.builder()
                    .profile(profile)
                    .proxy(proxy)
                    .subReddit(columns.get(PostingFields.SUB_REDDIT.getIndex()))
                    .title(columns.get(PostingFields.TITLE.getIndex()))
                    .imageUrl(columns.get(PostingFields.IMAGE_URL.getIndex()))
                    .comment(parser.parseIfPresent(columns, PostingFields.COMMENT.getIndex()))
                    .flairs(parser.parseIfPresent(columns, PostingFields.FLAIRS.getIndex()))
                    .upVoteCount(parser.parseIntIfPresent(columns, PostingFields.UP_VOTE_COUNT.getIndex()))
                    .rowNumber(rowIndex + 1)
                    .status(WorkStatus.WORK)
                    .build();
        } catch (ResourceNotFoundedException ex){
            var session = new PostingSessionExcel();
            session.setError("Reddit account not found", rowIndex + 1);

            return session;
        } catch (Exception ex){
            ex.printStackTrace();
            var session = new PostingSessionExcel();
            session.setError("Incorrect input data: " + ex.getMessage(), rowIndex + 1);

            return session;
        }
    }
}
