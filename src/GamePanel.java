import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Armin on 6/25/2016.
 */
public class GamePanel extends JLayeredPane implements MouseMotionListener {

    private Image bgImage;
    private Image peashooterImage;
    private Image freezePeashooterImage;
    private Image sunflowerImage;
    private Image peaImage;
    private Image freezePeaImage;

    private Image normalZombieImage;
    private Image coneHeadZombieImage;
    private Collider[] colliders;

    private List<List<Zombie>> laneZombies;
    private List<List<Pea>> lanePeas;
    private List<Sun> activeSuns;

    private Timer redrawTimer;
    private Timer advancerTimer;
    private Timer sunProducer;
    private Timer zombieProducer;
    private JLabel sunScoreboard;

    private PlantType activePlantingBrush = PlantType.None;

    private int mouseX, mouseY;

    private int sunScore;
    private boolean isLogEnabled = false;
    LevelInfoIcon levelInfo = new LevelInfoIcon();
    private final LevelData levelData;
    int progress = 0;
    int zombiesSpawned = 0;

    public GamePanel(JLabel sunScoreboard, LevelData levelData) {
        this.levelData = levelData;
        setSize(1000, 752);
        setLayout(null);
        addMouseMotionListener(this);
        this.sunScoreboard = sunScoreboard;
        setSunScore(GameData.INITIAL_SCORE);  //pool avalie

        bgImage = new ImageIcon(this.getClass().getResource("images/mainBG.png")).getImage();

        peashooterImage = new ImageIcon(this.getClass().getResource("images/plants/peashooter.gif")).getImage();
        freezePeashooterImage = new ImageIcon(this.getClass().getResource("images/plants/freezepeashooter.gif")).getImage();
        sunflowerImage = new ImageIcon(this.getClass().getResource("images/plants/sunflower.gif")).getImage();
        peaImage = new ImageIcon(this.getClass().getResource("images/pea.png")).getImage();
        freezePeaImage = new ImageIcon(this.getClass().getResource("images/freezepea.png")).getImage();

        normalZombieImage = new ImageIcon(this.getClass().getResource("images/zombies/zombie1.png")).getImage();
        coneHeadZombieImage = new ImageIcon(this.getClass().getResource("images/zombies/zombie2.png")).getImage();

        laneZombies = new ArrayList<>();
        laneZombies.add(new CopyOnWriteArrayList<>()); //line 1
        laneZombies.add(new CopyOnWriteArrayList<>()); //line 2
        laneZombies.add(new CopyOnWriteArrayList<>()); //line 3
        laneZombies.add(new CopyOnWriteArrayList<>()); //line 4
        laneZombies.add(new CopyOnWriteArrayList<>()); //line 5

        lanePeas = new ArrayList<>();
        lanePeas.add(new CopyOnWriteArrayList<>()); //line 1
        lanePeas.add(new CopyOnWriteArrayList<>()); //line 2
        lanePeas.add(new CopyOnWriteArrayList<>()); //line 3
        lanePeas.add(new CopyOnWriteArrayList<>()); //line 4
        lanePeas.add(new CopyOnWriteArrayList<>()); //line 5

        colliders = new Collider[45];
        for (int i = 0; i < 45; i++) {
            Collider a = new Collider();
            a.setLocation(44 + (i % 9) * 100, 109 + (i / 9) * 120);
            a.setAction(new PlantActionListener((i % 9), (i / 9)));
            colliders[i] = a;
            add(a, new Integer(0));
        }

        //colliders[0].setPlant(new FreezePeashooter(this,0,0));
/*
        colliders[9].setPlant(new Peashooter(this,0,1));
        laneZombies.get(1).add(new NormalZombie(this,1));*/

        activeSuns = new ArrayList<>();

        redrawTimer = new Timer(25, (ActionEvent e) -> {
            repaint();
        });
        redrawTimer.start();

        advancerTimer = new Timer(60, (ActionEvent e) -> advance());
        advancerTimer.start();

        sunProducer = new Timer(2000, (ActionEvent e) -> {// todo restore it
            produceSun();
        });
        sunProducer.start();

        zombieProducer = new Timer(7000, (ActionEvent e) -> {
            produceZombie(levelData);
        });
        zombieProducer.start();

    }

