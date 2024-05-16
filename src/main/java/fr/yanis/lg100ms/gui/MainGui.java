package fr.yanis.lg100ms.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.yanis.lg100ms.LGMSMain;
import fr.yanis.lg100ms.commands.admin.Command100ms;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class MainGui implements InventoryProvider {

    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("config")
            .manager(LGMSMain.getInstance().getInvManager())
            .provider(new MainGui())
            .size(3, 9)
            .title("§cMenu Administratif - Accueil")
            .closeable(true)
            .build();

    public MainGui() {

    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillBorders(ClickableItem.empty(new ItemBuilder(Material.STAINED_GLASS_PANE).setDisplayName("§c").build()));

        contents.set(1, 1, ClickableItem.of(new ItemBuilder(Material.BOOK).setDisplayName("§9Scénarios").build(), e -> ScenarioGui.INVENTORY.open(player)));

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(LGMSMain.getInstance().isDev() ? "§aActivé" : "§cDésactivé");
        contents.set(1, 7, ClickableItem.of(new ItemBuilder(UniversalMaterial.REDSTONE.getStack())
                .setDisplayName("§9Dev Mode").setLore(lore)
                .build(), e -> LGMSMain.getInstance().setDev(!LGMSMain.instance.isDev())));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(LGMSMain.getInstance().isDev() ? "§aActivé" : "§cDésactivé");
        contents.set(1, 7, ClickableItem.of(new ItemBuilder(UniversalMaterial.REDSTONE.getStack())
                .setDisplayName("§9Dev Mode").setLore(lore)
                .build(), e -> LGMSMain.getInstance().setDev(!LGMSMain.instance.isDev())));
    }
}
