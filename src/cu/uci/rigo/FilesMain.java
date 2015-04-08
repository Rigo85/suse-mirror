package cu.uci.rigo;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static java.nio.file.FileVisitResult.CONTINUE;

/**
 * Created by Rigoberto L. Salgado Reyes on 4/1/15.
 */
public class FilesMain {

    private static class RPMFile {
        private Path path;
        private long size;

        public RPMFile(Path path, long size) {
            this.path = path;
            this.size = size;
        }

        public Path getPath() {
            return path;
        }

        public long getSize() {
            return size;
        }

        @Override
        public String toString() {
            return "RPMFile{" +
                    "path=" + path +
                    ", size=" + size +
                    '}';
        }
    }


    public static void main(String[] args) {

        final ArrayList<Integer> l1 = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 15, 11, 12));
        final ArrayList<Integer> l2 = new ArrayList<>(Arrays.asList(11, 12));
        final ArrayList<Integer> l3 = new ArrayList<>(Arrays.asList(4, 15));

        final java.util.List<Integer> tmp = l1.stream().filter(x -> l2.stream().allMatch(y -> !y.equals(x))).collect(Collectors.toList());
        l1.clear();
        l1.addAll(tmp);

        final java.util.List<Integer> tmp2 = l1.stream().filter(x -> l3.stream().allMatch(y -> !y.equals(x))).collect(Collectors.toList());
        l1.clear();
        l1.addAll(tmp2);

        l1.stream().forEach(System.out::println);

/*
        l1.stream()
                .map(x -> l2.stream().allMatch(y -> !y.equals(x)) ? x : null)
                .collect(Collectors.toList()).stream()
                .filter(z -> z != null)
                .forEach(System.out::println);

        System.out.println("+++++++++++++++++++");

        l1.stream().filter(x -> l2.stream().allMatch(y -> !y.equals(x))).forEach(System.out::println);
*/
        //l1.removeAll(l2);

        // l1.stream().filter(x -> x >= 4).forEach(System.out::println);

        //System.out.format("%s %n", l1.stream().anyMatch(x -> x == 10));

        //System.out.printf("%s%n", l1.stream().collect(Collectors.summingInt(x -> x)).intValue());

        // l1.stream().collect(Collectors.joining().)

        // final Path path = Paths.get("http://suse.uci.cu/packman/Extra/src/Q7Z-0.8.0-1.12.src.rpm");
        //  System.out.printf("%s%n", path.);
        // Get the Path object for the default directory
        //  Path startDir = Paths.get("/run/media/rigo/RIGO_500/repo/suse.uci.cu/");

        // System.out.printf("%s%n", startDir.getParent());
/*
        try {
            Files.find(startDir, 5, (path, basicFileAttributes) -> path.toString().endsWith(".gz") ).forEach(System.out::println);
        } catch (IOException e) {
            System.err.printf("%s%n", e);
        }
*/
       /* ArrayList<RPMFile> list = new ArrayList<>();
        // Get a file visitor object
        FileVisitor<Path> visitor = getFileVisitor(list);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");

        try {
            // Traverse the contents of the startDir
            System.out.printf("------------------> %s%n", dateFormat.format(new Date()));
            Files.walkFileTree(startDir, visitor);
            System.out.printf("------------------> %s%n", dateFormat.format(new Date()));
            //final List<Boolean> collect = list.stream().map(x -> x != null).collect(Collectors.toList());
            list.stream().forEach(x -> System.out.printf("%s%n", x));
            System.out.printf("------------------> %s%n", dateFormat.format(new Date()));

        } catch (IOException e) {
            System.err.printf("%s%n", e);
        }*/
    }

    public static FileVisitor<Path> getFileVisitor(ArrayList<RPMFile> list) {

        class DirVisitor<Path> extends SimpleFileVisitor<Path> {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (!file.toString().endsWith(".xml") && !file.toString().endsWith(".gz")) {
                    list.add(new RPMFile((java.nio.file.Path) file, attrs.size()));
                }
                return CONTINUE;
            }
        }

        FileVisitor<Path> visitor = new DirVisitor<>();

        return visitor;
    }
}
