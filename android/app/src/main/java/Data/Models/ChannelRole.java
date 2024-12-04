package Data.Models;

import androidx.room.ColumnInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChannelRole {
    @JsonProperty("roleId")
    private long roleId;
    @JsonProperty("overwrites")
    private long channelOverwrites;
}
