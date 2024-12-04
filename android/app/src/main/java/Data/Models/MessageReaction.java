package Data.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageReaction {
    @JsonProperty("emoji")
    private String emoji;
    @JsonProperty("count")
    private int count;
    @JsonProperty("me")
    private boolean me;
}
