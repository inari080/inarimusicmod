package com.inari080.smpbridge;

import com.musicmod.LocalMusicPlayer;
import com.musicmod.MusicMod;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Saves the URL of whatever track is currently playing to a text file named
 * after the track title. LocalMusicPlayer doesn't expose the underlying
 * lavaplayer AudioPlayer publicly, so this reaches its private "audioPlayer"
 * field via reflection to read the playing AudioTrack's info (title/uri).
 */
public final class TrackUrlSaver {

    private static final Path SAVE_DIR =
            FabricLoader.getInstance().getGameDir().resolve("music-urls");

    private TrackUrlSaver() {
    }

    public static void saveCurrentTrack() {
        LocalMusicPlayer mp = MusicMod.getMusicPlayer();
        if (mp == null) {
            notify("§c[Music] §7Player not ready yet.");
            return;
        }

        AudioTrack track;
        try {
            Field audioPlayerField = LocalMusicPlayer.class.getDeclaredField("audioPlayer");
            audioPlayerField.setAccessible(true);
            AudioPlayer audioPlayer = (AudioPlayer) audioPlayerField.get(mp);
            track = audioPlayer.getPlayingTrack();
        } catch (ReflectiveOperationException e) {
            notify("§c[Music] §7Couldn't read the current track (mod update may have changed internals).");
            return;
        }

        if (track == null) {
            notify("§c[Music] §7Nothing is playing right now.");
            return;
        }

        AudioTrackInfo info = track.getInfo();
        String safeTitle = sanitizeFileName(info.title);

        try {
            Files.createDirectories(SAVE_DIR);
            Path file = uniqueFile(SAVE_DIR, safeTitle, ".txt");
            Files.writeString(file, info.uri);
            notify("§a[Music] §7Saved URL to §f" + file.getFileName());
        } catch (IOException e) {
            notify("§c[Music] §7Failed to save URL: " + e.getMessage());
        }
    }

    /** Strips characters that are illegal in Windows/macOS/Linux file names. */
    private static String sanitizeFileName(String rawTitle) {
        String cleaned = rawTitle == null ? "untitled" : rawTitle.trim();
        cleaned = cleaned.replaceAll("[\\\\/:*?\"<>|]", "_");
        cleaned = cleaned.replaceAll("\\s+", " ");
        if (cleaned.isEmpty()) cleaned = "untitled";
        return cleaned.length() > 100 ? cleaned.substring(0, 100) : cleaned;
    }

    /** Appends " (2)", " (3)", ... if a file with that title already exists. */
    private static Path uniqueFile(Path dir, String baseName, String extension) {
        Path candidate = dir.resolve(baseName + extension);
        int counter = 2;
        while (Files.exists(candidate)) {
            candidate = dir.resolve(baseName + " (" + counter + ")" + extension);
            counter++;
        }
        return candidate;
    }

    private static void notify(String message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.sendSystemMessage(Component.literal(message));
        }
    }
}