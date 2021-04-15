package com.mady.utils.entities.factories.monster;

import com.mady.utils.Case;
import com.mady.utils.Map;
import com.mady.utils.Salle;
import com.mady.utils.entities.*;


public abstract class AbstractMonster extends AbstractEntities implements Monster {


    public AbstractMonster(Position pos,
                           int lifePoints,
                           int damages,
                           int movement,
                           String repr,
                           int effectiveArea,
                           Salle salle) {
        super(pos, lifePoints, damages, movement, repr, effectiveArea, salle);

    }

    private double getDistance(Player player) {
        Position monsterPos = getPosition();
        Position playerPos = player.getPosition();
        return monsterPos.getDistance(playerPos);
    }

    private void updatePos(Player player) {
        Position monsterPos = getPosition();
        Position playerPos = player.getPosition();
        System.out.printf("player pos : %s, and monster pos : %s\n", playerPos, monsterPos);

        /*setPos(monsterPos.incrementPos(direction(playerPos).pos));
        map.getMap()[getPosition().getX()][getPosition().getY()] = map.getMap()[monsterPos.getX()][monsterPos.getY()];
        map.clearCase(map.getMap()[monsterPos.getX()][monsterPos.getY()]);*/

        Deplacement dep=direction(playerPos);
        System.out.println(dep);
        map.move(this,dep.pos);

        System.out.printf("actual pos : %s\n", getPosition());
    }

    private Deplacement direction(Position playerPos) {
        if (getPosition().getX() < playerPos.getX()) {
           return Deplacement.BAS;
        } else if (getPosition().getY() < playerPos.getY()) {
            return Deplacement.DROITE;
        } else if (getPosition().getX() > playerPos.getX()){
            return Deplacement.HAUT;
        }
        return Deplacement.GAUCHE;
    }

    @Override
    public void act(Player player) {
        System.out.println("acting");
        double distance_from_player = getDistance(player);
        if (distance_from_player > 1) {
            updatePos(player);
            System.out.println("le monstre se rapproche");
        } else {
            attack(player);
            System.out.println("le monstre vous attaque");
        }
    }

    private void attack(Player player) {
        int monsterDamages = getDamages();
        player.takeDamages(monsterDamages);
    }


    private boolean nextTo(Map map) {
        Position monsterPos = this.getPosition();

        /*for (int i = monsterPos.getX() - 1; i <= monsterPos.getX() + 1; i++) {
            for (int j = monsterPos.getY() - 1; j <= monsterPos.getY() + 1; j++) {
                if (i != monsterPos.getX() && j != monsterPos.getY()) {
                    continue;
                } else if (map.isInside(i, j) && map.getMap()[i][j].getEntity() instanceof AbstractMonster) {
                    return true;
                }
            }*/


        if (map.getMap()[monsterPos.getX() - 1][monsterPos.getY()].getEntity() instanceof Player){
            return true;
        }

        if (map.getMap()[monsterPos.getX() + 1][monsterPos.getY()].getEntity() instanceof Player){
            return true;
        }

        if (map.getMap()[monsterPos.getX()][monsterPos.getY()-1].getEntity() instanceof Player){
            return true;
        }

        if (map.getMap()[monsterPos.getX()][monsterPos.getY()+1].getEntity() instanceof Player){
            return true;
        }

        return false;
    }


//    public boolean isAreaClear(Player player) {
//        return (getDistance(player) < effectiveArea);
//    }


}
