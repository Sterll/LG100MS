package fr.yanis.lg100ms.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.yanis.lg100ms.LGMSMain;
import fr.yanis.lg100ms.commands.admin.Command100ms;
import fr.yanis.lg100ms.scenario.NoCamp;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ScenarioGui implements InventoryProvider {

    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("config_scenario")
            .manager(LGMSMain.getInstance().getInvManager())
            .provider(new ScenarioGui())
            .size(3, 9)
            .title("§cMenu Administratif - Scenario")
            .closeable(true)
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillBorders(ClickableItem.empty(new ItemBuilder(Material.STAINED_GLASS_PANE).setDisplayName("§c").build()));
        contents.set(1, 1, ClickableItem.of(new ItemBuilder(Material.GOLDEN_APPLE).setDisplayName("§9NoCamp").build(), e -> NoCampGui.INVENTORY.open(player)));
        contents.set(1, 3, ClickableItem.of(new ItemBuilder(Material.BEACON).setDisplayName("§9AutoResizeGroup").build(), e -> AutoResizeGroupGui.INVENTORY.open(player)));

        contents.set(1,7, ClickableItem.of(new ItemBuilder(Material.BARRIER).setDisplayName("§cRetour").build(), e -> MainGui.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }
}
