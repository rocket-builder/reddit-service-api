package com.anthill.ofhelperredditmvc.repos;

import com.anthill.ofhelperredditmvc.domain.session.group.PostingGroup;
import com.anthill.ofhelperredditmvc.interfaces.IAuthenticatedRepository;
import com.anthill.ofhelperredditmvc.interfaces.IRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface PostingGroupRepos extends IAuthenticatedRepository<PostingGroup> {

    @Query(value = "select g from PostingGroup g left join fetch g.sessions where g.id=:id")
    Optional<PostingGroup> findByIdWithSessions(@Param("id") long id);

    @Query(value = "select g from PostingGroup g where g.user.id=:id")
    List<PostingGroup> findAllByUserId(long id);

    @Modifying
    @Transactional
    void deleteAllByUser_Id(long id);
}
