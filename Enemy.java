package game;

import java.awt.Color;
import java.awt.Graphics2D;

public class Enemy{

}

class Tear extends RectHitbox{
    private int x;
    private int y;
    int height;
    int width;

    public Tear(int x, int y, int height, int width){
        super(x, y, height, width);
    }
}

class RectHitbox{
    private int x;
    private int y;
    int height;
    int width;

    public RectHitbox(int x, int y, int height, int width){
        this.x = x + width/2;
        this.y = y + height/2;
        this.height = height;
        this.width = width;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }
}