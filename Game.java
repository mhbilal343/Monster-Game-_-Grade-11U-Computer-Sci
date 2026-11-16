import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Game extends JPanel {

    static Random rand = new Random();
    static String name;
    static String[] monsters = {"Goo Slime", "Skeleton Knight", "Fire Dragon"};
    static int hp = 60, points = 0, round = 0, monsterHp = 30;
    static boolean over = false;
    static String msg1 = "Fight 1! A Goo Slime oozes into the arena!", msg2 = "";

    public static void main(String[] args) {
        name = askName();
        JOptionPane.showMessageDialog(null, "Welcome to the CRIMSON ARENA, " + name + "!\n"
            + "You are a gladiator owned by the cruel Arena Lord.\n"
            + "GOAL: defeat his 3 monsters to win your freedom.\n\n"
            + "Attack: 5-12 damage (may crit!)\nDefend: halves the next hit\nHeal: +15 HP");

        Game game = new Game();
        game.setBackground(Color.BLACK);

        JButton attack = new JButton("Attack");
        JButton defend = new JButton("Defend");
        JButton heal = new JButton("Heal");
        JPanel buttons = new JPanel();
        buttons.add(attack);
        buttons.add(defend);
        buttons.add(heal);

        JFrame frame = new JFrame("Crimson Arena");
        frame.add(game, BorderLayout.CENTER);
        frame.add(buttons, BorderLayout.SOUTH);
        frame.setSize(520, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        attack.addActionListener(e -> { playTurn(1); game.repaint(); });
        defend.addActionListener(e -> { playTurn(2); game.repaint(); });
        heal.addActionListener(e -> { playTurn(3); game.repaint(); });
    }

    // Asks for a name and repeats until it is not empty
    static String askName() {
        String n = JOptionPane.showInputDialog("Enter your gladiator name:");
        while (n == null || n.trim().isEmpty()) {
            n = JOptionPane.showInputDialog("Name cannot be empty! Enter your name:");
        }
        return n.trim().toUpperCase();
    }

    // Returns a random number from min to max
    static int roll(int min, int max) {
        return min + rand.nextInt(max - min + 1);
    }

    // The player picks a move: 1 = attack, 2 = defend, 3 = heal
    static void playTurn(int choice) {
        if (over) return;
        if (choice == 3 && hp == 60) {
            msg1 = "You are already at full health!";
            msg2 = "";
            return;
        }

        int dmg = 0;
        boolean defending = false;
        if (choice == 1) {
            dmg = roll(5, 12);
            msg1 = "You slash for " + dmg + " damage.";
            if (rand.nextInt(100) < 20) {
                dmg = dmg * 2;
                msg1 = "CRITICAL HIT! " + dmg + " damage!";
            }
        } else if (choice == 2) {
            defending = true;
            msg1 = "You raise your shield.";
        } else {
            hp = Math.min(60, hp + 15);
            msg1 = "You drink a potion and heal 15 HP.";
        }

        monsterHp = Math.max(0, monsterHp - dmg);
        points += dmg;
        if (monsterHp == 0) {
            nextMonster();
            return;
        }

        // Monster fights back (stronger each round)
        int hit = roll(3, 8 + round * 4);
        if (defending) hit = hit / 2;
        hp = Math.max(0, hp - hit);
        msg2 = monsters[round] + " hits you for " + hit + ".";
        if (hp == 0) {
            over = true;
            msg1 = "You fall in the sand... GAME OVER, " + name + "! Score: " + points;
        }
    }

    static void nextMonster() {
        String beaten = monsters[round];
        points += 20;
        round++;
        if (round == 3) {
            over = true;
            points += hp;
            msg1 = "The " + beaten + " falls! The crowd roars: " + name + " is FREE!";
            if (hp >= 30) msg2 = "Final score: " + points + "  Rank: LEGEND OF THE ARENA";
            else msg2 = "Final score: " + points + "  Rank: Battered Survivor";
        } else {
            monsterHp = 30 + round * 15;
            hp = Math.min(60, hp + 10);
            msg1 = beaten + " defeated! (+10 HP) Next up: " + monsters[round];
            msg2 = "";
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        int r = Math.min(round, 2);

        // Player
        g.drawString(name + " the Gladiator", 20, 25);
        g.drawString(String.format("HP: %d/60   Score: %d", hp, points), 20, 45);
        g.drawRect(20, 55, 150, 12);
        g.fillRect(20, 55, hp * 150 / 60, 12);
        g.drawOval(60, 150, 30, 30);
        g.drawLine(75, 180, 75, 250);
        g.drawLine(75, 195, 110, 215);
        g.drawLine(75, 250, 55, 290);
        g.drawLine(75, 250, 95, 290);

        // Round circles (filled = reached)
        for (int i = 0; i < 3; i++) {
            g.drawOval(200 + i * 30, 330, 20, 20);
            if (i <= round) g.fillOval(200 + i * 30, 330, 20, 20);
        }

        // Monster (shape changes each fight)
        g.drawString("Fight " + (r + 1) + "/3: " + monsters[r] + "   HP: " + monsterHp, 280, 25);
        g.drawRect(280, 55, 150, 12);
        g.fillRect(280, 55, monsterHp * 150 / (30 + r * 15), 12);
        if (r == 0) g.drawOval(290, 150, 120, 130);
        else if (r == 1) g.drawRect(290, 150, 100, 140);
        else g.drawPolygon(new int[]{290, 350, 410}, new int[]{290, 140, 290}, 3);
        g.drawOval(325, 200, 12, 12);
        g.drawOval(355, 200, 12, 12);

        // Messages
        g.drawString(msg1, 20, 320);
        g.drawString(msg2, 20, 345);
    }
}