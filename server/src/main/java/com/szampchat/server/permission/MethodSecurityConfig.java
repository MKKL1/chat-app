package com.szampchat.server.permission;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.aop.support.annotation.AnnotationMethodMatcher;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.authorization.method.AuthorizationInterceptorsOrder;
import org.springframework.security.authorization.method.AuthorizationManagerAfterReactiveMethodInterceptor;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeReactiveMethodInterceptor;
import org.springframework.security.authorization.method.MethodInvocationResult;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;

@Configuration
@EnableReactiveMethodSecurity(useAuthorizationManager=true)
class MethodSecurityConfig {
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor customAuthorize(HasPermissionReactiveAuthorizationManager rules) {
        AnnotationMatchingPointcut pointcut = new AnnotationMatchingPointcut(null, HasPermission.class);
        AuthorizationManagerBeforeReactiveMethodInterceptor interceptor = new AuthorizationManagerBeforeReactiveMethodInterceptor(pointcut, rules);
        interceptor.setOrder(AuthorizationInterceptorsOrder.PRE_AUTHORIZE.getOrder() + 1);
        return interceptor;
    }
}