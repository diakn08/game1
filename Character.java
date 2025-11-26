package models;

import abilities.Ability;
import battle.BattleManager;
import effect.AttackBoost;
import effect.Effect;
import effect.Weakness;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Character {
    protected String name;
    protected int health, maxHealth;
    protected int strength, agility, endurance, intelligence, tactics, defense, speed;
    protected Ability ability;
    private Character tauntedBy = null;
    private int tauntDuration = 0;
    protected Character lastAttacker = null;
    protected List<Effect> activeEffects = new ArrayList<>();
    protected boolean silenced = false;
    protected boolean stunned = false;
    protected double bonusDamage = 1;
    protected BattleManager battleManager;

    public Character(String name, int str, int agi, int end, int intelli, int tac, int def, int spd, Ability ability) {
        this.name = name;
        this.strength = str;
        this.agility = agi;
        this.endurance = end;
        this.intelligence = intelli;
        this.tactics = tac;
        this.defense = (int) ((double)def * 1.35);
        this.speed = spd;
        this.ability = ability;
        this.health = 300 + end * 5 + str * 2;
        this.maxHealth = 300 + end * 5 + str * 2;
    }

    public double getTactics() {
        return (double) tactics;
    }

    public void setBattleManager(BattleManager battleManager) {
        this.battleManager = battleManager;
    }

    public List<Character> getEnemyTeam() {
        if (battleManager == null) {
            throw new IllegalStateException("Персонаж не участвует в битве!");
        }
        return battleManager.getEnemyTeam(this);
    }

    public abstract int attack();
    public abstract int defend(int damage);

    public void useAbility(Character target) {
        if (silenced) {
            System.out.printf("%s пытается использовать способность, но находится под эффектом 'Сайленс' и не может!%n", name);
            return;
        }
        if (ability != null) {
            ability.apply(this, target);
        }
    }

    public boolean isAlive() {
        return health > 0;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getSpeed() {
        return speed;
    }

    public boolean isTaunted() {
        return tauntedBy != null && tauntDuration > 0;
    }

    public Character getTaunter() {
        return tauntedBy;
    }

    public void decreaseTauntDuration() {
        if (tauntDuration > 0) {
            tauntDuration--;
            if (tauntDuration == 0) {
                tauntedBy = null;
            }
        }
    }

    public void setTauntedBy(Character taunter, int duration) {
        this.tauntedBy = taunter;
        this.tauntDuration = duration;
    }

    public void setLastAttacker(Character attacker) {
        this.lastAttacker = attacker;
    }

    public void applyEffect(Effect effect) {
        effect.apply(this);
        activeEffects.add(effect);
    }

    public void addEffect(Effect newEffect) {
        for (Effect effect : activeEffects) {
            if (effect.getName().equals(newEffect.getName())) {
                effect.refreshDuration(newEffect.getDuration());
                System.out.printf("🔄 Эффект '%s' обновлён! Новая длительность: %d ходов%n",
                        effect.getName(), effect.getDuration());
                return;
            }
        }
        activeEffects.add(newEffect);
        System.out.printf("🆕 Наложен эффект '%s' на %d ходов%n", newEffect.getName(), newEffect.getDuration());
    }

    public void updateEffects() {
        Iterator<Effect> iterator = activeEffects.iterator();
        while (iterator.hasNext()) {
            Effect effect = iterator.next();
            effect.onTurn(this);
            effect.decreaseDuration();
            if (effect.isExpired()) {
                effect.remove(this);
                iterator.remove();
            }
        }
    }


    public boolean isSilenced() {
        return silenced;
    }

    public void setSilenced(boolean silenced) {
        this.silenced = silenced;
    }

    public int applyWeakness(int damage) {
        for (Effect effect : activeEffects) {
            if (effect instanceof Weakness) {
                Weakness weaknessEffect = (Weakness) effect;
                damage = weaknessEffect.modifyOutgoingDamage(damage);
            }
        }
        return damage;
    }

    public int bonusDamage(int damage) {
        for (Effect effect : activeEffects) {
            if (effect instanceof AttackBoost) {
                AttackBoost attackBoost = (AttackBoost) effect;
                damage = attackBoost.modifyOutgoingDamage(damage);
            }
        }

        return damage;
    }

    public void takeDamage(int damage) {
        this.health -= damage;
    }

    public void setBonusDamage(double bonusDamage) {
        this.bonusDamage = bonusDamage;
    }

    public void setStunned(boolean stunned) {
        this.stunned = stunned;
    }

    public boolean isStunned() {
        return stunned;
    }
}
