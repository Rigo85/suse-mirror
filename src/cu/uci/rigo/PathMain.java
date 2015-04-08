package cu.uci.rigo;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Rigoberto L. Salgado Reyes on 3/31/15.
 */
public class PathMain {
    public static void main(String[] args) {
        String strpath = "http://suse.uci.cu/updates";

        String data = "repodata";

        Path path = Paths.get(strpath);
/*
        System.out.println(path.getFileName().toString());
        System.out.println(path.getName(0));
        System.out.println(path.getName(1));
        System.out.println(path.getName(2));*/

        System.out.println(path.subpath(1, path.getNameCount()).toString());
        System.out.println(Paths.get(path.subpath(1, path.getNameCount()).toString(), data));
    }
}
