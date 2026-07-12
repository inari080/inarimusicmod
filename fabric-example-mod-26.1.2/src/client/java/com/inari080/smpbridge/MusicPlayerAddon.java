package com.inari080.smpbridge;

import com.example.themedgui.client.api.AddonRegistration;
import com.example.themedgui.client.api.ThemedGuiAddon;

/**
 * Registered via the "themedgui:addon" entrypoint in fabric.mod.json.
 * Plugs Simple Music Player's controls into ThemedGuiMod's hub screen (O key).
 */
public final class MusicPlayerAddon implements ThemedGuiAddon {

    @Override
    public void register(AddonRegistration registration) {
        registration.registerMod(
                "simplemusicplayer",
                "Simple Music Player",
                MusicPlayerBridgeConfig.INSTANCE,
                null // no icon texture; pass an Identifier.of(...) here if you add one later
        );
    }
}