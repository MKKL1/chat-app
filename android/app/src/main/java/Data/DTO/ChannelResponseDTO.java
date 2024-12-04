package Data.DTO;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import Data.Models.ChannelRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelResponseDTO {
    @JsonProperty("channel")
    private ChannelDTO channel;
    @Nullable
    @JsonProperty("participants")
    private List<String> participants;
    @JsonProperty("overwrites")
    private List<ChannelRole> overwrites;
}
