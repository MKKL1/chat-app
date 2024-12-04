package Data.Models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(tableName = "communities")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Community {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "communityID")
    @JsonProperty("id")
    public long communityID;
    @NonNull
    @ColumnInfo(name = "Community Name")
    @JsonProperty("name")
    public String communityName;
    @ColumnInfo(name = "Owner ID")
    @JsonProperty("ownerId")
    public long ownerID;
    @ColumnInfo(name = "Image URL")
    @JsonProperty("imageUrl")
    public String imageUrl;
    @ColumnInfo(name = "Base Permissions")
    @JsonProperty("basePermissions")
    public int basePermissions;

}
