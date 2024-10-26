package com.szampchat.server.shared.docs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class JsonPatchSchema {
    @NotBlank
    public Op op;

    public enum Op {
        replace, add, remove, copy, move, test
    }

    @NotBlank
    @Schema(example = "/name")
    public String path;

    @NotBlank
    public String value;
}