    private void produceSun() {
        Random rnd = new Random();
        Sun sta = new Sun(this, rnd.nextInt(800) + 100, 0, rnd.nextInt(300) + 200);
        activeSuns.add(sta);
        add(sta, new Integer(1));
    }

    private void produceZombie(LevelData levelData) {
        if (levelData.getLevelComplete()<=zombiesSpawned) {
            return;// no more zombies for this level
        }
        Random rnd = new Random();
        int[][] levelValue = levelData.getLevelValue();
        int row = rnd.nextInt(5);
        int rndValue = rnd.nextInt(100);
        Zombie z = null;
        for (int i = 0; i < levelValue.length; i++) {
            if (rndValue >= levelValue[i][0] && rndValue <= levelValue[i][1]) {
                z = Zombie.getZombie(levelData.getLevelContent()[i], GamePanel.this, row);
            }
        }
        zombiesSpawned++;
        laneZombies.get(row).add(z);
    }

    private void advance() {
        logItems();
        for (int i = 0; i < 5; i++) {
            for (Zombie z : laneZombies.get(i)) {
                z.advance();
            }

            for (int j = 0; j < lanePeas.get(i).size(); j++) {
                Pea p = lanePeas.get(i).get(j);
                p.advance();
            }

        }

        for (int i = 0; i < activeSuns.size(); i++) {
            activeSuns.get(i).advance();
        }

    }


    public int getSunScore() {
        return sunScore;
    }

    public void setSunScore(int sunScore) {
        this.sunScore = sunScore;
        sunScoreboard.setText(String.valueOf(sunScore));
    }

    public void stopTimers() {
        redrawTimer.stop();
        advancerTimer.stop();
        sunProducer.stop();
        zombieProducer.stop();
    }

    public void release() {
        laneZombies.forEach(List::clear);
        lanePeas.forEach(List::clear);
        activeSuns.clear();
    }

    private void logItems() {
        if (isLogEnabled) {
            System.err.println("zombies: " + laneZombies.stream().mapToLong(List::size).sum() +
                    ", peas: " + lanePeas.stream().mapToLong(List::size).sum() + ", suns: " + activeSuns.size());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, null);

        //Draw Plants
        for (int i = 0; i < 45; i++) {
            Collider c = colliders[i];
            if (c.assignedPlant != null) {
                Plant p = c.assignedPlant;
                if (p instanceof Peashooter) {
                    g.drawImage(peashooterImage, 60 + (i % 9) * 100, 129 + (i / 9) * 120, null);
                }
                if (p instanceof FreezePeashooter) {
                    g.drawImage(freezePeashooterImage, 60 + (i % 9) * 100, 129 + (i / 9) * 120, null);
                }
                if (p instanceof Sunflower) {
                    g.drawImage(sunflowerImage, 60 + (i % 9) * 100, 129 + (i / 9) * 120, null);
                }
            }
        }

        for (int i = 0; i < 5; i++) {
            for (Zombie z : laneZombies.get(i)) {
                if (z instanceof NormalZombie) {
                    g.drawImage(normalZombieImage, z.getPosX(), 109 + (i * 120), null);
                } else if (z instanceof ConeHeadZombie) {
                    g.drawImage(coneHeadZombieImage, z.getPosX(), 109 + (i * 120), null);
                }
                if (GameData.SHOW_ENEMY_HEALTH) {
                    g.setColor(Color.RED);
                    g.fillRoundRect(z.getPosX()+ 15, 109 + (i * 120) - 10,
                            (int)(30.0*z.getHealth()/z.getMaxHealth()), 5, 2,2);
                }
            }

            for (int j = 0; j < lanePeas.get(i).size(); j++) {
                Pea pea = lanePeas.get(i).get(j);
                if (pea instanceof FreezePea) {
                    g.drawImage(freezePeaImage, pea.getPosX(), 130 + (i * 120), null);
                } else {
                    g.drawImage(peaImage, pea.getPosX(), 130 + (i * 120), null);
                }
            }

        }

        levelInfo.paintIcon(null, g, 600, 20);
        updateLevelInfo();

        //if(!"".equals(activePlantingBrush)){
        //System.out.println(activePlantingBrush);
            /*if(activePlantingBrush == GameWindow.PlantType.Sunflower) {
                g.drawImage(sunflowerImage,mouseX,mouseY,null);
            }*/

        //}


    }

