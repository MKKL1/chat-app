package Data.Models;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity(tableName = "messages")
@Setter
@Getter
public class Message {
    @PrimaryKey(autoGenerate = true)
    @JsonProperty("id")
    public long id;

    @JsonProperty("text")
    public String text;

    @JsonProperty("updated_at")
    @Nullable
    @TypeConverters(Data.Models.TypeConverters.class)
    public Date updatedAt;

    @JsonProperty("respondsToMessage")
    @Nullable
    public Long respondsToMessage;

    @JsonProperty("gifLink")
    @Nullable
    public String gifLink;

    @JsonProperty("attachments")
    @Nullable
    @TypeConverters(Data.Models.TypeConverters.class)
    public List<MessageAttachment> attachments;

    @JsonProperty("reactions")
    @Nullable
    @TypeConverters(Data.Models.TypeConverters.class)
    public List<MessageReaction> reactions;

    @JsonProperty("channelId")
    public long channelId;

    @JsonProperty("userId")
    public long userId;
}
