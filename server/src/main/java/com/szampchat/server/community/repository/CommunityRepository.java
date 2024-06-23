package com.szampchat.server.community.repository;

import com.szampchat.server.community.entity.Community;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends CrudRepository<Community, Long> {
}
