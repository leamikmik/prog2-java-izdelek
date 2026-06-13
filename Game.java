package game;

import javax.swing.JFrame;
import javax.swing.Box;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;


interface PanelSizes {

    final int appWidth = 1200;
    final int appHeight = 900;

    final int arenaWidth = 350;
    final int arenaHeight = 250;
    final int arenaBorder = 10;

    final int healthBarWidth = 100;
    final int healthBarHeight = 30;

}

public class Game extends JFrame implements KeyListener , PanelSizes{

    static Arena arena = new Arena();
    static EnemyPanel ep = new EnemyPanel();
    static boolean[] moving = {false, false, false, false};

    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode()== KeyEvent.VK_RIGHT){
            moving[0] = false;
        }
        else if(e.getKeyCode()== KeyEvent.VK_LEFT){
            moving[1] = false;
        }
        else if(e.getKeyCode()== KeyEvent.VK_DOWN)
            moving[2] = false;
        else if(e.getKeyCode()== KeyEvent.VK_UP)
            moving[3] = false;
        else if(e.getKeyCode()== KeyEvent.VK_SPACE && !Gamestate.inCombat)
            Menu.select();
    }

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()== KeyEvent.VK_RIGHT && !moving[0]){
            moving[0] = true;
            if(!Gamestate.inCombat && Menu.selected < 2) Menu.selected += 1;
        }
        else if(e.getKeyCode()== KeyEvent.VK_LEFT && !moving[1]){
            if(!Gamestate.inCombat && Menu.selected > 0) Menu.selected -= 1;
            moving[1] = true;
        }
        else if(e.getKeyCode()== KeyEvent.VK_DOWN && !moving[2])
            moving[2] = true;
        else if(e.getKeyCode()== KeyEvent.VK_UP && !moving[3])
            moving[3] = true;
    }

    public Game() throws IOException{
        super();
        addKeyListener(this);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        setTitle("Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(appWidth, appHeight));
        setResizable(false);
        getContentPane().setBackground(Color.BLACK);

        Box paddingL = new Box(0);
        Box paddingR = new Box(0);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.15;
        c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 3;
        add(paddingL, c);
        c.gridx = 4;
        add(paddingR, c);

        c.insets = new Insets(10, 0, 10, 0);
        c.weighty = 0.55;
        c.weightx = 0.7;
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.BOTH;
        add(ep, c);

        c.weighty = 0.30;
        c.weightx = 0.7;
        c.gridx = 1;
        c.gridy = 2;
        c.gridheight = 1;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.VERTICAL;
        add(arena, c);

        Menu menu = new Menu();
        c.weighty = 0.15;
        c.gridx = 1;
        c.gridy = 3;
        c.gridheight = 1;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.BOTH;
        add(menu, c);


        Tear.sprite = ImageIO.read(new File("tear.png"));
        EnemyPanel.sprite = ImageIO.read(new File("enemy.png"));
        EnemyPanel.sprite_idle = ImageIO.read(new File("enemy_idle.png"));
        EnemyPanel.sprite_dead = ImageIO.read(new File("enemy_dead.png"));
    }

    public static void main(String[] args) throws Exception {
        Game game = new Game();
        game.setVisible(true);
        
        while (true) {
            arena.update(moving);

            game.repaint();
            Thread.sleep(25);
        }
    }
}

class EnemyPanel extends JPanel implements PanelSizes {

    static BufferedImage sprite;
    static BufferedImage sprite_idle;
    static BufferedImage sprite_dead;
    
    EnemyPanel(){
        super();
        setBackground(Color.BLACK);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.RED);
        g2.fillRect(this.getWidth()/2 - healthBarWidth/2, 10, healthBarWidth, healthBarHeight);
        g2.setColor(Color.GREEN);
        g2.fillRect(this.getWidth()/2 - healthBarWidth/2, 10, (int)(healthBarWidth * Gamestate.enemyHealth / Gamestate.enemyMaxHealth), healthBarHeight);

