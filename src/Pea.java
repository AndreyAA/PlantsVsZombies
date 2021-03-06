import java.awt.*;
import java.util.List;
import java.util.Objects;

/**
 * Created by Armin on 6/25/2016.
 */
public class Pea {
    private final int id;
    private int posX;
    protected GamePanel gp;
    private int myLane;

    public Pea(GamePanel parent, int lane, int startX) {
        this.id = Ids.getNextId();
        this.gp = parent;
        this.myLane = lane;
        posX = startX;
    }

    public void advance() {
        Rectangle pRect = new Rectangle(posX, 130 + myLane * 120, 28, 28);
        boolean exit = false;
        List<Zombie> myLaneZombies = gp.getLaneZombies().get(myLane);
        for (Zombie z: myLaneZombies) {
            Rectangle zRect = new Rectangle(z.getPosX(), 109 + myLane * 120, 400, 120);
            if (pRect.intersects(zRect)) {
                z.setHealth(z.getHealth() - 300);
                exit = true;
                if (z.getHealth() < 0) {
                    System.out.println("ZOMBIE DIED");

                    myLaneZombies.remove(z);
                    gp.addProgress(1);
                }
                gp.getLanePeas().get(myLane).remove(this);
                break;
            } else {
                // not hit
                posX += 15;
            }
        }

        if (!exit) {
            // continue moving
            posX += 15;
        }

        if (posX > 1000) {
                gp.getLanePeas().get(myLane).remove(this);
        }
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getMyLane() {
        return myLane;
    }

    public void setMyLane(int myLane) {
        this.myLane = myLane;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pea)) return false;
        Pea pea = (Pea) o;
        return id == pea.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
