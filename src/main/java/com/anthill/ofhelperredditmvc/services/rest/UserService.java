package com.anthill.ofhelperredditmvc.services.rest;

import com.anthill.ofhelperredditmvc.domain.Proxy;
import com.anthill.ofhelperredditmvc.domain.Role;
import com.anthill.ofhelperredditmvc.domain.User;
import com.anthill.ofhelperredditmvc.domain.dto.SignUpDto;
import com.anthill.ofhelperredditmvc.domain.dto.TelegramUserDto;
import com.anthill.ofhelperredditmvc.exceptions.IncorrectInputDataException;
import com.anthill.ofhelperredditmvc.exceptions.InsufficientUpVoteBalanceException;
import com.anthill.ofhelperredditmvc.exceptions.LoginAlreadyTakenException;
import com.anthill.ofhelperredditmvc.exceptions.ResourceNotFoundedException;
import com.anthill.ofhelperredditmvc.repos.*;
import com.anthill.ofhelperredditmvc.services.parsers.TelegramTagParser;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService extends AbstractRestService<User, UserRepos> {

    private final ModelMapper mapper;
    private final TelegramTagParser telegramTagParser;
    private final SessionRepos sessionRepos;
    private final PostingGroupRepos postingGroupRepos;
    private final UpVoteGroupRepos upVoteGroupRepos;
    private final RedditAccountProfileRepos profileRepos;

    private final BCryptPasswordEncoder passwordEncoder;

    protected UserService(UserRepos repos,
                          ModelMapper mapper,
                          TelegramTagParser telegramTagParser,
                          SessionRepos sessionRepos,
                          PostingGroupRepos postingGroupRepos,
                          UpVoteGroupRepos upVoteGroupRepos,
                          RedditAccountProfileRepos profileRepos,
                          BCryptPasswordEncoder passwordEncoder) {
        super(repos);
        this.mapper = mapper;
        this.telegramTagParser = telegramTagParser;
        this.sessionRepos = sessionRepos;
        this.postingGroupRepos = postingGroupRepos;
        this.upVoteGroupRepos = upVoteGroupRepos;
        this.profileRepos = profileRepos;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User save(User entity) throws LoginAlreadyTakenException {
        var optional = repos.findByLogin(entity.getLogin());
        if(optional.isPresent()){
            throw new LoginAlreadyTakenException();
        }
        if(!telegramTagParser.isCorrect(entity.getTelegramTag())){
            throw new IncorrectInputDataException("Incorrect telegram tag!");
        }

        if(entity.getRoles() == null) {
            entity.setRoles(List.of(Role.ROLE_LIKER));
        }
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));

        return repos.save(entity);
    }

    public User signUp(SignUpDto signUp) throws LoginAlreadyTakenException {
        var user = mapper.map(signUp, User.class);

        return this.save(user);
    }

    public void withdrawUpVotes(User user, int upVoteCount) throws InsufficientUpVoteBalanceException {
        var balance = user.getUpVoteBalance();
        balance -= upVoteCount;

        if(balance < 0){
            throw new InsufficientUpVoteBalanceException();
        }

        user.setUpVoteBalance(balance);
        repos.save(user);
    }

    public void refundUpVotes(User user, int upVoteCount) {
        var balance = user.getUpVoteBalance();
        balance += upVoteCount;

        user.setUpVoteBalance(balance);
        repos.save(user);
    }

    public User findByLogin(String login) throws UsernameNotFoundException {

        return repos.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User with login: " + login + " not found"));
    }

    public User signUpTelegram(TelegramUserDto telegram) throws ResourceNotFoundedException {

        var user = repos.findByTelegramTag(telegram.getTag())
                .orElseThrow(ResourceNotFoundedException::new);

        user.setTelegramId(telegram.getId());

        return repos.save(user);
    }

    public Integer findUpVoteBalanceByTelegramId(long telegramId) throws ResourceNotFoundedException {

        return repos.findUpVoteBalanceByTelegramId(telegramId)
                .orElseThrow(ResourceNotFoundedException::new);
    }

    public User findByLoginWithExcelGroups(String login){
        return repos.findByLoginWithExcelGroups(login)
                .orElseThrow(() -> new UsernameNotFoundException("User with login: " + login + " not found"));
    }

    public User findByLoginWithUpVoteGroups(String login){
        return repos.findByLoginWithUpVoteGroups(login)
                .orElseThrow(() -> new UsernameNotFoundException("User with login: " + login + " not found"));
    }

    public User findBySessionId(long id) throws ResourceNotFoundedException {
        return repos.findUserBySessionId(id)
                .orElseThrow(ResourceNotFoundedException::new);
    }

    public List<Long> findAllTelegramIdsForNewsletter(){
        return repos.findAllTelegramIdsForNewsletter();
    }

    public void deleteAllProfilesByUserId(long userId){
        sessionRepos.deleteAllByUser_Id(userId);
        profileRepos.deleteAllByUser_Id(userId);
    }

    public void deleteAllExcelGroupsByUserId(long userId){
        sessionRepos.deleteAllByUser_Id(userId);
        postingGroupRepos.deleteAllByUser_Id(userId);
    }

    public void deleteAllUpVoteGroupsByUserId(long userId){
        sessionRepos.deleteAllByUser_Id(userId);
        upVoteGroupRepos.deleteAllByUser_Id(userId);
    }

    public void connectProxyToUser(User user, Proxy proxy){
        repos.connectProxyToUser(user.getId(), proxy.getId());
    }

    public void disconnectProxyFromUserById(long id){
        repos.disconnectProxyFromUser(id);
    }

    @Override
    public List<User> deleteAllByIds(List<Long> ids) {
        ids.forEach(id -> {
            repos.deleteRolesByUserId(id);
            deleteAllExcelGroupsByUserId(id);
            deleteAllUpVoteGroupsByUserId(id);
            deleteAllProfilesByUserId(id);
        });

        return super.deleteAllByIds(ids);
    }
}
