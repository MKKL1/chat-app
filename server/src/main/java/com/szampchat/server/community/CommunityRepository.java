package com.szampchat.server.community;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends CrudRepository<Community, Long> {
}
