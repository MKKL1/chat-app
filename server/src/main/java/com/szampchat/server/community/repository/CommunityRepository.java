package com.szampchat.server.community.repository;

import com.szampchat.server.community.entity.Community;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends R2dbcRepository<Community, Long> {
}
