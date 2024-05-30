package com.szampchat.server.role;

import jdk.jfr.Registered;
import org.springframework.data.repository.CrudRepository;

@Registered
public interface RoleRepository extends CrudRepository<Role, Long> {
}
