package Data.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageAttachment {
    @JsonProperty("id")
    private String id;
    @JsonProperty("size")
    private long size;
    @JsonProperty("name")
    private String name;
    @JsonProperty("path")
    private String path;
}
