import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by AndreyAA on 26.05.2019.
 */
public class Ids {

    private static final AtomicInteger id = new AtomicInteger(1);

    public static int getNextId() {
        return id.incrementAndGet();
    }

}
