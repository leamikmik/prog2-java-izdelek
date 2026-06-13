package game;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import javax.swing.JComponent;
import java.awt.image.BufferedImage;


public class Enemy{

    AttackPattern curPattern;

    public Enemy(){ curPattern = new TearPattern(); }

    public void update(Player player){
        if (Gamestate.inCombat){
            curPattern.checkHit(player);
            curPattern.update();
            if (curPattern.ttl <= 0){
                Gamestate.inCombat = false;
                curPattern = new TearPattern();
            }
        }
    }

    public void paint(Graphics g){
        if (Gamestate.inCombat){
            curPattern.paint(g);
        }
    }

}

class TearPattern extends AttackPattern<Tear> implements PanelSizes{

    final int tearWidth = 20;
    final int tearHeight = 30;
    final int tearAmount = 20;

    public TearPattern(){
        super();

        ttl = 40 * 10;

        for (int i = 0; i < tearAmount; i++){
            newTear();
        }
    }

    public void newTear(){
        Random r = new Random();
        int x = 0;
        int y = 0;
        boolean clear = false;
        while(!clear){
            clear = true;
            x = arenaBorder + r.nextInt(arenaWidth - 2*arenaBorder - tearWidth);
            y = -(arenaBorder + r.nextInt(arenaHeight));
            for (RectHitbox cur : projectiles){
                clear = !cur.intersects(x-5, y-5, tearHeight+10, tearWidth+10);
                if(!clear) break;
            }
        }
        projectiles.add(new Tear(x, y, tearHeight, tearWidth));
    }

    public void update(){
        super.update();

        ListIterator<Tear> iter = projectiles.listIterator();
        while(iter.hasNext()){
            Tear cur = iter.next();
            if(cur.getY() > arenaHeight){
                iter.remove();
                continue;
            }
            else if(cur.vy < 4 && cur.getY() > -arenaBorder){
                cur.vy += 0.2;
            }
            cur.update();
            iter.set(cur);
        }
        for (int i = 0; i < tearAmount-projectiles.size(); i++){
            newTear();
        }
    }
}

class AttackPattern<T extends RectHitbox> {

    protected List<T> projectiles;

    int ttl; // 40 = 1 sec

    public AttackPattern(){
        projectiles = new ArrayList<T>();
    }

    public void checkHit(Player player){
        ListIterator<T> iter = this.projectiles.listIterator();
        while(iter.hasNext()){
            T cur = iter.next();
            if (cur.intersects(player)){
                player.takeDamage();
                iter.remove();
            }
        }
    }

    public void paint(Graphics g){
        for (T cur : projectiles){
            cur.paintComponent(g);
        }
    }

    public void update(){
        ttl--;
    }

}

class Tear extends RectHitbox{
    float vy = 1;
    static BufferedImage sprite;

    public Tear(int x, int y, int height, int width){
        super(x, y, height, width);
    }

    public void update(){
        this.y += (int)vy;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(sprite, x, y, x+width, y+height, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
        //g.setColor(Color.WHITE);
        //g.fillOval(x-1, y-1, 2, 2);
    }    
}

abstract class RectHitbox extends JComponent{
    int x;
    int y;
    int height;
    int width;

    public RectHitbox(int x, int y, int height, int width){
        this.x = x + width/2;
        this.y = y + height/2;
        this.height = height;
        this.width = width;
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    public abstract void update();

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
    };

    public <T extends RectHitbox> boolean intersects(T other){
        return !(other.getX() > this.getX() + this.width || this.getX() > other.getX() + other.width || other.getY() > this.getY() + this.height || this.getY() > other.getY() + other.height);
    }

    public boolean intersects(Player player){
        return !(player.getX() > this.getX() + this.width || this.getX() > player.getX() + player.radius || player.getY() > this.getY() + this.height || this.getY() > player.getY() + player.radius);
    }

    public boolean intersects(int x, int y, int width, int height){
        return !(x > this.getX() + this.width || this.getX() > x + width || y > this.getY() + this.height || this.getY() > y + height);
    }
}