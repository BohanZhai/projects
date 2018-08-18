package byog.Core;

import java.util.Random;

public class MyRandom {
    static Random rand = null;

    MyRandom(Integer seed) {
        rand = new Random(seed);
    }

    public Random getRand() {
        return rand;
    }
}
