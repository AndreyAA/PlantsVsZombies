/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.util.logging.Logger;

/**
 * @author Vaibhav Singh Sikarwar
 */
public class LevelData {

    private static String[][] LEVEL_CONTENT = {{"NormalZombie"}, {"NormalZombie", "ConeHeadZombie"}};
    private static int[][][] LEVEL_VALUE = {{{0, 99}}, {{0, 49}, {50, 99}}};
    private static int[] LEVEL_COMPLETE = {10, 10};

    private volatile int currentLevel;


    public LevelData() {
        try {
            File f = new File("LEVEL_CONTENT.vbhv");

            if (!f.exists()) {
                BufferedWriter bwr = new BufferedWriter(new FileWriter(f));
                bwr.write("1");
                bwr.close();
                currentLevel = 1;
            } else {
                BufferedReader br = new BufferedReader(new FileReader(f));
                currentLevel = Integer.parseInt(br.readLine());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            currentLevel = 1;
        }
    }

    public String[] getLevelContent() {
        return LEVEL_CONTENT[currentLevel-1];
    }
    public int[][] getLevelValue() {
        return LEVEL_VALUE[currentLevel-1];
    }

    public int getLevelComplete() {
        return LEVEL_COMPLETE[currentLevel-1];
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void write(int nextLevel) {
        File f = new File("LEVEL_CONTENT.vbhv");
        try {
            BufferedWriter bwr = new BufferedWriter(new FileWriter(f));
            bwr.write(String.valueOf(nextLevel));
            bwr.close();
            currentLevel = nextLevel;
        } catch (IOException ex) {
            Logger.getLogger(LevelData.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

    }

    public int getRequiredProgress() {
        return LEVEL_COMPLETE[currentLevel-1];
    }
}
