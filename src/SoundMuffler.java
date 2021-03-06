package net.minecraft.src;

import java.util.HashMap;
import java.util.Iterator;

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
    public static final String VERSION = "1.6.2";
    public static boolean DEBUG = false;
    public static long MAX_LATENCY = 5000L; // that's one wicked ping time
    public static long CLEAN_EXPIRED_INTERVAL = 60000L;

    public static class SoundTrail {
        private final HashMap<String, Long> trail = new HashMap<String, Long>();
        private final String name;
        private long nextClean;

        public SoundTrail(String name) {
            this.name = name;
        }

        public void update(String soundName, int blockX, int blockY, int blockZ) {
            final long now = System.currentTimeMillis();
            String key = getSoundEventKey(soundName, blockX, blockY, blockZ);
            trail.put(key, now + MAX_LATENCY);
            if (DEBUG) {
                log("\u00a75updated sound trail " + name + ": " + key + "; size=" + trail.size());
            }

            // Periodically remove any sounds that we were planning to muffle
            // but never received from the server, perhaps because the player
            // respawned elsewhere.
            if (now >= nextClean) {
                int count = 0;
                final Iterator<Long> it = trail.values().iterator();
                while (it.hasNext()) {
                    if (now >= it.next()) {
                        count++;
                        it.remove();
                    }
                }
                if (DEBUG) {
                    log("\u00a73cleaned " + count + "/" + (count + trail.size()) +
                            " expired nodes for " + name);
                }
                nextClean = now + CLEAN_EXPIRED_INTERVAL;
            }
        }

        public boolean isOnTrailExact(String key) {
            if (trail.containsKey(key)) {
                if (System.currentTimeMillis() >= trail.get(key)) {
                    trail.remove(key);
                } else {
                    return true;
                }
            }
            return false;
        }
    }

    public static final SoundTrail placeTrail = new SoundTrail("place");

    /**
     * @return true if the sound event should be played, false to filter out
     */
    public static boolean checkMuffle(String soundName, double x, double y, double z) {
        String key = getSoundEventKey(soundName, getBlockCoord(x), getBlockCoord(y), getBlockCoord(z));
        if (placeTrail.isOnTrailExact(key)) {
            if (DEBUG) {
                log("\u00a74...muffled place: " + key);
            }
            return false;
        }
        if (DEBUG) {
            log("\u00a72allowed " + key);
        }
        return true;
    }

    /**
     * Uniquely (well, as uniquely as feasible) identify a sound event.
     */
    public static String getSoundEventKey(String soundName, int blockX, int blockY, int blockZ) {
        return new StringBuilder().append(soundName).append('@')
                .append(blockX).append(',')
                .append(blockY).append(',')
                .append(blockZ).toString();
    }

    public static int getBlockCoord(double coord) {
        return (int) Math.floor(coord);
    }

    public static void log(String message) {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(message);
    }
}