package me.colinhowes.rollinitiative.data;

/**
 * Created by colin on 5/1/17.
 */

public class CharacterType {
    String name;
    String colour;
    int hpCurrent;
    int hpMax;
    int initBonus;
    int init;
    int inCombat;

    public CharacterType(String name, String colour,
                     int hpCurrent, int hpMax, int initBonus, int init, int inCombat) {
        this.name = name;
        this.colour = colour;
        this.hpCurrent = hpCurrent;
        this.hpMax = hpMax;
        this.initBonus = initBonus;
        this.init = init;
        this.inCombat = inCombat;
    }
}

