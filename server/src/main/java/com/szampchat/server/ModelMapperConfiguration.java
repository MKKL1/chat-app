package com.szampchat.server;

import com.szampchat.server.channel.converters.ByteToChannelTypeConverter;
import com.szampchat.server.channel.converters.ChannelTypeToByteConverter;
import com.szampchat.server.shared.permission.converters.IntToPermissionConverter;
import com.szampchat.server.shared.permission.converters.LongToPermOverrideConverter;
import com.szampchat.server.shared.permission.converters.PermOverrideToLongConverter;
import com.szampchat.server.shared.permission.converters.PermissionToIntConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.PostgresDialect;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ModelMapperConfiguration {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public R2dbcCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new IntToPermissionConverter());
        converters.add(new PermissionToIntConverter());
        converters.add(new LongToPermOverrideConverter());
        converters.add(new PermOverrideToLongConverter());
        converters.add(new ByteToChannelTypeConverter());
        converters.add(new ChannelTypeToByteConverter());
        return R2dbcCustomConversions.of(PostgresDialect.INSTANCE, converters);
    }
}
