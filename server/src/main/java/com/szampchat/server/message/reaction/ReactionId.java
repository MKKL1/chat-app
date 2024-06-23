package com.szampchat.server.message.reaction;

import com.szampchat.server.snowflake.SnowflakeGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class ReactionId {
    @GeneratedValue(generator = SnowflakeGenerator.GENERATOR_NAME)
    @GenericGenerator(name = SnowflakeGenerator.GENERATOR_NAME, type = SnowflakeGenerator.class)
    @Column(name="reaction_id")
    private Long id;
    @Column(name="channel_id")
    private Long channelId;
}
