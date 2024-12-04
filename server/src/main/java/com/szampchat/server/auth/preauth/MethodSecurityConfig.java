package com.szampchat.server.auth.preauth;

import com.szampchat.server.auth.annotation.HasPermission;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Role;
import org.springframework.security.authorization.method.AuthorizationInterceptorsOrder;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeReactiveMethodInterceptor;

@Configuration
//@EnableReactiveMethodSecurity(useAuthorizationManager=true)

class MethodSecurityConfig {
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor customAuthorize(HasPermAuthManager rules) {
        AnnotationMatchingPointcut pointcut = new AnnotationMatchingPointcut(null, HasPermission.class);
        AuthorizationManagerBeforeReactiveMethodInterceptor interceptor = new AuthorizationManagerBeforeReactiveMethodInterceptor(pointcut, rules);
        interceptor.setOrder(AuthorizationInterceptorsOrder.PRE_AUTHORIZE.getOrder() + 1);
        return interceptor;
    }

    @Profile(value = {"development", "production"})
    @Bean
    public HasPermAuthManager hasPermAuthManager(ResourcePermissionEvaluator permissionEvaluator) {
        return new HasPermAuthManagerImpl(permissionEvaluator);
    }
}