package com.szampchat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import Data.Models.ChannelRole;
import Data.Models.Message;
import Data.Models.MessageAttachment;
import Data.Models.MessageReaction;
import Data.Models.TypeConverters;

public class TypeConvertersTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // String <-> List<String>
    @Test
    public void testFromStringList() throws JsonProcessingException {
        List<String> strings = Arrays.asList("one", "two", "three");
        String expectedJson = objectMapper.writeValueAsString(strings);

        String result = TypeConverters.fromStringList(strings);

        assertEquals(expectedJson, result);
    }

    @Test
    public void testToStringList() throws JsonProcessingException {
        String json = "[\"one\",\"two\",\"three\"]";
        List<String> expectedList = Arrays.asList("one", "two", "three");

        List<String> result = TypeConverters.toStringList(json);

        assertEquals(expectedList, result);
    }

    // Message <-> String
    @Test
    public void testFromMessage() throws JsonProcessingException {
        Message message = new Message(1L, "Hello, world!", 123L, 456L);
        String expectedJson = objectMapper.writeValueAsString(message);

        String result = TypeConverters.fromMessage(message);

        assertEquals(expectedJson, result);
    }

    @Test
    public void testToMessage() throws JsonProcessingException {
        String json = "{\"id\":1,\"text\":\"Hello, world!\",\"channelId\":123,\"userId\":456}";
        Message expectedMessage = objectMapper.readValue(json, Message.class);

        Message result = TypeConverters.toMessage(json);

        assertEquals(expectedMessage, result);
    }

    // List<MessageReaction> <-> String
    @Test
    public void testFromMessageReactionList() throws JsonProcessingException {
        List<MessageReaction> reactions = Arrays.asList(
                new MessageReaction("👍", 5, true),
                new MessageReaction("🤔", 3, false)
        );
        String expectedJson = objectMapper.writeValueAsString(reactions);

        String result = TypeConverters.fromMessageReactionList(reactions);

        assertEquals(expectedJson, result);
    }

    @Test
    public void testToMessageReactionList() throws JsonProcessingException {
        String json = "[{\"emoji\":\"👍\",\"count\":5,\"me\":true}," +
                "{\"emoji\":\"🤔\",\"count\":3,\"me\":false}]";
        List<MessageReaction> expectedList = Arrays.asList(
                new MessageReaction("👍", 5, true),
                new MessageReaction("🤔", 3, false)
        );

        List<MessageReaction> result = TypeConverters.toMessageReactionList(json);

        assertEquals(expectedList, result);
    }

    // List<MessageAttachment> <-> String
    @Test
    public void testFromMessageAttachmentList() throws JsonProcessingException {
        List<MessageAttachment> attachments = Arrays.asList(
                new MessageAttachment(1L, 1024L, "file1.jpg", "/path/to/file1.jpg"),
                new MessageAttachment(2L, 2048L, "file2.png", "/path/to/file2.png")
        );
        String expectedJson = objectMapper.writeValueAsString(attachments);

        String result = TypeConverters.fromMessageAttachmentList(attachments);

        assertEquals(expectedJson, result);
    }

    @Test
    public void testToMessageAttachmentList() throws JsonProcessingException {
        String json = "[{\"id\":1,\"size\":1024,\"name\":\"file1.jpg\",\"path\":\"/path/to/file1.jpg\"}," +
                "{\"id\":2,\"size\":2048,\"name\":\"file2.png\",\"path\":\"/path/to/file2.png\"}]";
        List<MessageAttachment> expectedList = objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<List<MessageAttachment>>() {});

        List<MessageAttachment> result = TypeConverters.toMessageAttachmentList(json);

        assertEquals(expectedList, result);
    }

    // Date <-> Long
    @Test
    public void testFromDate() {
        Date date = new Date();
        Long expectedTimestamp = date.getTime();

        Long result = TypeConverters.fromDate(date);

        assertEquals(expectedTimestamp, result);
    }

    @Test
    public void testToDate() {
        Long timestamp = System.currentTimeMillis();
        Date expectedDate = new Date(timestamp);

        Date result = TypeConverters.toDate(timestamp);

        assertEquals(expectedDate, result);
    }

    // List<ChannelRole> <-> String
    @Test
    public void testFromChannelRoleList() throws JsonProcessingException {
        List<ChannelRole> roles = Arrays.asList(
                new ChannelRole(1L, 100L),
                new ChannelRole(2L, 200L)
        );
        String expectedJson = objectMapper.writeValueAsString(roles);

        String result = TypeConverters.fromChannelRoleList(roles);

        assertEquals(expectedJson, result);
    }

    @Test
    public void testToChannelRoleList() throws JsonProcessingException {
        String json = "[{\"roleId\":1,\"overwrites\":100},{\"roleId\":2,\"overwrites\":200}]";
        List<ChannelRole> expectedList = objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<List<ChannelRole>>() {});

        List<ChannelRole> result = TypeConverters.toChannelRoleList(json);

        assertEquals(expectedList, result);
    }

    @Test
    public void testFromLongList() throws JsonProcessingException {
        List<Long> longs = Arrays.asList(1L, 2L, 3L);
        String expectedJson = objectMapper.writeValueAsString(longs);

        String result = TypeConverters.fromLongList(longs);

        assertEquals(expectedJson, result);
    }

    @Test
    public void testToLongList() throws JsonProcessingException {
        String json = "[1,2,3]";
        List<Long> expectedList = Arrays.asList(1L, 2L, 3L);

        List<Long> result = TypeConverters.toLongList(json);

        assertEquals(expectedList, result);
    }
}
