package game;

import javax.swing.JFrame;
import javax.swing.Box;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JPanel;
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
import java.util.Random;

public class Game extends JFrame implements KeyListener{

    static Arena arena = new Arena();
    static boolean[] moving = {false, false, false, false};
    static GameState gs = new GameState();

    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode()== KeyEvent.VK_RIGHT)
            moving[0] = false;
        else if(e.getKeyCode()== KeyEvent.VK_LEFT)
            moving[1] = false;
        else if(e.getKeyCode()== KeyEvent.VK_DOWN)
            moving[2] = false;
        else if(e.getKeyCode()== KeyEvent.VK_UP)
            moving[3] = false;
    }

    public void keyTyped(KeyEvent e) {
        System.out.println("keyTyped");
    }

    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()== KeyEvent.VK_RIGHT && !moving[0])
            moving[0] = true;
        else if(e.getKeyCode()== KeyEvent.VK_LEFT && !moving[1])
            moving[1] = true;
        else if(e.getKeyCode()== KeyEvent.VK_DOWN && !moving[2])
            moving[2] = true;
        else if(e.getKeyCode()== KeyEvent.VK_UP && !moving[3])
            moving[3] = true;
    }

    public Game(){
        super();
        addKeyListener(this);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        setTitle("Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1200, 900));
        setResizable(false);
        getContentPane().setBackground(Color.BLACK);

        JPanel ui = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
            }
        };

        Box paddingL = new Box(0);
        Box paddingR = new Box(0);
        c.fill = GridBagConstraints.BOTH;
        //c.anchor = GridBagConstraints.NORTH;
        c.weightx = 0.15;
        c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 3;
        add(paddingL, c);
        c.gridx = 4;
        add(paddingR, c);

        ui.setBackground(Color.BLUE);
        c.insets = new Insets(10, 0, 10, 0);
        c.weighty = 0.55;
        c.weightx = 0.7;
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.BOTH;
        add(ui, c);

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

    }

    public static void main(String[] args) throws Exception {
        Game game = new Game();
        game.setVisible(true);
        int moveAmt = 5;
        while (true) {
            game.repaint();
            Thread.sleep(25);
            if(moving[0] && game.arena.player.getX() + moveAmt < game.arena.getWidth() - (10 + game.arena.player.radius))
                game.arena.player.moveX(moveAmt);
            if(moving[1] && game.arena.player.getX() - moveAmt > 5 + game.arena.player.radius/2)
                game.arena.player.moveX(-moveAmt);
            if(moving[2] && game.arena.player.getY() + moveAmt < game.arena.getHeight() - (15 + game.arena.player.radius))
                game.arena.player.moveY(moveAmt);
            if(moving[3] && game.arena.player.getY() - moveAmt > 5 + game.arena.player.radius/2)
                game.arena.player.moveY(-moveAmt);
        }
    }
}

class Arena extends JPanel {

    static Player player = new Player(100, 100);
    private int width = 350;

    public Arena(){
        super();
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(width, 0));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(10));
        g2.drawRect(5, 5, this.getWidth()-10, this.getHeight()-10);
        player.paintComponent(g);
    }

    public int getWidth(){
        return width;
    }

    public void setWidth(int newD){
        this.width = newD;
        this.setPreferredSize(new Dimension(newD, 0));
    }

}

class Menu extends JPanel {

    public Menu(){
        super();
        setBackground(Color.GREEN);
        //setPreferredSize(new Dimension(350, 300));

    }
}

class Player extends JComponent{
    private int x;
    private int y;
    static int radius = 10;

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.drawRect(x, y, radius, radius);
        g.fillRect(x, y, radius, radius);
        g.setColor(Color.BLACK);
    }

    public Player(int x, int y){
        this.x = x - this.radius/2;
        this.y = y - this.radius/2;
    }

    public int getX(){ return this.x; }
    public int getY(){ return this.y; }
    public void moveX(int add){ 
        this.x = this.x + add;
    }
    public void moveY(int add){ 
        this.y = this.y + add;
    }
}