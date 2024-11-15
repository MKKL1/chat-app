package com.szampchat.server.shared;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.szampchat.server.role.dto.RoleWithMembersDTO;
import lombok.AllArgsConstructor;

//Bad name
@AllArgsConstructor
public class JsonPatchUtil<T> {
    private final Class<T> clazz;

    public T patch(T toPatch, JsonPatch jsonPatch) throws InvalidPatchException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode roleWithMembersNode = objectMapper.convertValue(toPatch, JsonNode.class);

        try {
            JsonNode patchedNode = jsonPatch.apply(roleWithMembersNode);
            return objectMapper.treeToValue(patchedNode, clazz);
        } catch (JsonPatchException e) {
            throw new InvalidPatchException("Failed to apply patch", e);
        } catch (JsonProcessingException e) {
            throw new InvalidPatchException("Invalid json", e);
        }
    }
}
