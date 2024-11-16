package com.szampchat.server.auth.annotation;

import com.szampchat.server.permission.data.PermissionScope;
import com.szampchat.server.permission.data.PermissionFlag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface HasPermission {
    PermissionScope scope() default PermissionScope.COMMUNITY;
    PermissionFlag[] value();
}
