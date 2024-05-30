package com.szampchat.server.channel;

import com.szampchat.server.role.Role;
import jdk.jfr.Registered;
import org.springframework.data.repository.CrudRepository;

@Registered
public interface ChannelRepository extends CrudRepository<Channel, Long> {
}
