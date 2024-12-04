package Data.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import Data.Models.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleResponseDTO {
    @JsonProperty("role")
    private Role role;
    @JsonProperty("members")
    private List<Long> members;
}
