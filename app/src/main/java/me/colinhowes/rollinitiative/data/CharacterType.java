package me.colinhowes.rollinitiative.data;

/**
 * Created by colin on 5/1/17.
 */

public class CharacterType {

    String name;
    String colour;
    int id;
    int hpCurrent;
    int hpMax;
    int initBonus;
    int init;
    int inCombat;
    int turnOrder;

    /*
     * We call this constructor when a character is first created, since we don't know
     * it's ID or turn order until after it has been inserted into the database and
     * it has been added to an encounter (respectively)
     */
    public CharacterType(String name, String colour,
                     int hpCurrent, int hpMax, int initBonus, int init, int inCombat) {
        this.name = name;
        this.colour = colour;
        this.hpCurrent = hpCurrent;
        this.hpMax = hpMax;
        this.initBonus = initBonus;
        this.init = init;
        this.inCombat = inCombat;
        this.turnOrder = -1;
        this.id = -1;
    }

    public CharacterType(int id, String name, String colour, int hpCurrent, int hpMax,
                         int initBonus, int init, int turnOrder, int inCombat) {
        this.name = name;
        this.colour = colour;
        this.hpCurrent = hpCurrent;
        this.hpMax = hpMax;
        this.initBonus = initBonus;
        this.init = init;
        this.inCombat = inCombat;
        this.turnOrder = turnOrder;
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public String getColour() {
        return this.colour;
    }

    public int getId() {
        return this.id;
    }

    public int getHpCurrent() {
        return this.hpCurrent;
    }

    public int getHpTotal() {
        return this.hpMax;
    }

    public int getInitBonus() {
        return this.initBonus;
    }

    public int getInit() {
        return this.init;
    }

    public int getInCombat() {
        return this.inCombat;
    }

    public int getTurnOrder() {
        return this.turnOrder;
    }

    public void setHealth(int health) {
        health = health > 999 ? 999 : health;
        health = health < -99 ? -99 : health;
        this.hpCurrent = health;
    }

    public void setInCombat(int inCombat) {
        this.inCombat = inCombat;
    }

    public void setTurnOrder(int turnOrder) {
        this.turnOrder = turnOrder;
    }
}

