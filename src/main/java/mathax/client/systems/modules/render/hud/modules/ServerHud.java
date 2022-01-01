package mathax.client.systems.modules.render.hud.modules;

import mathax.client.systems.modules.render.hud.DoubleTextHudElement;
import mathax.client.systems.modules.render.hud.HUD;
import mathax.client.utils.Utils;

public class ServerHud extends DoubleTextHudElement {
    public ServerHud(HUD hud) {
        super(hud, "server", "Displays the server you're currently in.", true);
    }

    @Override
    protected String getLeft() {
        return "Server: ";
    }

    @Override
    protected String getRight() {
        if (!Utils.canUpdate()) return "None";

        return Utils.getWorldName();
    }
}
