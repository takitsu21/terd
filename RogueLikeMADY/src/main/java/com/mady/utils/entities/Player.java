package com.mady.utils.entities;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.Attribute;
import com.mady.utils.Case;
import com.mady.utils.Map;
import com.mady.utils.Salle;
import com.mady.utils.Util;
import com.mady.utils.entities.factories.items.Chest;
import com.mady.utils.entities.factories.items.Inventory;
import com.mady.utils.entities.factories.items.Item;
import com.mady.utils.entities.factories.monster.Boss;

import java.util.HashMap;
import java.util.List;

public class Player extends AbstractEntities {

    private final Stuff stuff;
    private final Inventory inventory;
    //private double maxHp = getMaxHitPoints();
    //private int HP = getHitPoints();
    private int exp = 0;
    private int expMax = 10;
    private int maxMp = 50;
    private int MP = maxMp;
    private int ATK = 3;
    private int DEF = 1;
    private int AGI = 1;
    private int LUK = 1;

    private int realMaxHp = 100;
    private int realHP = 100;
    private int realExp = 0;
    private int realExpMax = 10;
    private int realMaxMp = 50;
    private int realMP = realMaxMp;
    private int realATK = 3;
    private int realDEF = 1;
    private int realAGI = 1;
    private int realLUK = 1;

    private final int DASH_MP_COST = 5;
    private final HashMap<String, Integer> stats = new HashMap<>() {{
        put("LVL", getLvl());
        put("MAX_HP", getMaxHitPoints());
        put("HP", getHitPoints());
        put("MAX_MP", maxMp);
        put("MP", MP);
        put("ATK", ATK);
        put("DEF", DEF);
        put("AGI", AGI);
        put("LUK", LUK);
    }};

    private int maxExpToWin = 3;
    private int coins = 0;
    private int manaAttack = 2;
//    private List<Double> stats = new ArrayList<>(Arrays.asList(maxMp, maxHp, expMax, HP, MP, ATK, DEF, AGI, LUK));


    public Player(Position pos, int hitPoints, int damages, int movement, String repr, Salle salle) {
        super("@", pos, hitPoints, damages, movement, repr, 3, salle);
        this.stuff = new Stuff();
        this.inventory = new Inventory(this.stuff);
    }


    /**
     * Ramasse un item et le met dans l'inventaire.
     *
     * @param i AbstractStuffItem
     * @return boolean
     */
    public boolean pickItem(AbstractStuffItem i) {
        return inventory.addItem(i);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    /**
     * Ramasse un item sur la case en ouvrant le coffre.
     *
     * @param c Case de la map.
     */
    public void pickItem(Case c) {
        Item chest =c.getItem();
        AbstractStuffItem i = ((Chest) chest).openChest(this);
        i.setPosition(null);
        if (((Chest)chest).getPrice()>this.getCoins()) {
            //c.setItem(null);
            Util.currentAction.append(Ansi.colorize(String.format("Vous n'avez pas assez de MADY coins pour ouvrir le coffre.\n")));
        }else if (pickItem(i)) {
            setCoins(getCoins()-((Chest) chest).getPrice());
            c.setItem(null);
            Util.currentAction.append(Ansi.colorize(String.format("Vous avez récupérer <%s> : %s dans le coffre.\n",
                    i.getName().substring(0, 1).toUpperCase() + i.getName().substring(1), i), Attribute.MAGENTA_TEXT()));
        }

    }

    /**
     * Enlève les stats au joueur par rapport à l'item.
     *
     * @param item item qui va modiifer les statistiques du joueur
     */
    private void removeStats(AbstractStuffItem item) {
        if (item == null) {
            return; // Si aucun item n'est encore équipé
        }
        setLUK(getLUK() - item.getLUK());
        setAGI(getAGI() - item.getAGI());
        setDEF(getDEF() - item.getDEF());
        setMaxMp(getMaxMp() - item.getMP());
        setMP(getMP() - item.getMP());
        setMaxHitPoints((int) (getMaxHitPoints() - item.getHP()));
        setHitPoints((int) (getHitPoints() - item.getHP()));
        setATK(getATK() - item.getATK());
    }


    public int getDASH_MP_COST() {
        return DASH_MP_COST;
    }

    /**
     * Ajoute les stats au joueur par rapport à l'item.
     *
     * @param item item qui a va modifier les statistiques du joueur
     */
    private void addStats(AbstractStuffItem item) {
        setLUK(getLUK() + item.getLUK());
        setAGI(getAGI() + item.getAGI());
        setDEF(getDEF() + item.getDEF());
        setMaxMp(getMaxMp() + item.getMP());
        setMP(getMP() + item.getMP());
        setMaxHitPoints((int) (getMaxHitPoints() + item.getHP()));
        setHitPoints((int) (getHitPoints() + item.getHP()));
        setATK(getATK() + item.getATK());
    }

    /**
     * Equipe un item.
     *
     * @param item item a équipé.
     * @return true si l'item a bien été équipé.
     */
    public boolean setEquipment(AbstractStuffItem item) {
        switch (item.getName()) {
            case "helmet":
                removeStats(stuff.getHelmet());
                stuff.setHelmet(item);
                break;
            case "weapon":
                removeStats(stuff.getWeapon());
                stuff.setWeapon(item);
                break;
            case "shoes":
                removeStats(stuff.getShoes());
                stuff.setShoes(item);
                break;
            case "pant":
                removeStats(stuff.getPant());
                stuff.setPant(item);
                break;
            case "chest":
                removeStats(stuff.getChest());
                stuff.setChest(item);
                break;
            case "amulet":
                removeStats(stuff.getAmulet());
                stuff.setAmulet(item);
                break;
            case "gauntlet":
                removeStats(stuff.getGauntlet());
                stuff.setGauntlet(item);
                break;
            default:
                return false;
        }
        addStats(item);
        return true;
    }

    /**
     * @param idx index de l'item a équipé.
     */
    public void equipItem(int idx) {
        AbstractStuffItem item = (AbstractStuffItem) inventory.getInventory().get(idx);
        if (setEquipment(item)) {
            inventory.getInventory().remove(idx);
        }
    }

    public void useItem(Case c) {
        Item i = c.getItem();

        c.setItem(null);
        //if (i.isDrinkable()) {
            i.act(this);
        //} else if (i.isPickable()) {
            // pickItem(i);
        //}
    }


    @Override
    public void setLvl(int lvl) {
        super.setLvl(lvl);
        stats.put("LVL", lvl);
    }


    public int getMaxMp() {
        return maxMp;
    }

    public void setMaxMp(int maxMp) {
        this.maxMp = maxMp;
        stats.put("MAX_MP", maxMp);
        stats.put("MP", maxMp);
    }


    @Override
    public void setMaxHitPoints(int maxHp) {
        super.setMaxHitPoints(maxHp);
        stats.put("MAX_HP", maxHp);
    }

    @Override
    public void setHitPoints(int Hp) {
        super.setHitPoints(Hp);
        stats.put("HP", Hp);
    }


    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
        stats.put("EXP", this.exp);
    }

