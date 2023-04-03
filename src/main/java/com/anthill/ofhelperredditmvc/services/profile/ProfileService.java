package com.anthill.ofhelperredditmvc.services.profile;

import com.anthill.ofhelperredditmvc.domain.User;
import com.anthill.ofhelperredditmvc.domain.dto.UserProfileDto;
import com.anthill.ofhelperredditmvc.domain.session.WorkStatus;
import com.anthill.ofhelperredditmvc.domain.session.group.UpVoteGroup;
import com.anthill.ofhelperredditmvc.repos.PostingGroupRepos;
import com.anthill.ofhelperredditmvc.repos.RedditAccountProfileRepos;
import com.anthill.ofhelperredditmvc.repos.UpVoteGroupRepos;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private final RedditAccountProfileRepos profileRepos;
    private final PostingGroupRepos postingGroupRepos;
    private final UpVoteGroupRepos upVoteGroupRepos;

    public ProfileService(RedditAccountProfileRepos profileRepos,
                          PostingGroupRepos postingGroupRepos,
                          UpVoteGroupRepos upVoteGroupRepos) {
        this.profileRepos = profileRepos;
        this.postingGroupRepos = postingGroupRepos;
        this.upVoteGroupRepos = upVoteGroupRepos;
    }

    public UserProfileDto toProfile(User user){
        
        var totalAccounts = profileRepos.countAllByUser(user);
        var totalKarma = user.getProfiles()
                .stream()
                .mapToLong(p -> p.getRedditAccount().getKarma())
                .sum();

        var totalPosts = postingGroupRepos.findAllByUserId(user.getId())
                .stream()
                .mapToLong(g -> g.getSessions().size())
                .sum();

        var totalUpVotes = upVoteGroupRepos.findAllByUserId(user.getId())
                .stream()
                .filter(g -> g.getStatus().equals(WorkStatus.DONE))
                .mapToLong(UpVoteGroup::getUpVoteCount)
                .sum();

        return UserProfileDto.builder()
                .login(user.getLogin())
                .roles(user.getRoles())
                .totalAccounts(totalAccounts)
                .totalKarma(totalKarma)
                .totalPosts(totalPosts)
                .totalUpVotes(totalUpVotes)
                .build();
    }
}
