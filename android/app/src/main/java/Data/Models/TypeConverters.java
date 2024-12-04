package Data.Models;

import android.util.Log;

import androidx.room.TypeConverter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TypeConverters {
    private static final ObjectMapper objectMapper = new ObjectMapper();



    @TypeConverter
    public static String fromStringList(List<String> strings) {
        try {
            return objectMapper.writeValueAsString(strings);
        } catch (IOException e) {
            // Logowanie błędu
            e.printStackTrace();
            throw new RuntimeException("Serialization error: cannot convert String list to JSON", e);
        }
    }

    @TypeConverter
    public static List<String> toStringList(String data) {
        try {
            return objectMapper.readValue(data, new TypeReference<List<String>>() {});
        } catch (IOException e) {
            // Logowanie błędu
            e.printStackTrace();
            throw new RuntimeException("Deserialization error: cannot convert JSON to String list", e);
        }
    }

    @TypeConverter
    public static String fromMessage(Message message) {
        try {
            return message == null ? null : objectMapper.writeValueAsString(message);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Serialization error: cannot convert Message to JSON", e);
        }
    }

    @TypeConverter
    public static Message toMessage(String data) {
        try {
            return data == null ? null : objectMapper.readValue(data, Message.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Deserialization error: cannot convert JSON to Message", e);
        }
    }

    @TypeConverter
    public static String fromMessageReactionList(List<MessageReaction> reactions) {
        try {
            return reactions == null ? null : objectMapper.writeValueAsString(reactions);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Serialization error: cannot convert MessageReaction list to JSON", e);
        }
    }

    @TypeConverter
    public static List<MessageReaction> toMessageReactionList(String data) {
        try {
            return data == null ? null : objectMapper.readValue(data, new TypeReference<List<MessageReaction>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Deserialization error: cannot convert JSON to MessageReaction list", e);
        }
    }

    @TypeConverter
    public static String fromMessageAttachmentList(List<MessageAttachment> attachments) {
        try {
            return attachments == null ? null : objectMapper.writeValueAsString(attachments);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Serialization error: cannot convert MessageAttachment list to JSON", e);
        }
    }

    @TypeConverter
    public static List<MessageAttachment> toMessageAttachmentList(String data) {
        try {
            return data == null ? null : objectMapper.readValue(data, new TypeReference<List<MessageAttachment>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Deserialization error: cannot convert JSON to MessageAttachment list", e);
        }
    }

    @TypeConverter
    public static Long fromDate(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }
    @TypeConverter
    public static String fromChannelRoleList(List<ChannelRole> roles) {
        try {
            return objectMapper.writeValueAsString(roles);
        } catch (IOException e) {
            Log.d("TypeConvertes", "fromChannelRoleList " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException("Serialization error: cannot convert ChannelRole list to JSON", e);
        }
    }

    @TypeConverter
    public static List<ChannelRole> toChannelRoleList(String data) {
        try {
            return objectMapper.readValue(data, new TypeReference<List<ChannelRole>>() {});
        } catch (IOException e) {
            Log.d("TypeConvertes", "toChannelRoleList " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException("Deserialization error: cannot convert JSON to ChannelRole list", e);
        }
    }
    @TypeConverter
    public static String fromLongList(List<Long> roles) {
        try {
            return objectMapper.writeValueAsString(roles);
        } catch (IOException e) {
            Log.d("TypeConvertes", "fromLongList " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException("Serialization error: cannot convert Long list to JSON", e);
        }
    }

    @TypeConverter
    public static List<Long> toLongList(String data) {
        try {
            return objectMapper.readValue(data, new TypeReference<List<Long>>() {});
        } catch (IOException e) {
            Log.d("TypeConvertes", "toLongList " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException("Deserialization error: cannot convert JSON to Long list", e);
        }
    }
}
