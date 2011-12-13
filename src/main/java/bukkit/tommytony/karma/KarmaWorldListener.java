package bukkit.tommytony.karma;

import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldSaveEvent;

public class KarmaWorldListener extends WorldListener {

	private final Karma karma;

	public KarmaWorldListener(Karma karma) {
		this.karma = karma;
	}

	@Override
    public void onWorldSave(WorldSaveEvent event) {
        this.karma.getKarmaDatabase().putAll();
    }
}
