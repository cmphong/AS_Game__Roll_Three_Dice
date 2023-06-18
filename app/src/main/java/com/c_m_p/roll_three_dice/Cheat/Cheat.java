package com.c_m_p.roll_three_dice.Cheat;

import com.c_m_p.roll_three_dice.R;

import java.io.Serializable;
import java.util.UUID;

public class Cheat implements Serializable {
    private String id;
    private int diceLeft;
    private int diceCenter;
    private int diceRight;
    private boolean isStop;
    private boolean isPlay;

    public Cheat() {
        this.id = UUID.randomUUID().toString();
        this.diceLeft = R.drawable.img_7;
        this.diceCenter = R.drawable.img_7;
        this.diceRight = R.drawable.img_7;
        this.isStop = true;
        this.isPlay = false;
    }

    public String getId(){ return this.id; }

    public void setId(String id){ this.id = id; }

    public int getDiceLeft() {
        return diceLeft;
    }

    public void setDiceLeft(int diceLeft) {
        this.diceLeft = diceLeft;
    }

    public int getDiceCenter() {
        return diceCenter;
    }

    public void setDiceCenter(int diceCenter) {
        this.diceCenter = diceCenter;
    }

    public int getDiceRight() {
        return diceRight;
    }

    public void setDiceRight(int diceRight) {
        this.diceRight = diceRight;
    }

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }

}
