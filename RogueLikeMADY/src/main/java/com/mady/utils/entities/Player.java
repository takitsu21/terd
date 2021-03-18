package com.mady.utils.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Player extends AbstractEntities{

    private static int lvl;
    private double exp;
    private static double expMax;
    private static double HP;
    private static double MP;
    private static double ATK;
    private static double DEF;
    private static double AGI;
    private static double LUK;
    private List<Double> stats = new ArrayList<>(Arrays.asList(expMax, HP, MP, ATK, DEF, AGI, LUK));

    public Player(Position pos, int hitPoints, int damages, double movement, String repr) {
        super(pos, hitPoints, damages, movement, repr);
        this.lvl = 1;
        this.HP = 10;
        this.MP = 5;
        this.ATK = 3;
        this.DEF = 1;
        this.AGI = 1;
        this.LUK = 1;

    }


    @Override
    public int getMaxDammages() {
        return 0;
    }

    @Override
    public void setMaxDammages(int maxDammages) {

    }

    public double getExp() {
        return exp;
    }

    public void setExp(double exp) {
        this.exp = exp;
    }

    public static int getLvl() {
        return lvl;
    }

    public static void setLvl(int lvl) {
        Player.lvl = lvl;
    }

    public static double getExpMax() {
        return expMax;
    }

    public static void setExpMax(double expMax) {
        Player.expMax = expMax;
    }

    public static double getHP() {
        return HP;
    }

    public static void setHP(double HP) {
        Player.HP = HP;
    }

    public static double getMP() {
        return MP;
    }

    public static void setMP(double MP) {
        Player.MP = MP;
    }

    public static double getATK() {
        return ATK;
    }

    public static void setATK(double ATK) {
        Player.ATK = ATK;
    }

    public static double getDEF() {
        return DEF;
    }

    public static void setDEF(double DEF) {
        Player.DEF = DEF;
    }

    public static double getAGI() {
        return AGI;
    }

    public static void setAGI(double AGI) {
        Player.AGI = AGI;
    }

    public static double getLUK() {
        return LUK;
    }

    public static void setLUK(double LUK) {
        Player.LUK = LUK;
    }

    public List<Double> getStats() {
        return stats;
    }

    public void setStats(List<Double> stats) {
        this.stats = stats;
    }

    public void updateStats() {
        lvl += 1;
        for (double stat : stats) {
            stat = stat * 1.36;
        }
    }

    public boolean isLevelUp(int expGain) {
        double newExp = (exp + expGain) % expMax;
        if (newExp < exp) {
            exp = newExp;
            return true;
        }
        return false;
    }

    public boolean isDead() {
        return (getHP() <= 0);
    }

}
