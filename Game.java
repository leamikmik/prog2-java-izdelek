package game;

import javax.swing.JFrame;
import javax.swing.Box;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;
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
        else if(e.getKeyCode()== KeyEvent.VK_SPACE && !Gamestate.inCombat && Gamestate.playerHealth > 0)
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


        Tear.sprite = ImageIO.read(new File("assets", "tear.png"));
        EnemyPanel.sprites[0] = ImageIO.read(new File("assets", "enemy.png"));
        EnemyPanel.sprites[2] = ImageIO.read(new File("assets", "enemy_idle.png"));
        EnemyPanel.sprites[1] = ImageIO.read(new File("assets", "enemy_dead.png"));

        Menu.sprites[0][0] = ImageIO.read(new File("assets", "atk.png")); 
        Menu.sprites[0][1] = ImageIO.read(new File("assets", "atk_idle.png"));
        Menu.sprites[0][2] = ImageIO.read(new File("assets", "atk_select.png"));
        Menu.sprites[1][0] = ImageIO.read(new File("assets", "use.png")); 
        Menu.sprites[1][1] = ImageIO.read(new File("assets", "use_idle.png"));
        Menu.sprites[1][2] = ImageIO.read(new File("assets", "use_select.png"));
        Menu.sprites[2][0] = ImageIO.read(new File("assets", "run.png")); 
        Menu.sprites[2][1] = ImageIO.read(new File("assets", "run_idle.png"));
        Menu.sprites[2][2] = ImageIO.read(new File("assets", "run_select.png"));

        Menu.item_sprites[0][0] = ImageIO.read(new File("assets", "atk_up.png")); 
        Menu.item_sprites[0][2] = ImageIO.read(new File("assets", "atk_up_select.png")); 
        Menu.item_sprites[1][0] = ImageIO.read(new File("assets", "def_up.png")); 
        Menu.item_sprites[1][2] = ImageIO.read(new File("assets", "def_up_select.png")); 
        Menu.item_sprites[2][0] = ImageIO.read(new File("assets", "hp_up.png")); 
        Menu.item_sprites[2][2] = ImageIO.read(new File("assets", "hp_up_select.png")); 

        Player.sprite = ImageIO.read(new File("assets", "player.png"));
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

    static BufferedImage[] sprites = new BufferedImage[3];
    
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
            g.drawImage(sprites[0], this.getWidth()/2 - sprites[0].getWidth()/2, healthBarHeight + 40, null);
        else if(Gamestate.enemyHealth <= 0) 
            g.drawImage(sprites[1], this.getWidth()/2 - sprites[0].getWidth()/2, healthBarHeight + 40, null);
        else 
            g.drawImage(sprites[2], this.getWidth()/2 - sprites[0].getWidth()/2, healthBarHeight + 40, null);

    }

}

class Arena extends JPanel implements PanelSizes {

    static Player player = new Player();
    private int moveAmt = 5;
    static Enemy enemy = new Enemy();
    static JLabel text = new JLabel(){
        @Override
        public void paintComponent(Graphics g){
            if(!Gamestate.inCombat) super.paintComponent(g);
        }
    };

    public Arena(){
        super();
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(arenaWidth, 0));
        setBorder(new EmptyBorder(arenaBorder, arenaBorder, arenaBorder, arenaBorder));
        player.setX(arenaWidth/2 - player.radius/2);
        player.setY(arenaHeight/2 - player.radius/2);

        text.setForeground(Color.WHITE);
        text.setFont(text.getFont().deriveFont(20f));
        this.add(text);
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
                text.setText("You lost!");

            }
            else if(Gamestate.enemyHealth <= 0){
                text.setText("You won!");

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

    static BufferedImage[][] sprites = new BufferedImage[3][3];

    static BufferedImage[][] item_sprites = new BufferedImage[3][3];



    public Menu(){
        super();
        setBackground(Color.BLACK);
    }

    static public void select(){
        switch (selected){
            case 0:
                if (Gamestate.selecting_item) {
                    Gamestate.atk_dmg = 3;
                    Gamestate.selecting_item = false;}
                else {
                    Gamestate.enemyHealth -= Gamestate.atk_dmg;
                    Gamestate.atk_dmg = 1;
                }
                Gamestate.inCombat = true;
                break;
            case 1:
                if (Gamestate.selecting_item) {
                    Gamestate.shield = 5;
                    Gamestate.selecting_item = false;
                    Gamestate.inCombat = true;
                }
                else {Gamestate.selecting_item = true;}
                break;
            case 2:
                if (Gamestate.selecting_item) {
                    if (Gamestate.playerHealth + 2 > 10) {Gamestate.playerHealth = 10;}
                    else {Gamestate.playerHealth += 2;}
                    Gamestate.selecting_item = false;
                }
                else {
                    if(Math.random() <= 0.1 * (Gamestate.enemyMaxHealth - Gamestate.enemyHealth))
                        Gamestate.enemyHealth = 0;
                    }
                Gamestate.inCombat = true;
                break;
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

        int phase = 0;
        int x = 0;
        int y = 0;
        for(int i = 0; i < 3; i++){
            if (Gamestate.inCombat || Gamestate.enemyHealth <= 0 || Gamestate.playerHealth <= 0) phase = 1;
            else if (selected == i && !Gamestate.inCombat) phase = 2;
            else phase = 0;
            x = getWidth()/2 + (int)(buttonWidth*(i - 3/2.0)) + buttonMargin*(i-1);
            y = healthBarHeight + 20;
            if (!Gamestate.selecting_item) {
                g.drawImage(sprites[i][phase], x, y, x+buttonWidth, y+buttonHeight, 0, 0, sprites[i][phase].getWidth(), sprites[i][phase].getHeight(), null);
            }
            else {
                g.drawImage(item_sprites[i][phase], x, y, x+buttonWidth, y+buttonHeight, 0, 0, item_sprites[i][phase].getWidth(), item_sprites[i][phase].getHeight(), null);
            }
        }
    }

}

class Player extends JComponent{
    private int x;
    private int y;
    static int radius = 20;
    static BufferedImage sprite;

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (Gamestate.inCombat){
            g.drawImage(sprite, x, y, x+radius, y+radius, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
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
        if (Gamestate.shield <= 0) {Gamestate.playerHealth -= 1;}
        else {Gamestate.shield -= 1;}
    }
}