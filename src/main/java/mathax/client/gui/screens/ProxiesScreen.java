package mathax.client.gui.screens;

import mathax.client.gui.renderer.GuiRenderer;
import mathax.client.gui.widgets.WLabel;
import mathax.client.gui.widgets.containers.WHorizontalList;
import mathax.client.gui.widgets.containers.WTable;
import mathax.client.gui.widgets.input.WDropdown;
import mathax.client.gui.widgets.input.WIntEdit;
import mathax.client.gui.widgets.input.WTextBox;
import mathax.client.gui.widgets.pressable.WButton;
import mathax.client.gui.widgets.pressable.WCheckbox;
import mathax.client.gui.widgets.pressable.WMinus;
import mathax.client.systems.proxies.Proxies;
import mathax.client.systems.proxies.Proxy;
import mathax.client.systems.proxies.ProxyType;
import mathax.client.utils.misc.NbtUtils;
import mathax.client.gui.GuiTheme;
import mathax.client.gui.WindowScreen;

import java.util.ArrayList;
import java.util.List;

import static mathax.client.MatHax.mc;

public class ProxiesScreen extends WindowScreen {
    private final List<WCheckbox> checkboxes = new ArrayList<>();
    private boolean doReload;

    public ProxiesScreen(GuiTheme theme) {
        super(theme, "Proxies");
    }

    protected void openEditProxyScreen(Proxy proxy) {
        mc.setScreen(new EditProxyScreen(theme, proxy));
    }

    @Override
    protected void init() {
        if (!doReload) return;

        reload();
        doReload = false;
    }

    @Override
    public void initWidgets() {
        // Proxies
        WTable table = add(theme.table()).expandX().widget();

        int i = 0;
        for (Proxy proxy : Proxies.get()) {
            int index = i;

            // Enabled
            WCheckbox enabled = table.add(theme.checkbox(proxy.enabled)).widget();
            checkboxes.add(enabled);
            enabled.action = () -> {
                boolean checked = enabled.checked;
                Proxies.get().setEnabled(proxy, checked);

                for (WCheckbox checkbox : checkboxes) checkbox.checked = false;
                checkboxes.get(index).checked = checked;
            };

            // Name
            WLabel name = table.add(theme.label(proxy.name)).widget();
            name.color = theme.textColor();

            // Type
            WLabel type = table.add(theme.label("(" + proxy.type + ")")).widget();
            type.color = theme.textSecondaryColor();

            // IP + Port
            WHorizontalList ipList = table.add(theme.horizontalList()).expandCellX().widget();
            ipList.spacing = 0;

            ipList.add(theme.label(proxy.address));
            ipList.add(theme.label(":")).widget().color = theme.textSecondaryColor();
            ipList.add(theme.label(Integer.toString(proxy.port)));

            // Edit
            WButton edit = table.add(theme.button(GuiRenderer.EDIT)).widget();
            edit.action = () -> openEditProxyScreen(proxy);

            // Remove
            WMinus remove = table.add(theme.minus()).widget();
            remove.action = () -> {
                Proxies.get().remove(proxy);
                reload();
            };

            table.row();
            i++;
        }

        // New
        WButton newBtn = add(theme.button("New")).expandX().widget();
        newBtn.action = () -> openEditProxyScreen(null);
    }

    @Override
    public boolean toClipboard() {
        return NbtUtils.toClipboard(Proxies.get());
    }

    @Override
    public boolean fromClipboard() {
        return NbtUtils.fromClipboard(Proxies.get());
    }

    protected class EditProxyScreen extends WindowScreen {
        private final boolean isNew;
        private final Proxy proxy;

        public EditProxyScreen(GuiTheme theme, Proxy p) {
            super(theme, p == null ? "New Proxy" : "Edit Proxy");

            isNew = p == null;
            proxy = isNew ? new Proxy() : p;
        }

        @Override
        public void initWidgets() {
            // General
            WTable general = add(theme.table()).expandX().widget();

            // Name
            general.add(theme.label("Proxy Name:"));
            WTextBox name = general.add(theme.textBox(proxy.name)).expandX().widget();
            name.action = () -> proxy.name = name.get();
            general.row();

            // Type
            general.add(theme.label("Type:"));
            WDropdown<ProxyType> type = general.add(theme.dropdown(proxy.type)).widget();
            type.action = () -> proxy.type = type.get();
            general.row();

            // IP
            general.add(theme.label("IP:"));
            WTextBox ip = general.add(theme.textBox(proxy.address)).minWidth(400).expandX().widget();
            ip.action = () -> proxy.address = ip.get();
            general.row();

            // Port
            general.add(theme.label("Port:"));
            WIntEdit port = general.add(theme.intEdit(proxy.port, 0, 65535, true)).expandX().widget();
            port.action = () -> proxy.port = port.get();

            // Optional
            add(theme.horizontalSeparator("Optional")).expandX().widget();
            WTable optional = add(theme.table()).expandX().widget();

            // Username
            optional.add(theme.label("Username:"));
            WTextBox username = optional.add(theme.textBox(proxy.username)).expandX().widget();
            username.action = () -> proxy.username = username.get();
            optional.row();

            // Password
            optional.add(theme.label("Password:"));
            WTextBox password = optional.add(theme.textBox(proxy.password)).expandX().widget();
            password.action = () -> proxy.password = password.get();

            // Add/Save
            add(theme.horizontalSeparator()).expandX();

            WButton addSave = add(theme.button(isNew ? "Add" : "Save")).expandX().widget();
            addSave.action = () -> {
                if (proxy.resolveAddress() && (!isNew || Proxies.get().add(proxy))) {
                    doReload = true;
                    onClose();
                }
            };

            enterAction = addSave.action;
        }
    }
}
