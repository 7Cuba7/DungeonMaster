package zaidimas;

import com.fasterxml.jackson.annotation.JsonProperty;

import zaidimas.Gameplay.StatusEffect;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Characters {

  public CharacterGroup characterGroup;

  public static class CharacterGroup {
    public LinkedList<Character> heroes;
    public LinkedList<Character> monsters;
  }

  public static class Character {
    public String name;
    public String type;
    public int hp;
    public int speed;
    public Map<String, Integer> resistances;
    public int criticalDamage;
    public List<Ability> abilities;
    public List<StatusEffect> statusEffects;
    public int poisonTurns = 0;
    public int bleedTurns = 0;
    public int stunTurns = 0;

    public Character() {
      this.statusEffects = new LinkedList<>();
    }
  }

  public static class Ability {
    public String name;
    public String element;
    public int damage;

  }
}
