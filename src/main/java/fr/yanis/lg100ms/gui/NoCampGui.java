package fr.yanis.lg100ms.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.yanis.lg100ms.LGMSMain;
import fr.yanis.lg100ms.scenario.NoCamp;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NoCampGui implements InventoryProvider {

    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("config_nocamp")
            .manager(LGMSMain.getInstance().getInvManager())
            .provider(new NoCampGui())
            .size(3, 9)
            .title("§cMenu Administratif - NoCamp")
            .closeable(true)
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillBorders(ClickableItem.empty(new ItemBuilder(Material.STAINED_GLASS_PANE).setDisplayName("§c").build()));
        contents.set(1, 1, ClickableItem.of(new ItemBuilder(Material.BOOK).setDisplayName(NoCamp.isActivate() ? "§aActivé" : "§cDésactivé")
                .build(), e -> NoCamp.setActivate(!NoCamp.isActivate())));
        if(LGMSMain.getInstance().getGameStats().isStarted()) contents.set(1, 3, ClickableItem.empty(new ItemBuilder(Material.BOOK).setDisplayName("§bTop 10").setLore(getTop10()).build()));
        else contents.set(1, 3, ClickableItem.empty(new ItemBuilder(Material.BOOK).setDisplayName("§bTop 10").setLore("§cLa partie n'a pas encore commencé").build()));

        contents.set(1,7, ClickableItem.of(new ItemBuilder(Material.BARRIER).setDisplayName("§cRetour").build(), e -> ScenarioGui.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        contents.set(1, 1, ClickableItem.of(new ItemBuilder(Material.BOOK).setDisplayName(NoCamp.isActivate() ? "§aActivé" : "§cDésactivé")
                .build(), e -> NoCamp.setActivate(!NoCamp.isActivate())));
        if(LGMSMain.getInstance().getGameStats().isStarted()) contents.set(1, 3, ClickableItem.empty(new ItemBuilder(Material.BOOK).setDisplayName("§bTop 10").setLore(getTop10()).build()));
        else contents.set(1, 3, ClickableItem.empty(new ItemBuilder(Material.BOOK).setDisplayName("§bTop 10").setLore("§cLa partie n'a pas encore commencé").build()));
    }

    public List<String> getTop10(){
        List<String> top10 = new ArrayList<>();
        NoCamp.getCompteur().entrySet().stream().sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())).limit(10).forEach(e -> top10.add("§7" + e.getKey().getName() + " §f: §e" + e.getValue()));
        return top10;
    }
}
