package com.anthill.ofhelperredditmvc.repos;

import com.anthill.ofhelperredditmvc.domain.session.group.UpVoteGroup;
import com.anthill.ofhelperredditmvc.interfaces.IAuthenticatedRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UpVoteGroupRepos extends IAuthenticatedRepository<UpVoteGroup> {

    @Query(value = "select g from UpVoteGroup g left join fetch g.sessions where g.id=:id")
    Optional<UpVoteGroup> findByIdWithSessions(@Param("id") long id);

    @Query(value = "select g from UpVoteGroup g where g.user.id=:id")
    List<UpVoteGroup> findAllByUserId(@Param("id") long id);

    @Modifying
    @Transactional
    void deleteAllByUser_Id(long id);

    @Query(value = "select g from UpVoteGroup g left join fetch g.sessions where g.start >= :today and g.status = 'DONE'")
    Set<UpVoteGroup> findDoneTodayGroupsWithSessionsByStatus(Date today);
}
