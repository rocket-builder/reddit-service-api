package com.anthill.ofhelperredditmvc.repos;

import com.anthill.ofhelperredditmvc.domain.session.Session;
import com.anthill.ofhelperredditmvc.domain.session.group.AbstractGroup;
import com.anthill.ofhelperredditmvc.interfaces.IRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface SessionRepos extends IRepository<Session> {

    @Modifying
    @Transactional
    void deleteAllByGroup_Id(long id);

    @Modifying
    @Transactional
    void deleteAllByProfile_Id(long id);

    List<Session> findAllByGroup(AbstractGroup group);

    @Modifying
    @Transactional
    @Query(value = "delete s from reddit_session s left join session_group g on s.group_id=g.id where g.user_id = ?", nativeQuery = true)
    void deleteAllByUser_Id(long id);

    @Modifying
    @Transactional
    @Query("delete from Session s where s.profile.id in (:ids)")
    void deleteAllByProfileIds(List<Long> ids);
}
