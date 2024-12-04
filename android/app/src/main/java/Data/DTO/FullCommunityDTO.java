package Data.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import Data.Models.Community;
import Data.Models.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FullCommunityDTO {
    @JsonProperty("community")
    Community community;
    @JsonProperty("channels")
    List<ChannelResponseDTO> channels;
    @JsonProperty("members")
    List<MemberDTO> members;
    @JsonProperty("roles")
    List<Role> roles;
}
