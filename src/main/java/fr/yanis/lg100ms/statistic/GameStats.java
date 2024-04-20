package fr.yanis.lg100ms.statistic;

import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class GameStats {
    private int playersCount;
    private int timer;
    private ArrayList<IPlayerWW> players;
    private boolean started;
    public GameStats(){
        this.playersCount = 0;
        this.timer = 0;
        this.started = false;
        this.players = new ArrayList<>();
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isStarted() {
        return started;
    }

    public void setPlayersCount(int playersCount){
        this.playersCount = playersCount;
    }

    public int getPlayersCount(){
        return this.playersCount;
    }

    public void setTimer(int timer){
        this.timer = timer;
    }

    public int getTimer(){
        return this.timer;
    }

    public void addTimer(){
        this.timer++;
    }

    public void removeTimer(){
        this.timer--;
    }

    public void reset(){
        this.playersCount = 0;
        this.timer = 0;
        this.players = new ArrayList<>();
    }

    public void removePlayer(){
        this.playersCount--;
    }

    public void removePlayer(int i){
        this.playersCount -= i;
    }

    public void addPlayer(int i){
        this.playersCount += i;
    }

    public void addTimer(int i){
        this.timer += i;
    }

    public void removeTimer(int i){
        this.timer -= i;
    }

    public void addPlayer(IPlayerWW player){
        this.players.add(player);
    }

    public void removePlayer(IPlayerWW player){
        this.players.remove(player);
    }

    public ArrayList<IPlayerWW> getPlayers(){
        return this.players;
    }
}
