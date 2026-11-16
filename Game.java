import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Game extends JPanel {

    static Random rand = new Random();
    static String name;
    static int playerHp = 50, monsterHp, round = 0, points = 0;
    static String message = "";
    static boolean over = false;

    public static void main(String[] args) {
        name = askName();

        Game game = new Game();
        game.setBackground(Color.BLACK);

        JButton attack = new JButton("Attack");
        JButton heal = new JButton("Heal");
        JPanel buttons = new JPanel();
        buttons.add(attack);
        buttons.add(heal);

        JFrame frame = new JFrame("Monster Battle Arena");
        frame.add(game, BorderLayout.CENTER);
        frame.add(buttons, BorderLayout.SOUTH);
        frame.setSize(500, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        nextMonster();
        attack.addActionListener(e -> { playTurn(true); game.repaint(); });
        heal.addActionListener(e -> { playTurn(false); game.repaint(); });
    }

    // Asks for a name and repeats until it is not empty
    static String askName() {
        String n = JOptionPane.showInputDialog("Enter your name, hero:");
        while (n == null || n.trim().isEmpty()) {
            n = JOptionPane.showInputDialog("Name cannot be empty! Enter your name:");
        }
        return n.trim().toUpperCase();
    }

    // Returns a random number from 1 to max
    static int roll(int max) {
        return 1 + rand.nextInt(max);
    }

    static void nextMonster() {
        round++;
        monsterHp = 20 + rand.nextInt(21);
        message = "Round " + round + ": a monster appears!";
    }

    static void playTurn(boolean isAttack) {
        if (over) return;

        if (isAttack) {
            int dmg = roll(15);
            monsterHp = Math.max(0, monsterHp - dmg);
            points += dmg;
            message = "You hit for " + dmg + ".";
        } else {
            playerHp = Math.min(50, playerHp + 10);
            message = "You healed 10 HP.";
        }

        if (monsterHp == 0) {
            if (round == 3) {
                over = true;
                points += playerHp;
                message = "YOU WIN, " + name + "! Final score: " + points;
            } else {
                nextMonster();
            }
        } else {
            int hit = roll(12);
            playerHp = Math.max(0, playerHp - hit);
            message += " Monster hits for " + hit + ".";
        }

        if (playerHp == 0 && monsterHp > 0) {
            over = true;
            message = "YOU LOST, " + name + ". Final score: " + points;
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);

        // Player info
        g.drawString(name + "   HP: " + playerHp + "   Points: " + points, 20, 25);
        g.drawRect(20, 35, 250, 15);
        g.fillRect(20, 35, playerHp * 5, 15);

        // Round circles (filled = reached)
        for (int i = 1; i <= 3; i++) {
            g.drawOval(20 + i * 40, 65, 25, 25);
            if (i <= round) g.fillOval(20 + i * 40, 65, 25, 25);
        }

        // Monster
        g.drawRect(170, 130, 140, 140);
        g.drawOval(200, 160, 25, 25);
        g.drawOval(255, 160, 25, 25);
        g.drawLine(200, 230, 280, 230);
        g.drawString("Monster HP: " + monsterHp, 190, 120);

        // Message
        g.drawString(message, 20, 320);
    }
}