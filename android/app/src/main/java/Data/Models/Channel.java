package Data.Models;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;

import Data.DTO.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(tableName = "channels")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Channel {
    @ColumnInfo(name = "Channel Id")
    @PrimaryKey
    public long id;
    @ColumnInfo(name = "Channel Name")
    public String name;
    @ColumnInfo(name = "Community Id")
    public long communityId;
    @ColumnInfo(name = "Channel Type")
    public ChannelType type;
    @ColumnInfo(name = "Voice Channel Participants")
    @TypeConverters(Data.Models.TypeConverters.class)
    @Nullable
    public List<String> participants;
    @ColumnInfo(name = "Channel Role Overwrites")
    @TypeConverters(Data.Models.TypeConverters.class)
    public List<ChannelRole> overwrites;
}
