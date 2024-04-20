package fr.yanis.lg100ms.role;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.yanis.lg100ms.LGMSMain;
import org.jetbrains.annotations.NotNull;

@Role(key = Baron.KEY + ".display",
        category = Category.NEUTRAL,
        attribute = RoleAttribute.NEUTRAL,
        defaultAura = Aura.DARK,
        incompatibleRoles = {RoleBase.ANGEL, RoleBase.GUARDIAN_ANGEL, RoleBase.SERIAL_KILLER, RoleBase.RIVAL, RoleBase.FLUTE_PLAYER, RoleBase.NECROMANCER, RoleBase.ROMULUS_REMUS, RoleBase.MASTERMIND}) // Need to add - Escrpc and Hitman and 100ms role
public class Baron extends RoleNeutral {

    public static final String KEY = LGMSMain.KEY + ".role.baron";

    public Baron(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(this.game, this).setDescription(game.translate(KEY + ".description"))
                .setItems(game.translate(KEY + ".items"))
                .build();
    }

    @Override
    public void recoverPower() {

    }
}
