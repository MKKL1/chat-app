package com.szampchat.server.tools;


import com.szampchat.server.auth.CurrentUser;
import lombok.AllArgsConstructor;
import org.springframework.core.annotation.AliasFor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.*;

/**
 * Annotation to inject fake user data into {@link SecurityContext}.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WithSecurityContext(factory = WithMockCustomUser.AuthenticationFactory.class)
public @interface WithMockCustomUser {

    long userId() default 1;

    @AliasFor(annotation = WithSecurityContext.class)
    TestExecutionEvent setupBefore() default TestExecutionEvent.TEST_METHOD;

    @AllArgsConstructor
    class AuthenticationFactory implements WithSecurityContextFactory<WithMockCustomUser> {

        @Override
        public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
            final SecurityContext context = SecurityContextHolder.createEmptyContext();

            //Random data, as it's not important
            final Jwt jwt = Jwt.withTokenValue("123")
                    .header("alg", "none")
                    .claim("scope", "message:read")
                    .build();
            context.setAuthentication(new CurrentUser(jwt, null, annotation.userId()));
            return context;
        }
    }
}
