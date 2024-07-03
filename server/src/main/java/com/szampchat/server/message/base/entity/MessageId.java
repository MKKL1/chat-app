package com.szampchat.server.message.base.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class MessageId implements Serializable {
    private Long id;
    private Long channel;
}