    public int getExpMax() {
        return expMax;
    }

    public void setExpMax(int expMax) {
        this.expMax = expMax;
        stats.put("EXP_MAX", expMax);
    }


    public int getHP() {
        return getHitPoints();
    }

    public void setHP(int HP) {
        setHitPoints(HP);
        stats.put("HP", HP);
    }


    public int getMP() {
        return MP;
    }

    public boolean setMP(int MP) {
        if (MP < 0) {
            return false;
        }

        if (MP >= getMaxMp()) {
            MP = getMaxMp();
        }

        this.MP = MP;
        stats.put("MP", MP);
        return true;
    }

    public int getATK() {
        return ATK;
    }

    public void setATK(int ATK) {
        this.ATK = ATK;
        stats.put("ATK", ATK);
    }

    public int getDEF() {
        return DEF;
    }

    public void setDEF(int DEF) {
        this.DEF = DEF;
        stats.put("DEF", DEF);
    }

    public int getAGI() {
        return AGI;
    }

    public void setAGI(int AGI) {
        this.AGI = AGI;
        stats.put("AGI", AGI);
    }

    public int getLUK() {
        return LUK;
    }

    public void setLUK(int LUK) {
        this.LUK = LUK;
        stats.put("LUK", LUK);
    }

    public HashMap<String, Integer> getStats() {
        return stats;
    }

    public Stuff getStuff() {
        return stuff;
    }

    public int getMaxExpToWin() {
        return maxExpToWin;
    }

    public void setMaxExpToWin(int maxExpToWin) {
        this.maxExpToWin = maxExpToWin;
    }

    public void updateStats() {
        setExp(0);
        setLvl(getLvl() + 1);
        setRealMaxHp((int) (getRealMaxHp() * getMultiplicateur()));
        setRealHP(getRealMaxHp());
        setRealMaxMp((int) (getRealMaxMp() * getMultiplicateur()));
        setRealMP(getRealMaxMp());
        setRealATK((int) (getRealATK() * getMultiplicateur()));
        setDamages((int) (getDamages() + getRealATK()));
        setRealDEF((int) (getRealDEF() * getMultiplicateur()));
        setRealAGI((int) (getRealAGI() * getMultiplicateur()));
        setRealLUK((int) (getRealLUK() * getMultiplicateur()));
        setRealExpMax((int) (getRealExpMax() * getMultiplicateur() + getRealExpMax()));
        setMaxExpToWin((int) (getMaxExpToWin() * getMultiplicateur()));
        manaAttack *= getMultiplicateur();
        maxExpToWin*=getMultiplicateur();

    }

