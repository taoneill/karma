package bukkit.tommytony.karma;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;

public class KarmaWorldListener implements Listener {

	private final Karma karma;

	public KarmaWorldListener(Karma karma) {
		this.karma = karma;
	}

	@EventHandler
    public void onWorldSave(final WorldSaveEvent event) {
        this.karma.getKarmaDatabase().putAll();
    }
}
