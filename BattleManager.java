package battle;

import models.Character;
import models.Sniper;

import java.util.List;
import java.util.Comparator;
import java.util.Random;

public class BattleManager {
    private List<Character> team1;
    private List<Character> team2;
    private Random random = new Random();

    public BattleManager(List<Character> team1, List<Character> team2) {
        this.team1 = team1;
        this.team2 = team2;

        for (Character c : team1) c.setBattleManager(this);
        for (Character c : team2) c.setBattleManager(this);
    }

    public List<Character> getEnemyTeam(Character character) {
        return team1.contains(character) ? team2 : team1;
    }

    public void startBattle() {
        System.out.println("Битва начинается!\n");

        sortBySpeed(team1);
        sortBySpeed(team2);

        int counter = 1;

        while (isTeamAlive(team1) && isTeamAlive(team2)) {
            System.out.println("Ход: " + counter);

            for (Character a : team1) {
                if (a instanceof Sniper) {
                    Sniper sniper = (Sniper) a;
                    sniper.setWasHitLastTurn(false);
                }
            }

            for (Character b : team2) {
                if (b instanceof Sniper) {
                    Sniper sniper = (Sniper) b;
                    sniper.setWasHitLastTurn(false);
                }
            }

            for (Character attacker : team1) {
                if (!attacker.isAlive()) continue;
                attacker.updateEffects();
                Character defender = getTarget(attacker, team2);
                if (defender == null) break;
                executeTurn(attacker, defender);
            }

            for (Character attacker : team2) {
                if (!attacker.isAlive()) continue;
                attacker.updateEffects();
                Character defender = getTarget(attacker, team1);
                if (defender == null) break;
                executeTurn(attacker, defender);
            }

            counter++;
        }

        System.out.println("\nБитва окончена! Победитель: " + (isTeamAlive(team1) ? "Команда 1" : "Команда 2"));
    }

    private void executeTurn(Character attacker, Character defender) {
        if (attacker.isStunned()) {
            System.out.printf("💫 %s оглушён и не может атаковать!%n", attacker.getName());
            return;
        }

        int damage = attacker.attack();
        int actualDamage = defender.defend(damage);
        defender.setLastAttacker(attacker);

        System.out.printf("%s (Команда %d) наносит %d урона по %s (Команда %d), проходит %d урона, оставшееся здоровье %d/%d%n",
                attacker.getName(), getTeam(attacker),
                damage, defender.getName(), getTeam(defender),
                actualDamage, defender.getHealth(), defender.getMaxHealth());

        double tactics = attacker.getTactics() / 100;
        if (tactics > 0.25) tactics = 0.25;
        if (Math.random() < (0.1 + tactics)) {
            attacker.useAbility(defender);
        }

        attacker.decreaseTauntDuration();
    }

    private void sortBySpeed(List<Character> team) {
        team.sort(Comparator.comparingInt(Character::getSpeed).reversed());
    }

    private boolean isTeamAlive(List<Character> team) {
        return team.stream().anyMatch(Character::isAlive);
    }

    private Character getTarget(Character attacker, List<Character> enemyTeam) {
        if (attacker.isTaunted()) {
            return attacker.getTaunter();
        }
        return getRandomAliveTarget(enemyTeam);
    }

    private Character getRandomAliveTarget(List<Character> team) {
        List<Character> aliveEnemies = team.stream().filter(Character::isAlive).toList();
        if (aliveEnemies.isEmpty()) return null;
        return aliveEnemies.get(random.nextInt(aliveEnemies.size()));
    }

    private int getTeam(Character character) {
        return team1.contains(character) ? 1 : 2;
    }
}
