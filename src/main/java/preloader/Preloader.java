package preloader;

import java.util.HashMap;

public abstract class Preloader {
    public abstract HashMap<String,byte[]> load();
}
