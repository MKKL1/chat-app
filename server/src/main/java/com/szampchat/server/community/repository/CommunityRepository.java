package com.szampchat.server.community.repository;

import com.szampchat.server.community.entity.Community;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CommunityRepository extends R2dbcRepository<Community, Long> {

    @Query("SELECT EXISTS (SELECT 1 FROM communities WHERE id = :community AND owner_id = :owner) AND expired_at < CURRENT_TIMESTAMP")
    Mono<Boolean> isOwnerOfCommunity(@Param("community") Long community, @Param("owner") Long owner);

    // Not sure if I should return dto with owner info
    @Query("""
          SELECT c.* FROM communities as c
          JOIN community_members as cm  
          ON cm.community_id = c.id
          WHERE cm.user_id = :user""")
    Flux<Community> userCommunities(@Param("user") Long user);

    @Query("""
        SELECT * FROM communities
        WHERE owner_id = :owner
    """)
    Flux<Community> ownedCommunities(@Param("owner") Long owner);
}
