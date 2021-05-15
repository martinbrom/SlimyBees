package cz.martinbrom.slimybees.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class ChunkLoadListener implements Listener {

    @EventHandler
    public void chunkLoad(ChunkLoadEvent event) {
        // TODO: 15.05.21 Add the option to turn off worldgen and get bees a different way
        if (event.isNewChunk()) {
            // TODO: 15.05.21 Generate hives
        }
    }

}
