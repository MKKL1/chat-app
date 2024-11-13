package com.szampchat.server.permission;

import com.szampchat.server.permission.data.PermissionContext;
import com.szampchat.server.permission.data.PermissionFlag;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface HasPermission {
    PermissionContext context();
    PermissionFlag[] value();
}
