package com.szampchat.server.permission.data;

import org.assertj.core.api.Condition;
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
        assertThat((data & (1L << CHANNEL_CREATE.asMask())) == 0)
                .as("check that permission overwrite doesn't include CHANNEL_CREATE in CHANNEL scope")
                .isTrue();
        assertThat((data & (1L << ADMINISTRATOR.asMask())) == 0)
                .as("check that permission overwrite doesn't include ADMINISTRATOR in CHANNEL scope")
                .isTrue();
        assertThat(data)
                .as("check that permission overwrite only includes CHANNEL_MODIFY which can be overwritten in CHANNEL scope")
                .isEqualTo(CHANNEL_MODIFY.asMask());
    }

    @Test
    public void givenMaskAndChannelContext_whenDeny_shouldSetCorrectFlags() {
        int permissionMask = CHANNEL_CREATE.asMask() | CHANNEL_MODIFY.asMask() | ADMINISTRATOR.asMask();

        PermissionOverwrites permissionOverwrites = new PermissionOverwrites(0);
        permissionOverwrites.deny(PermissionScope.CHANNEL, permissionMask);

        long data = permissionOverwrites.getPermissionOverwriteData();
        assertThat((data & (1L << (CHANNEL_CREATE.asMask() + 32))) == 0)
                .as("check that permission overwrite doesn't include CHANNEL_CREATE in CHANNEL scope")
                .isTrue();
        assertThat((data & (1L << (ADMINISTRATOR.asMask() + 32))) == 0)
                .as("check that permission overwrite doesn't include ADMINISTRATOR in CHANNEL scope")
                .isTrue();
        assertThat(data)
                .as("check that permission overwrite only includes CHANNEL_MODIFY which can be overwritten in CHANNEL scope")
                .isEqualTo((long) CHANNEL_MODIFY.asMask() << 32);
    }
}
