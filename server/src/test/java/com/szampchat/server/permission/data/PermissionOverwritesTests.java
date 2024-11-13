package com.szampchat.server.permission.data;

import org.junit.jupiter.api.Test;

import static com.szampchat.server.permission.data.PermissionFlag.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PermissionOverwritesTests {

    @Test
    public void givenMaskAndChannelContext_whenAllow_shouldSetCorrectFlags() {
        int permissionMask = CHANNEL_CREATE.asMask() | CHANNEL_MODIFY.asMask() | ADMINISTRATOR.asMask();

        PermissionOverwrites permissionOverwrites = new PermissionOverwrites(0);
        permissionOverwrites.allow(PermissionScope.CHANNEL, permissionMask);

        long data = permissionOverwrites.getPermissionOverwriteData();
        assertThat(data).isNotEqualTo(0L);
        assertThat(data).isNotEqualTo(permissionMask); //It should not include Administrator perm
        assertThat(data).isEqualTo(CHANNEL_CREATE.asMask() | CHANNEL_MODIFY.asMask());
    }

    @Test
    public void givenMaskAndChannelContext_whenDeny_shouldSetCorrectFlags() {
        int permissionMask = CHANNEL_CREATE.asMask() | CHANNEL_MODIFY.asMask() | ADMINISTRATOR.asMask();

        PermissionOverwrites permissionOverwrites = new PermissionOverwrites(0);
        permissionOverwrites.deny(PermissionScope.CHANNEL, permissionMask);

        long data = permissionOverwrites.getPermissionOverwriteData();
        assertThat(data).isNotEqualTo(0L);
        assertThat(data).isNotEqualTo((long) permissionMask << 32); //It should not include Administrator perm
        assertThat(data).isEqualTo((long) (CHANNEL_CREATE.asMask() | CHANNEL_MODIFY.asMask()) << 32);
    }
}
