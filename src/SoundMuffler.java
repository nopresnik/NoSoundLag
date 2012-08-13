package net.minecraft.src;

import java.util.HashMap;

import net.minecraft.client.Minecraft;

/**
 * Keep track of future sound events that we want to ignore from the server.
 * This isn't an exact science: there is no way to take a server sound event
 * and definitively tie it to an earlier event generated by the player.
 * However, we can make a reasonable guess by remembering the coordinates
 * of those earlier events and using a timeout window.
 * 
 * @author bencvt
 */
public abstract class SoundMuffler {
    public static final String SOURCE_URL = "https://github.com/bencvt/NoSoundLag";
    public static final long MAX_LATENCY = 5000L; // that's one wicked ping time
    private static final HashMap<String, Long> sounds = new HashMap<String, Long>();
    private static long lastRemoveExpired;

    public static void muffle(String soundName, int blockX, int blockY, int blockZ) {
        long now = System.currentTimeMillis();
        String key = getKey(soundName, blockX, blockY, blockZ);
        sounds.put(key, now + MAX_LATENCY);
        //Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage("muffling... " + key);
        if (now > lastRemoveExpired + 5*60000) {
            removeExpired();
        }
    }

    /**
     * @return true if the sound event should be played, false to filter out
     */
    public static boolean removeAndCheckMuffle(String soundName, int blockX, int blockY, int blockZ) {
        String key = getKey(soundName, blockX, blockY, blockZ);
        Long value = sounds.remove(key);
        //Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage("checking " + key);
        if (value == null || System.currentTimeMillis() > value) {
            return true;
        }
        //Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage("...muffled " + key);
        return false;
    }

    /**
     * Remove any sounds that we were planning to muffle but never received from the server,
     * perhaps because the player respawned elsewhere.
     */
    public static void removeExpired() {
        lastRemoveExpired = System.currentTimeMillis();
        for (String key : sounds.keySet()) {
            Long value = sounds.get(key);
            if (value != null && lastRemoveExpired > value) {
                sounds.remove(key);
            }
        }
    }

    /**
     * Uniquely (well, as uniquely as feasible) identify a sound event.
     */
    private static String getKey(String soundName, int blockX, int blockY, int blockZ) {
        return String.format("%s@%d,%d,%d", soundName, blockX, blockY, blockZ);
    }

    public static int getBlockCoord(double coord) {
        return (int) (coord > 0.0 ? coord : coord - 1.0);
    }
}
