import javax.swing.*; 
import java.awt.*;
import java.util.Random;
//Import diffrent tools in which the program can use
//javax.swing allows for buttions and windows (for the input) to show
// java.awt allows for graghics
//java.util.Random allows for random numbers to be generated




public class Game extends JPanel {
//container//
//extends JPanel allows for graghics and not just text//



//Variables//  The Reason I used Static was because they kept giving me an error, because methods are allowed to use these variables
    static Random rand = new Random();      // This line generated random numbers
    static String name;
    static String[] monsters = {"Goo Slime", "Skeleton Knight", "Fire Dragon"};     //An array, with the monster names
    static int hp = 60, points = 0, round = 0, monsterHp = 30;           //Initial variables for player and monster
    static boolean over = false;                                         //The game is not yet over, it will once the player wins or loses
    static String msg1 = "Fight 1, BATTLE THE GOOSLIME AND WIN!!", msg2 = "";






//----------------MAIN method (Starts the program)-----------------
    public static void main(String[] args) {   //Java can find it, run it
        name = askName();
        JOptionPane.showMessageDialog(null, "Welcome to the battle ground, " + name + "!\n"
            + "You are a gladiator.\n"
            + "GOAL: defeat 3 monsters and be crowned as the winner.\n\n"
            + "Attack: 5-12 damage \nDefend: halves the next hit\nHeal: +15 HP");




//----Setting up a new game, makes the background BLACK----//
        Game game = new Game();  
        game.setBackground(Color.BLACK); 


//----Buttons found at the bottom ----//
        JButton attack = new JButton("Attack");
        JButton defend = new JButton("Defend");
        JButton heal = new JButton("Heal");
        JPanel buttons = new JPanel();
//Puts them from left to right//
        buttons.add(attack);
        buttons.add(defend);
        buttons.add(heal);


//---- Window Set Up ---// 
        JFrame frame = new JFrame("Arena");
        frame.add(game, BorderLayout.CENTER);      //Black Screen centered
        frame.add(buttons, BorderLayout.SOUTH);    //The buttons go at the bottom
        frame.setSize(520, 450);                  //W&H
        frame.setVisible(true);                    //Shows the window

//---- When a button is clicked ----//
//Each button sends a number to playTurn (1 = attack, 2 = defend, 3 = heal), then the screen redraws
        attack.addActionListener(e -> { playTurn(1); game.repaint(); });
        defend.addActionListener(e -> { playTurn(2); game.repaint(); });
        heal.addActionListener(e -> { playTurn(3); game.repaint(); });
    }


//----------------askName method--------------
//Uses a while loop until its not null
    static String askName() { 
        String n = JOptionPane.showInputDialog("Enter your gladiator name:");  //JOptionPane.showInputDialog adds the window (if from one of the imports)
        while (n == null || n.trim().isEmpty()) {     
            n = JOptionPane.showInputDialog("Name cannot be empty! ");
        }
        return n.trim().toLowerCase();      //Takes off extra spaces and makes it all LowerCase
    }


//----------------roll method (The dice)-----------------