        if(Gamestate.inCombat)
            g.drawImage(sprite, this.getWidth()/2 - sprite.getWidth()/2, healthBarHeight + 40, null);
        else if(Gamestate.enemyHealth <= 0) 
            g.drawImage(sprite_dead, this.getWidth()/2 - sprite.getWidth()/2, healthBarHeight + 40, null);
        else 
            g.drawImage(sprite_idle, this.getWidth()/2 - sprite.getWidth()/2, healthBarHeight + 40, null);

    }

}

class Arena extends JPanel implements PanelSizes {

    static Player player = new Player();
    private int moveAmt = 5;
    static Enemy enemy = new Enemy();
    static JLabel text = new JLabel();

    public Arena(){
        super();
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(arenaWidth, 0));
        player.setX(arenaWidth/2 - player.radius/2);
        player.setY(arenaHeight/2 - player.radius/2);


    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        enemy.paint(g);
        player.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(arenaBorder));
        g2.drawRect(arenaBorder/2, arenaBorder/2, arenaWidth-arenaBorder, arenaHeight-arenaBorder);

        if(!Gamestate.inCombat)
            if(Gamestate.playerHealth <= 0){

            }
            else if(Gamestate.enemyHealth <= 0){

            }

        
    }

    public void update(boolean[] moving){

        if(moving[0] && player.getX() + moveAmt + player.radius < arenaWidth - arenaBorder)
            player.moveX(moveAmt);
        if(moving[1] && player.getX() - moveAmt > arenaBorder)
            player.moveX(-moveAmt);
        if(moving[2] && player.getY() + moveAmt + player.radius < arenaHeight - arenaBorder)
            player.moveY(moveAmt);
        if(moving[3] && player.getY() - moveAmt > arenaBorder)
            player.moveY(-moveAmt);

        if(!Gamestate.inCombat){
            player.setX(arenaWidth/2 - player.radius/2);
            player.setY(arenaHeight/2 - player.radius/2);
        }

        if(Gamestate.playerHealth <= 0 || Gamestate.enemyHealth <= 0) Gamestate.inCombat = false;

        enemy.update(player);
    }

}

class Menu extends JPanel implements PanelSizes {

    static int selected = 0;
    static int buttonWidth = 100;
    static int buttonHeight = 50;
    static int buttonBorder = 6;
    static int buttonMargin = 60;

    public Menu(){
        super();
        setBackground(Color.BLACK);
    }

    static public void select(){
        switch (selected){
            case 0:
                Gamestate.enemyHealth -= 1;
                Gamestate.inCombat = true;
        }
    }

    @Override
    public void paint(Graphics g){
        super.paint(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.RED);
        g2.fillRect(this.getWidth()/2 - healthBarWidth/2, 0, healthBarWidth, healthBarHeight);
        g2.setColor(Color.GREEN);
        g2.fillRect(this.getWidth()/2 - healthBarWidth/2, 0, (int)(healthBarWidth * Gamestate.playerHealth / Gamestate.playerMaxHealth), healthBarHeight);

        g2.setStroke(new BasicStroke(buttonBorder));

        for(int i = 0; i < 3; i++){
            if (selected == i && !Gamestate.inCombat) g2.setColor(Color.YELLOW);
            else if (Gamestate.inCombat) g2.setColor(Color.GRAY);
            else g2.setColor(Color.WHITE);
            g2.drawRect(getWidth()/2 + (int)(buttonWidth*(i - 3/2.0)) + buttonMargin*(i-1), healthBarHeight + 20, buttonWidth - buttonBorder/2, buttonHeight - buttonBorder/2);
        }

    }

}

class Player extends JComponent{
    private int x;
    private int y;
    static int radius = 20;

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (Gamestate.inCombat){
            g.setColor(Color.RED);
            g.fillRect(x, y, radius, radius);
            g.setColor(Color.WHITE);
            g.fillOval(x-1, y-1, 2, 2);
        }
    }

    public Player(){}

    public int getX(){ return this.x; }
    public int getY(){ return this.y; }
    public void setX(int x){ this.x = x; }
    public void setY(int y){ this.y = y; }
    public void moveX(int add){ 
        this.x = this.x + add;
    }
    public void moveY(int add){ 
        this.y = this.y + add;
    }

    public void takeDamage(){
        Gamestate.playerHealth -= 1;
    }
}