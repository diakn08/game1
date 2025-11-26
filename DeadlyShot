package abilities;

import models.Character;

public class DeadlyShot extends Ability {
    public DeadlyShot() {
        super("Смертельный выстрел");
    }

    public void apply(Character user, Character target) {
        int healthThreshold = target.getMaxHealth() * 30 / 100;
        if (target.getHealth() <= healthThreshold) {
            System.out.printf("💀 %s наносит смертельный выстрел в %s! Враг мгновенно погиб!%n",
                    user.getName(), target.getName());
            target.takeDamage(target.getHealth());
        } else {
            int damage = (int) ((double) user.attack() * 0.35);
            int realDamage = target.defend(damage);
            System.out.printf("🎯 %s выпускает 'Смертельный выстрел' по %s, нанося %d урона!%n",
                    user.getName(), target.getName(), realDamage);

        }
    }
}