    static int roll(int min, int max) { 
        return min + rand.nextInt(max - min + 1);
    }


//----------------playTurn method (Runs every time a button is clicked)-----------------
//choice is the number from the button: 1 = attack, 2 = defend, 3 = heal
    static void playTurn(int choice) {
        if (over) return;         //Game is over so the buttons do nothing
        if (choice == 3 && hp == 60) {      //Can't heal if the HP is already full
            msg1 = "You are already at full health!";
            msg2 = "";
            return;
        }

        int dmg = 0;                    //Damage the player does this turn
        boolean defending = false;      //Only true if the player picked defend

//---- Player's move ----//
        if (choice == 1) {
            dmg = roll(5, 12);          //Random damage from 5 to 12
            msg1 = "You slash for " + dmg + " damage.";



        } else if (choice == 2) {
            defending = true;           //The monster's hit gets cut in half later
            msg1 = "You raise your shield.";

        
        } else {
            hp = Math.min(60, hp + 15);     //Stops the HP from going over 60
            msg1 = "You drink a potion and heal 15 HP.";
        }

        monsterHp = Math.max(0, monsterHp - dmg);     //Math.max stops the monster's HP from going under 0
        points += dmg;                                //damage dealt is added to points earned.
        if (monsterHp == 0) {           //Monster is dead, next monster
            nextMonster();
            return;
        }

//---- Monster's turn (gets stronger each round) ----//
        int hit = roll(3, 8 + round * 4);

        if (defending) hit = hit / 2;       //Defending cuts the hit in half
        hp = Math.max(0, hp - hit);         //HP can't go under 0
        msg2 = monsters[round] + " hits you for " + hit + ".";
        if (hp == 0) {             //Player is out of HP so the game ends
            over = true;
            msg1 = "You fall in the sand... GAME OVER, " + name + "! Score: " + points;
        }
    }


//----------------nextMonster method (Next fight or the win)-----------------
    static void nextMonster() {
        String beaten = monsters[round];    //Saves the dead monster's name for the message
        points += 20;                      //Bonus points for beating a monster
        round++;                     //Moves on to the next monster
        if (round == 3) {           //All 3 monsters are beaten so the player wins
            over = true;
            points += hp;            //Bonus points for the HP left over
            msg1 = "The " + beaten + " falls! The crowd roars: " + name + " is FREE!";
            if (hp >= 30) msg2 = "Final score: " + points + "  Rank: LEGEND OF THE ARENA";    //Rank depends on how much HP is left
            else msg2 = "Final score: " + points + "  Rank: Battered Survivor";
        } else {
            monsterHp = 30 + round * 15;  //Each new monster has more HP
            hp = Math.min(60, hp + 10);   //Player heals a little between fights
            msg1 = beaten + " defeated! (+10 HP) Next up: " + monsters[round];
            msg2 = "";
        }
    }


//----------------paintComponent method (Draws everything on the screen)----------------- (OUTSIDE HELP USED FOR THIS)

    public void paintComponent(Graphics g) {
        super.paintComponent(g);        //Clears the old drawing first
        g.setColor(Color.WHITE);        //White pen
        int r = Math.min(round, 2);     

//---- Player ----//
        g.drawString(name + " the Gladiator", 20, 25);
        g.drawString(String.format("HP: %d/60   Score: %d", hp, points), 20, 45);     //%d gets replaced by hp, then points
        g.drawRect(20, 55, 150, 12);                //Health bar outline
        g.fillRect(20, 55, hp * 150 / 60, 12);     
        g.drawOval(60, 150, 30, 30);              //Head
        g.drawLine(75, 180, 75, 250);             //Body
        g.drawLine(75, 195, 110, 215);            //Arm
        g.drawLine(75, 250, 55, 290);             //Left leg
        g.drawLine(75, 250, 95, 290);             //Right leg

//---- Round circles (filled = reached) ----//
        for (int i = 0; i < 3; i++) {                           //For loop, runs 3 times for the 3 fights
            g.drawOval(200 + i * 30, 330, 20, 20);              
            if (i <= round) g.fillOval(200 + i * 30, 330, 20, 20);
        }














//Monster (shape changes each fight)//
        g.drawString("Fight " + (r + 1) + "/3: " + monsters[r] + "   HP: " + monsterHp, 280, 25);
        g.drawRect(280, 55, 150, 12);
        g.fillRect(280, 55, monsterHp * 150 / (30 + r * 15), 12);     //Bar size depends on that monster's starting HP
        if (r == 0) g.drawOval(290, 150, 120, 130);                   //Slime is an oval
        else if (r == 1) g.drawRect(290, 150, 100, 140);              //Skeleton is a rectangle
        else g.drawPolygon(new int[]{290, 350, 410}, new int[]{290, 140, 290}, 3);     //Dragon is a triangle
        g.drawOval(325, 200, 12, 12);       //Eyes
        g.drawOval(355, 200, 12, 12);

//Messages
        g.drawString(msg1, 20, 320);
        g.drawString(msg2, 20, 345);
    }
}