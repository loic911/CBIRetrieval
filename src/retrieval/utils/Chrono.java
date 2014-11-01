package retrieval.utils;

import java.util.*;

/**
 * This class implements a simple chrono
 * @author Rollus Loic
 **/
public final class Chrono {
    /* */
    private Date D1;
    private Date D2;

    /**
     * Constructor for a chrono
     */
    public Chrono() {
        D1 = new Date();
    }

    /**
     * Stop chrono
     */
    public void stop() {
        D2 = new Date();
        printTime(D2.getTime() - D1.getTime());
    }

    /**
     * Getter of time
     * @return Time
     */
    public long getTime() {
        D2 = new Date();
        long delay = D2.getTime() - D1.getTime();
        return delay;
    }

    /**
     * Print time delay
     * @param delay Delay
     */
    public void printTime(long delay) {
        System.out.print("Execution time :");
        if (delay < 1000) {
            System.out.println(delay + " ms");
        } else {
            long d1 = delay / 1000;
            long ms = (delay - d1 * 1000);
            if (d1 < 60) {
                System.out.println(d1 + " s " + ms + " ms");
            } else {
                long d2 = d1 / 60;
                long reste = d1 - d2 * 60;
                if (d2 < 60) {
                    System.out.println(d2 + " min " + reste + " s");
                } else {
                    long d3 = d2 / 60;
                    long r2 = d2 - d3 * 60;
                    System.out.println(d3 + " h " + r2 + " min " + reste + " s");
                }
            }
        }
    }

}
