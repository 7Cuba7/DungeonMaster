package zaidimas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Random;

public class Gameplay {
  private Characters charactersData;
  private LinkedList<Characters.Character> selectedHeroes;
  private LinkedList<Characters.Character> selectedMonsters;
  private PriorityQueue<Characters.Character> turnOrder;
  Scanner scanner = new Scanner(System.in);
  public static final String RED = "\u001B[31m";
  public static final String GREEN = "\u001B[32m";
  public static final String RESET = "\u001B[0m";

  public Gameplay(Characters charactersData) {
    this.charactersData = charactersData;
    this.selectedHeroes = new LinkedList<>();
    this.selectedMonsters = new LinkedList<>();
    this.turnOrder = new PriorityQueue<>((a, b) -> b.speed - a.speed);

  }

  public void game() {
    chooseHeroes();
    chooseMonsters();
    initializeOrder();
    round();
  }

  public enum StatusEffect {
    POISON, STUN, BLIND, CONFUSE, REGENERATION, BLEED
  }

  public void chooseHeroes() {
    System.out.println("Choose 3 Heroes:");

    // Display available heroes
    for (int i = 0; i < charactersData.characterGroup.heroes.size(); i++) {
      Characters.Character hero = charactersData.characterGroup.heroes.get(i);
      System.out.println((i + 1) + ". " + hero.name);
    }

    // Select heroes
    for (int i = 1; i <= 3; i++) {
      System.out.print("Enter the number for Hero " + i + ": ");
      int choice = scanner.nextInt();

      // Validate the choice
      while (choice < 1 || choice > charactersData.characterGroup.heroes.size()
          || selectedHeroes.contains(charactersData.characterGroup.heroes.get(choice - 1))) {
        System.out.print("Invalid choice or hero already selected. Try again: ");
        choice = scanner.nextInt();
      }

      // Add the selected hero to the list
      Characters.Character selectedHero = charactersData.characterGroup.heroes.get(choice - 1);
      selectedHeroes.add(selectedHero);
      System.out.println(selectedHero.name + " has been selected.");
    }

    System.out.println("\nYour team:");
    for (Characters.Character hero : selectedHeroes) {
      System.out.println("- " + hero.name);
    }
  }

  public void chooseMonsters() {
    Random random = new Random();
    int randomNumber;
    int[] choices = { 2, 3, 4 };
    int randomValue = choices[random.nextInt(choices.length)];

    for (int i = 1; i <= randomValue; i++) {
      randomNumber = random.nextInt(7);
      Characters.Character selectedMonster = charactersData.characterGroup.monsters.get(randomNumber);

      // Clone the selected monster to create a new independent instance
      Characters.Character monsterClone = cloneCharacter(selectedMonster);
      selectedMonsters.add(monsterClone);
    }

    System.out.println("Monsters team:");
    for (Characters.Character monster : selectedMonsters) {
      System.out.println("- " + monster.name);
    }
  }

  public Characters.Character cloneCharacter(Characters.Character original) {
    Characters.Character clone = new Characters.Character();
    clone.name = original.name;
    clone.type = original.type;
    clone.hp = original.hp;
    clone.speed = original.speed;
    clone.resistances = new HashMap<>(original.resistances); 
    clone.criticalDamage = original.criticalDamage;
    clone.abilities = new ArrayList<>(original.abilities);
    clone.statusEffects = new LinkedList<>(original.statusEffects); 
    clone.poisonTurns = original.poisonTurns;
    clone.bleedTurns = original.bleedTurns;
    clone.stunTurns = original.stunTurns;

    return clone;
  }

  public void initializeOrder() {
    turnOrder.clear();
    turnOrder.addAll(selectedHeroes);
    turnOrder.addAll(selectedMonsters);
  }

  public void UI() {
    Characters.Character current = turnOrder.peek();
    System.out.println(
        "#######################################################################################################");
    System.out.println(GREEN);
    System.out.println("Heroes Team");
    System.out.println("-----------");

    // Prints each hero's info
    for (Characters.Character hero : selectedHeroes) {
      System.out.println(hero.name);
      System.out.println("Hp:" + hero.hp);
      System.out.println("-----------");
    }

    System.out.println(RED);
    System.out.println("\nMonsters Team" + RED);
    System.out.println("-----------");

    // Prints each monster's info
    for (Characters.Character monster : selectedMonsters) {
      System.out.println(monster.name);
      System.out.println("Hp:" + monster.hp);
      System.out.println("-----------");
    }
    System.out.println(RESET);
    System.out.println(
        "#######################################################################################################");
  }

