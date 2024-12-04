package Data.Models;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(tableName = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "userID")
    @JsonProperty("id")
    public long userId;

    @Nullable
    @ColumnInfo(name = "Communities List")
    @TypeConverters(Data.Models.TypeConverters.class)
    public List<Long> communitiesList;

    @ColumnInfo(name = "Username")
    @JsonProperty("username")
    public String username;

    @ColumnInfo(name = "Image URL")
    @JsonProperty("imageUrl")
    public String imageUrl;

    @ColumnInfo(name = "Description")
    @JsonProperty("description")
    public String description;

    @Setter
    @Nullable
    @ColumnInfo(name = "Roles List")
    @TypeConverters(Data.Models.TypeConverters.class)
    public List<Long> roles;

    public void addCommunity(long communityId){
        if (getCommunitiesList()==null) setCommunitiesList(new ArrayList<>());
        getCommunitiesList().add(communityId);
    }

    public void addRole(long roleId){
        if (getRoles()==null) setRoles(new ArrayList<>());
        getRoles().add(roleId);
    }
}