    public boolean isDead(Map map) {
        return (getHitPoints() <= 0);
    }

    /**
     * @param monster monstre a attaquer
     * @param map     map sur laquelle ce trouve le joueur
     */

    public void closeAttack(Entities monster, Map map) {
        if (setMP(getMP() - manaAttack)) {
            if (monster == null) {
                Util.currentAction.append("Aucune cible atteinte...\n");
            } else {
                attackMonster(monster, map);
            }
        } else {
            Util.currentAction.append("Pas assez de mana...\n");
        }
    }

    /**
     * @param monsters liste des monstres a attaquer
     * @param map      map sur laquelle ce trouve le joueur
     */

    public void zoneAttack(List<Entities> monsters, Map map) {
        if (setMP(getMP() - manaAttack * 4)) {
            if (monsters.isEmpty()) {
                Util.currentAction.append("Aucune cible atteinte...\n");
            } else {
                for (Entities monster : monsters) {
                    attackMonster(monster, map);
                }
            }
        } else {
            Util.currentAction.append("Pas assez de mana...\n");
        }
    }

    /**
     * @param monster monstre a attaquer
     * @param map     map sur laquelle ce trouve le joueur
     *                the attack procedure. attack first and then looks if the monster's dead
     */

    private void attackMonster(Entities monster, Map map) {
        monster.takeDamages(getDamages());
        Util.currentAction.append(Ansi.colorize(String.format("Vous attaquez %s<%d/%d HP> et lui infligez %d points de dégâts.\n",
                monster.getName(), monster.getHitPoints(), monster.getMaxHitPoints(), getDamages()), Attribute.BLUE_TEXT()));

        if (monster.isDead(map)) {
            Util.currentAction.append(Ansi.colorize(String.format("Vous avez tué %s.\n", monster.getName()),
                    Attribute.RED_TEXT()));
            winExp(monster);
            map.getEntities().remove(monster);
        }
    }

    private int randomExp(Entities monster) {
        return monster instanceof Boss ?
                (int) ((Math.random() * maxExpToWin) + 1)*2 :
                (int) ((Math.random() * maxExpToWin) + 1);
    }


    private void winExp(Entities monster) {
        setExp(exp + randomExp(monster));
        if (exp >= expMax) {
            updateStats();
            Util.currentAction.append(Ansi.colorize(String.format("Vous avez atteint le niveau %d, félicitation!\n",
                    getLvl()), Attribute.YELLOW_TEXT()));
            System.out.println(stats.toString());
        }
    }

    public int getRealMaxHp() {
        return realMaxHp;
    }

    public void setRealMaxHp(int realMaxHp) {
        setMaxHitPoints(getMaxHitPoints()-getRealMaxHp()+realMaxHp);
        this.realMaxHp = realMaxHp;
    }

    public int getRealHP() {
        return realHP;
    }

    public void setRealHP(int realHP) {
        setHitPoints(getHitPoints()-getRealHP()+realHP);
        this.realHP = realHP;
    }

    public int getRealExp() {
        return realExp;
    }

    public void setRealExp(int realExp) {
        setExp(getExp()-getRealExp()+realExp);
        this.realExp = realExp;
    }

    public int getRealExpMax() {
        return realExpMax;
    }

    public void setRealExpMax(int realExpMax) {
        setExpMax(getExpMax()-getRealExpMax()+realExpMax);
        this.realExpMax = realExpMax;
    }

    public int getRealMaxMp() {
        return realMaxMp;
    }

    public void setRealMaxMp(int realMaxMp) {
        System.out.println(getMaxMp());
        System.out.println(getRealMaxMp());
        System.out.println(realMaxMp);
        setMaxMp(getMaxMp()-getRealMaxMp()+realMaxMp);
        this.realMaxMp = realMaxMp;
    }

    public int getRealMP() {
        return realMP;
    }

    public void setRealMP(int realMP) {
        setMP(getMP()-getRealMP()+realMP);
        this.realMP = realMP;
    }

    public int getRealATK() {
        return realATK;
    }

    public void setRealATK(int realATK) {
        setATK(getATK()-getRealATK()+realATK);
        this.realATK = realATK;
    }

    public int getRealDEF() {
        return realDEF;
    }

    public void setRealDEF(int realDEF) {
        setDEF(getDEF()-getRealDEF()+realDEF);
        this.realDEF = realDEF;
    }

    public int getRealAGI() {
        return realAGI;
    }

    public void setRealAGI(int realAGI) {
        setAGI(getAGI()-getRealAGI()+realAGI);
        this.realAGI = realAGI;
    }

    public int getRealLUK() {
        return realLUK;
    }

    public void setRealLUK(int realLUK) {
        setLUK(getLUK()-getRealLUK()+realLUK);
        this.realLUK = realLUK;
    }

    public int getManaAttack() {
        return manaAttack;
    }
}
