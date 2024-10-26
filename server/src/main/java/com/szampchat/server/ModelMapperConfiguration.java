package com.szampchat.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.szampchat.server.channel.ChannelType;
import com.szampchat.server.channel.converters.ShortToChannelTypeConverter;
import com.szampchat.server.channel.converters.ChannelTypeToShortConverter;
import com.szampchat.server.permission.converters.IntToPermissionConverter;
import com.szampchat.server.permission.converters.LongToPermOverrideConverter;
import com.szampchat.server.permission.converters.PermOverrideToLongConverter;
import com.szampchat.server.permission.converters.PermissionToIntConverter;
import com.szampchat.server.permission.data.PermissionOverwrites;
import com.szampchat.server.permission.data.Permissions;
import io.r2dbc.spi.ConnectionFactory;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.DialectResolver;
import org.springframework.data.r2dbc.dialect.PostgresDialect;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ModelMapperConfiguration {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(ChannelType.class, new ChannelTypeSerializer());
        module.addSerializer(PermissionOverwrites.class, new PermissionOverwritesSerializer());
        module.addSerializer(Permissions.class, new PermissionSerializer());
        objectMapper.registerModule(module);
        return objectMapper;
    }

    @Bean
    public R2dbcCustomConversions customConversions(ConnectionFactory connectionFactory) {
        R2dbcDialect dialect = DialectResolver.getDialect(connectionFactory);

        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new IntToPermissionConverter());
        converters.add(new PermissionToIntConverter());
        converters.add(new LongToPermOverrideConverter());
        converters.add(new PermOverrideToLongConverter());
        converters.add(new ShortToChannelTypeConverter());
        converters.add(new ChannelTypeToShortConverter());

        return R2dbcCustomConversions.of(dialect, converters);
    }
}
