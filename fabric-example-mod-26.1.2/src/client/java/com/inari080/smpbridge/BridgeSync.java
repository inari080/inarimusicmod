package com.inari080.smpbridge;

import com.musicmod.LocalMusicPlayer;
import com.musicmod.MusicMod;

/**
 * Keeps MusicPlayerBridgeConfig's live-state fields (volume, paused) in sync
 * with the real LocalMusicPlayer, in both directions:
 *
 *  - ThemedGuiMod hub slider/checkbox changed -> pushed into LocalMusicPlayer
 *  - external change (e.g. "/music volume", "/music pause", a keybind)
 *    -> pulled back into the config so the hub row shows the true state
 *
 * Call BridgeSync.tick() once per client tick (see SmpBridgeClient).
 */
public final class BridgeSync {

    private static int lastKnownVolume = -1;
    private static Boolean lastKnownPaused = null;

    private BridgeSync() {
    }

    public static void tick() {
        LocalMusicPlayer mp = MusicMod.getMusicPlayer();
        if (mp == null) {
            // simplemusicplayer hasn't finished initializing yet this tick
            return;
        }

        MusicPlayerBridgeConfig cfg = MusicPlayerBridgeConfig.INSTANCE;

        int actualVolume = mp.getVolume();
        if (lastKnownVolume == -1) {
            cfg.volume = actualVolume; // first tick: adopt whatever the player already has
        } else if (actualVolume != lastKnownVolume) {
            cfg.volume = actualVolume; // changed from outside the hub
        } else if (cfg.volume != actualVolume) {
            mp.setVolume(cfg.volume); // changed via the hub slider
        }
        lastKnownVolume = mp.getVolume();

        boolean actualPaused = mp.isPaused();
        if (lastKnownPaused == null) {
            cfg.paused = actualPaused;
        } else if (actualPaused != lastKnownPaused) {
            cfg.paused = actualPaused; // changed from outside the hub
        } else if (cfg.paused != actualPaused) {
            mp.setPaused(cfg.paused); // changed via the hub checkbox
        }
        lastKnownPaused = mp.isPaused();
    }
}