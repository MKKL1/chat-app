package com.szampchat.server.community.repository;

import com.szampchat.server.community.CommunityMemberRolesRow;
import com.szampchat.server.community.entity.Community;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CommunityRepository extends R2dbcRepository<Community, Long> {


}
