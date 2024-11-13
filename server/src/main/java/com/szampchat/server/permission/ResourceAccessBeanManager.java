package com.szampchat.server.permission;

import com.szampchat.server.permission.data.ResourceTypes;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Component
public class ResourceAccessBeanManager {
    private final Map<ResourceTypes, ResourceAccessHandler> handlerMap;

    public ResourceAccessBeanManager(ApplicationContext applicationContext) {
        handlerMap = applicationContext.getBeansOfType(ResourceAccessHandler.class)
                .values()
                .stream()
                .collect(Collectors.toUnmodifiableMap(ResourceAccessHandler::getType, resourceAccessHandler -> resourceAccessHandler));
    }

    public ResourceAccessHandler get(ResourceTypes resourceType) {
        ResourceAccessHandler resourceAccessHandler = handlerMap.get(resourceType);
        if(resourceAccessHandler == null) throw new NoResourceAccessHandlerException();
        return resourceAccessHandler;
    }
}
