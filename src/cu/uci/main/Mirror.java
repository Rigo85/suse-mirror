package cu.uci.main;

import cu.uci.mirror.DownloadManager;

/**
 * Created by Rigoberto L. Salgado Reyes on 3/31/15.
 */
public class Mirror {
    public static void main(String[] args) {
        try {
            if (args.length != 0)
                new DownloadManager(args[0]);
            else
                new DownloadManager("config.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