    private class PlantActionListener implements ActionListener {

        int x, y;

        public PlantActionListener(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            if (activePlantingBrush == PlantType.Sunflower) {
                if (getSunScore() >= GameData.CARD_SUN_FLOWER.getPrice()) {
                    colliders[x + y * 9].setPlant(new Sunflower(GamePanel.this, x, y));
                    setSunScore(getSunScore() - GameData.CARD_SUN_FLOWER.getPrice());
                }
            }
            if (activePlantingBrush == PlantType.Peashooter) {
                if (getSunScore() >= GameData.CARD_PEAR_SHOOTER.getPrice()) {
                    colliders[x + y * 9].setPlant(new Peashooter(GamePanel.this, x, y));
                    setSunScore(getSunScore() - GameData.CARD_PEAR_SHOOTER.getPrice());
                }
            }

            if (activePlantingBrush == PlantType.FreezePeashooter) {
                if (getSunScore() >= GameData.CARD_FREEZE_SHOOTER.getPrice()) {
                    colliders[x + y * 9].setPlant(new FreezePeashooter(GamePanel.this, x, y));
                    setSunScore(getSunScore() - GameData.CARD_FREEZE_SHOOTER.getPrice());
                }
            }
            activePlantingBrush = PlantType.None;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    private void updateLevelInfo() {
        levelInfo.setInfo(levelData.getCurrentLevel(), levelData.getRequiredProgress()-progress);
    }

    public void addProgress(int num) {
        progress = progress + num;
        System.out.println(progress);
        updateLevelInfo();
        if (progress >= levelData.getRequiredProgress()) {
            if (1 == levelData.getCurrentLevel()) {
                stopTimers();
                JOptionPane.showMessageDialog(null,
                        "LEVEL_CONTENT Completed !!!" + '\n' + "Starting next LEVEL_CONTENT");
                GameWindow.gw.dispose();
                release();
                levelData.write(2);
                GameWindow.gw = new GameWindow();
            } else {
                stopTimers();
                JOptionPane.showMessageDialog(null,
                        "LEVEL_CONTENT Completed !!!" + '\n' + "More Levels will come soon !!!" + '\n' + "Resetting data");
                release();
                levelData.write(1);
                System.exit(0);
            }
            progress = 0;
        }
    }

    public PlantType getActivePlantingBrush() {
        return activePlantingBrush;
    }

    public void setActivePlantingBrush(PlantType activePlantingBrush) {
        this.activePlantingBrush = activePlantingBrush;
    }

    public List<List<Zombie>> getLaneZombies() {
        return laneZombies;
    }

    public void setLaneZombies(List<List<Zombie>> laneZombies) {
        this.laneZombies = laneZombies;
    }

    public List<List<Pea>> getLanePeas() {
        return lanePeas;
    }

    public void setLanePeas(List<List<Pea>> lanePeas) {
        this.lanePeas = lanePeas;
    }

    public List<Sun> getActiveSuns() {
        return activeSuns;
    }

    public void setActiveSuns(List<Sun> activeSuns) {
        this.activeSuns = activeSuns;
    }

    public Collider[] getColliders() {
        return colliders;
    }

    public void setColliders(Collider[] colliders) {
        this.colliders = colliders;
    }

public static class LevelInfoIcon implements Icon {

    private String info = "Level 1:";

    public void setInfo(int level, int remainZombies) {
        this.info = "Level " + level + ": " + remainZombies;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setFont(new Font(Font.SERIF, Font.BOLD, 22));
        g.setColor(Color.BLACK);

        g.drawString(info, 870,23);
    }

    @Override
    public int getIconWidth() {
        return 50;
    }

    @Override
    public int getIconHeight() {
        return 20;
    }
}
}
