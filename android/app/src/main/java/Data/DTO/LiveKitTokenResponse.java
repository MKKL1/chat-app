package Data.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LiveKitTokenResponse {
    @JsonProperty("token")
    private String token;
}
