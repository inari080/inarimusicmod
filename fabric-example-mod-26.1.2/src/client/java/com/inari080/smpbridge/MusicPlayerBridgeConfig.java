package com.inari080.smpbridge;

import com.example.themedgui.client.config.Setting;
import com.musicmod.LocalMusicPlayer;
import com.musicmod.MusicMod;

import java.util.function.Consumer;

/**
 * @Setting-annotated config holder for Simple Music Player, discovered by
 * ThemedGuiMod's SettingRegistry via reflection and shown in the O-key hub.
 *
 * volume/paused are "live" fields: SettingRegistry treats them as plain
 * persisted ints/booleans, but BridgeSync (see BridgeSync.java) mirrors them
 * against the real LocalMusicPlayer every client tick, in both directions.
 *
 * skip/stopAll are Runnable fields -> ThemedGuiMod renders these as buttons
 * (ACTION kind) and never persists them to config/simplemusicplayer.json.
 */
public final class MusicPlayerBridgeConfig {

    public static final MusicPlayerBridgeConfig INSTANCE = new MusicPlayerBridgeConfig();

    @Setting(category = "Playback", label = "Volume",
            tooltip = "Matches /music volume (0-150)", min = 0, max = 150)
    public int volume = 80;

    @Setting(category = "Playback", label = "Pause",
            tooltip = "Pause or resume the current track")
    public boolean paused = false;

    @Setting(category = "Playback", label = "Skip Track")
    public Runnable skip = () -> withPlayer(LocalMusicPlayer::skip);

    @Setting(category = "Playback", label = "Stop && Clear Queue")
    public Runnable stopAll = () -> withPlayer(LocalMusicPlayer::stop);

    @Setting(category = "Playback", label = "Save Current URL",
            tooltip = "Saves the playing track's URL to music-urls/<title>.txt")
    public Runnable saveUrl = TrackUrlSaver::saveCurrentTrack;

    private MusicPlayerBridgeConfig() {
    }

    static void withPlayer(Consumer<LocalMusicPlayer> action) {
        LocalMusicPlayer mp = MusicMod.getMusicPlayer();
        if (mp != null) action.accept(mp);
    }
}