  public void round() {
    while (!selectedHeroes.isEmpty() && !selectedMonsters.isEmpty()) {

      Random random = new Random();
      int blind;
      Characters.Character current = turnOrder.poll();

      String input = scanner.nextLine();
      if (input.equalsIgnoreCase("quit")) {
        endGame();
        break;
      }

      if (selectedHeroes.contains(current)) {
        UI();
      }

      if (selectedHeroes.contains(current)) {
        if (current.statusEffects != null && current.statusEffects.contains(StatusEffect.STUN)) {
          current.statusEffects.remove(StatusEffect.STUN);
          System.out.println(current.name + " is STUNNED");
        } else {
          if (current.statusEffects != null && current.statusEffects.contains(StatusEffect.BLIND)) {
            current.statusEffects.remove(StatusEffect.BLIND);
            blind = random.nextInt(2);
            if (blind == 0) {
              System.out.println(current.name + " missed the attack");
            } else {
              shorterHeroAttackMonster(current);

            }
          } else {
            shorterHeroAttackMonster(current);

          }
        }
      } else if (selectedMonsters.contains(current)) {
        if (current.statusEffects != null && current.statusEffects.contains(StatusEffect.STUN)) {
          current.statusEffects.remove(StatusEffect.STUN);
          System.out.println(current.name + " is STUNNED");
        } else {
          if (current.statusEffects != null && current.statusEffects.contains(StatusEffect.BLIND)) {
            current.statusEffects.remove(StatusEffect.BLIND);
            blind = random.nextInt(2);
            if (blind == 0) {
              System.out.println(current.name + " missed the attack");
            } else {
              shorterMonsterAttackHero(current);
            }
          } else {
            shorterMonsterAttackHero(current);
          }
        }
      }

      if (turnOrder.size() == 0) {
        initializeOrder();
      }

      applyStatusEffects();
    }
    System.out.print("\033[H\033[2J");
    System.out.flush();
    if (selectedHeroes.isEmpty()) {
      System.out.println("You lost, monsters defeated you");
    } else {
      System.out.println("Congratulation, you deafeated all monsters");
    }
  }

  public void calculations(Characters.Character current, int attackChoice, int abilityChoice) {
    Characters.Character target;
    if (selectedHeroes.contains(current)) {
      target = selectedMonsters.get(attackChoice);
    } else {
      target = selectedHeroes.get(attackChoice);
    }

    Characters.Ability ability = current.abilities.get(abilityChoice);

    // Resistance
    int resistance = target.resistances.getOrDefault(ability.element, 0);
    int reducedDamage = ability.damage - (ability.damage * resistance / 100);

    applyEffect(current, ability, target);

    // Critical attack
    Random random = new Random();
    boolean isCritical = random.nextInt(100) < current.criticalDamage;
    if (isCritical) {
      reducedDamage *= 1.5;
      System.out.println("Critical Hit!");
    }

    target.hp -= reducedDamage;
    if (target.hp < 0)
      target.hp = 0;

    System.out.println(current.name + " used " + ability.name + " on " + target.name);
    System.out.println("Damage dealt: " + reducedDamage);
    System.out.println(target.name + "'s remaining HP: " + target.hp);
    System.out.println(" ");

    if (target.hp == 0 && selectedMonsters.contains(target)) {
      System.out.println(target.name + " has been defeated!");
      selectedMonsters.remove(target);
    }
    if (target.hp == 0 && selectedHeroes.contains(target)) {
      System.out.println(target.name + " has been defeated!");
      selectedHeroes.remove(target);
    }

  }

  private void applyStatusEffects() {
    for (Characters.Character character : selectedHeroes) {
      applyStatusEffectCalculations(character);
    }
    for (Characters.Character character : selectedMonsters) {
      applyStatusEffectCalculations(character);
    }
  }

  private void applyStatusEffectCalculations(Characters.Character character) {
    if (character.statusEffects != null && character.statusEffects.contains(StatusEffect.POISON)) {
      // Poison lasts for 3 turns
      if (character.poisonTurns > 0) {
        int poisonDamage = 10; // Set poison damage per turn
        character.hp -= poisonDamage;
        character.poisonTurns--;
        System.out.println(character.name + " takes " + poisonDamage + " poison damage! Remaining HP: " + character.hp);
      }
      if (character.poisonTurns == 0) {
        character.statusEffects.remove(StatusEffect.POISON); // Remove Poison after duration
        System.out.println(character.name + " is no longer POISONED.");
      }
    }

    if (character.statusEffects != null && character.statusEffects.contains(StatusEffect.BLEED)) {
      // Bleed lasts for 3 turns
      if (character.bleedTurns > 0) {
        int bleedDamage = 15; // Set bleed damage per turn
        character.hp -= bleedDamage;
        character.bleedTurns--;
        System.out.println(character.name + " takes " + bleedDamage + " bleed damage! Remaining HP: " + character.hp);
      }
      if (character.bleedTurns == 0) {
        character.statusEffects.remove(StatusEffect.BLEED); // Remove Bleed after duration
        System.out.println(character.name + " is no longer BLEEDING.");
      }
    }

    if (character.statusEffects != null && character.statusEffects.contains(StatusEffect.REGENERATION)) {
      int regenAmount = 10;
      character.hp += regenAmount;
      System.out.println(character.name + " regenerates " + regenAmount + " HP! Current HP: " + character.hp);
      character.statusEffects.remove(StatusEffect.REGENERATION);
    }

  }

