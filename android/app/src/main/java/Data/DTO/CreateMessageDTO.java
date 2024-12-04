package Data.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMessageDTO {
    @JsonProperty("text")
    private String text;
    @JsonProperty("communityId")
    private String communityId;
    @JsonProperty("respondsToMessage")
    private String respondsToMessage;
    @JsonProperty("gifLink")
    private String gifLink;
}
