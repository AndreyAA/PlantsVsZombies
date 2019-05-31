import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Armin on 6/25/2016.
 */
public class GameWindow extends JFrame {

    //PlantType activePlantingBrush = PlantType.None;

    public GameWindow() {
        setSize(1012, 785);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel sun = new JLabel("SUN");
        sun.setLocation(37, 80);
        sun.setSize(60, 20);

        GamePanel gp = new GamePanel(sun);
        gp.setLocation(0, 0);
        getLayeredPane().add(gp, new Integer(0));

        addCard(gp, GameData.CARD_SUN_FLOWER);
        addCard(gp, GameData.CARD_PEAR_SHOOTER);
        addCard(gp, GameData.CARD_FREEZE_SHOOTER);

        getLayeredPane().add(sun, new Integer(2));

        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addCard(GamePanel gamePanel, GameData.Card card) {
        PlantCard plantCard = new PlantCard(new ImageIcon(this.getClass().getResource(card.getImage())).getImage());
        plantCard.setLocation(card.getX(), card.getY());
        plantCard.setAction((ActionEvent e) -> {
            gamePanel.setActivePlantingBrush(card.getType());
        });
        getLayeredPane().add(plantCard, new Integer(3));
    }

    public GameWindow(boolean b) {
        Menu menu = new Menu();
        menu.setLocation(0, 0);
        setSize(1012, 785);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getLayeredPane().add(menu, new Integer(0));
        menu.repaint();
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    static GameWindow gw;

    public static void begin() {
        gw.dispose();
        gw = new GameWindow();
    }

    public static void main(String[] args) {
        gw = new GameWindow(true);
    }

}