  public void applyEffect(Characters.Character current, Characters.Ability ability, Characters.Character target) {
    Random random = new Random();
    boolean stats1;
    if ("magic".equals(ability.element)) {
      stats1 = random.nextInt(100) < 10;
      if (stats1) {
        target.statusEffects.add(StatusEffect.STUN);
      }
      stats1 = random.nextInt(100) < 10;
      if (stats1) {
        target.statusEffects.add(StatusEffect.CONFUSE);
      }
    } else if ("fire".equals(ability.element)) {
      stats1 = random.nextInt(100) < 15;
      if (stats1) {
        target.statusEffects.add(StatusEffect.STUN);
      }
    } else if ("nature".equals(ability.element)) {
      stats1 = random.nextInt(100) < 10;
      if (stats1) {
        current.statusEffects.add(StatusEffect.REGENERATION);
      }
    } else if ("poison".equals(ability.element)) {
      stats1 = random.nextInt(100) < 15;
      if (stats1) {
        target.statusEffects.add(StatusEffect.POISON);
        System.out.println(target.name + " was POISONED");
      }
    } else if ("physical".equals(ability.element)) {
      stats1 = random.nextInt(100) < 15;
      if (stats1) {
        target.statusEffects.add(StatusEffect.BLEED);
        System.out.println(target.name + " started BLEEDING");
      }
    } else if ("explosive".equals(ability.element)) {
      stats1 = random.nextInt(100) < 10;
      if (stats1) {
        target.statusEffects.add(StatusEffect.BLIND);
      }
      stats1 = random.nextInt(100) < 100;
      if (stats1) {
        target.statusEffects.add(StatusEffect.STUN);
      }
    }
  }

  public void shorterHeroAttackMonster(Characters.Character current) {
    Random random = new Random();
    int attackChoice, abilityChoice;
    System.out.println("\n" + current.name + " is taking a turn!");

    for (int i = 0; i < selectedMonsters.size(); i++) {
      System.out.print(i + 1 + ". " + selectedMonsters.get(i).name + " ");
    }
    System.out.println("");

    System.out.print("Choose Monster to attack: ");
    attackChoice = scanner.nextInt();
    while (attackChoice < 1 || attackChoice > selectedMonsters.size()) {
      System.out.print("Wrong data, type again ");
      attackChoice = scanner.nextInt();
    }
    attackChoice--;
    if (current.statusEffects != null && current.statusEffects.contains(StatusEffect.CONFUSE)) {
      current.statusEffects.remove(StatusEffect.CONFUSE);
      System.out.println(current.name + " is CONFUSED, it may attack the wrong enemy");
      attackChoice = random.nextInt(selectedMonsters.size());
    }

    System.out.println("");
    for (int i = 0; i < current.abilities.size(); i++) {
      System.out.println(i + 1 + ".");
      System.out.println("Ability name: " + current.abilities.get(i).name);
      System.out.println("Ability element: " + current.abilities.get(i).element);
      System.out.println("Ability damage: " + current.abilities.get(i).damage);
      System.out.println("");
    }
    System.out.print("Choose hero's ability: ");
    abilityChoice = scanner.nextInt();
    while (abilityChoice < 1 || abilityChoice > current.abilities.size()) {
      System.out.print("Wrong data, type again ");
      abilityChoice = scanner.nextInt();
    }
    abilityChoice--;
    calculations(current, attackChoice, abilityChoice);
  }

  public void shorterMonsterAttackHero(Characters.Character current) {
    Random random = new Random();
    int bestAttackChoice = -1;
    int bestAbilityChoice = -1;
    int highestDamage = -1;

    System.out.println("\n" + current.name + " is taking a turn!");

    // Iterates through each hero to calculate potential damage
    for (int i = 0; i < selectedHeroes.size(); i++) {
      Characters.Character target = selectedHeroes.get(i);

      // Iterates through each ability to find the one with the highest damage
      for (int j = 0; j < current.abilities.size(); j++) {
        Characters.Ability ability = current.abilities.get(j);

        // Resistance and damage calculations
        int resistance = target.resistances.getOrDefault(ability.element, 0);
        int reducedDamage = ability.damage - (ability.damage * resistance / 100);

        // Check for critical hit
        boolean isCritical = random.nextInt(100) < current.criticalDamage;
        if (isCritical) {
          reducedDamage *= 1.5;
          System.out.println("Critical Hit!");
        }

        // Update if this attack does more damage than the current best
        if (reducedDamage > highestDamage) {
          highestDamage = reducedDamage;
          bestAttackChoice = i;
          bestAbilityChoice = j;
        }
      }
    }

    // Perform the attack with the best choice
    Characters.Character target = selectedHeroes.get(bestAttackChoice);
    Characters.Ability bestAbility = current.abilities.get(bestAbilityChoice);
    calculations(current, bestAttackChoice, bestAbilityChoice);

    // System.out.println(current.name + " used " + bestAbility.name + " on " +
    // target.name);
    // System.out.println("Damage dealt: " + highestDamage);
    // System.out.println(target.name + "'s remaining HP: " + target.hp);
  }

  public void endGame() {
    System.out.println("Battle ended! Thank you for playing.");
    System.exit(0); // Ends the game
  }
}
