package fr.yanis.lg100ms.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.yanis.lg100ms.LGMSMain;
import fr.yanis.lg100ms.scenario.AutoResizeGroup;
import fr.yanis.lg100ms.scenario.NoCamp;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoResizeGroupGui implements InventoryProvider {

    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("config_arg")
            .manager(LGMSMain.getInstance().getInvManager())
            .provider(new AutoResizeGroupGui())
            .size(3, 9)
            .title("§cMenu Administratif - AutoResizeGroup")
            .closeable(true)
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillBorders(ClickableItem.empty(new ItemBuilder(Material.STAINED_GLASS_PANE).setDisplayName("§c").build()));
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§9Pourcentage de villageois: §b" + AutoResizeGroup.getPercentageVillager() + "%");
        lore.add("§9Pourcentage de loup-garou: §b" + AutoResizeGroup.getPercentageWerewolf() + "%");
        lore.add("§9Pourcentage de neutre: §b" + AutoResizeGroup.getPercentageNeutral() + "%");
        if(LGMSMain.getInstance().getGameStats().isStarted()) contents.set(1, 3, ClickableItem.empty(new ItemBuilder(Material.BOOK).setDisplayName("§bPourcentage").setLore(lore).build()));
        else contents.set(1, 1, ClickableItem.empty(new ItemBuilder(Material.BOOK).setDisplayName("§bPourcentage").setLore("§cLa partie n'a pas encore commencé").build()));

        contents.set(1,7, ClickableItem.of(new ItemBuilder(Material.BARRIER).setDisplayName("§cRetour").build(), e -> ScenarioGui.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§9Pourcentage de villageois: §b" + AutoResizeGroup.getPercentageVillager() + "%");
        lore.add("§9Pourcentage de loup-garou: §b" + AutoResizeGroup.getPercentageWerewolf() + "%");
        lore.add("§9Pourcentage de neutre: §b" + AutoResizeGroup.getPercentageNeutral() + "%");
        if(LGMSMain.getInstance().getGameStats().isStarted()) contents.set(1, 3, ClickableItem.empty(new ItemBuilder(Material.BOOK).setDisplayName("§bPourcentage").setLore(lore).build()));
        else contents.set(1, 1, ClickableItem.empty(new ItemBuilder(Material.BOOK).setDisplayName("§bPourcentage").setLore("§cLa partie n'a pas encore commencé").build()));
    }
}
