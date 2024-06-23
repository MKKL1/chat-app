package com.szampchat.server.message.base;

import com.szampchat.server.snowflake.SnowflakeGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class MessageId implements Serializable {
    @GeneratedValue(generator = SnowflakeGenerator.GENERATOR_NAME)
    @GenericGenerator(name = SnowflakeGenerator.GENERATOR_NAME, type = SnowflakeGenerator.class)
    @Column(name = "message_id")
    private Long id;

    @Column(name = "channel_id")
    private Long channelId;
}
