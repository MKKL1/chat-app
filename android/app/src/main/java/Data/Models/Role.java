package Data.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(tableName = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Role ID")
    @JsonProperty("id")
    public long roleId;
    @ColumnInfo(name = "Role Name")
    @JsonProperty("name")
    public String name;
    @ColumnInfo(name = "Community ID")
    @JsonProperty("community")
    public long communityId;
    @ColumnInfo(name = "Permission Overwrites")
    @JsonProperty("permissionOverwrites")
    public long permissionOverwrites;
}
