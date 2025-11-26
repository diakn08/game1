package abilities;

import models.Character;

public class MasterOfKilling extends Ability {
    private final int bonusDamage = 15;
    private boolean activated = false;

    public MasterOfKilling() {
        super("Мастер убийства");
    }


    @Override
    public void apply(Character user, Character target) {
        if (!activated) {
            System.out.printf("\uD83D\uDD25 %s повышает урон на %d процентов до конца игры !%n",
                    user.getName(), bonusDamage);
            user.setBonusDamage(((double) bonusDamage/100 + 1));
            activated = true;
        }
    }
}
