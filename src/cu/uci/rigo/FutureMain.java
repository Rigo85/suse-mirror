package cu.uci.rigo;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Rigoberto L. Salgado Reyes on 3/30/15.
 */
public class FutureMain {
    public static void main(String[] args) {

        ArrayList<Integer> list = new ArrayList<>();

        for (int i = 1; i <= 10; i++)
            list.add(i);

        list.stream().sequential().forEach(x -> {
            ArrayList<Integer> l = new ArrayList<>(Arrays.asList(x * 1, x * 2, x * 3, x * 4, x * 5));
            l.parallelStream().forEach(y -> System.out.printf("%d %d%n", x, y));
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++");
        });

        //System.out.println(sum);

        /*ArrayList<String> list = new ArrayList<>(Arrays.asList(
                "http://isos.uci.cu/Linux/CentOS/7.0/CentOS-7.0-1406-x86_64-NetInstall.iso",
                "http://isos.uci.cu/Linux/Debian/Wheezy/debian-7.5.0-amd64-netinst.iso",
                "http://isos.uci.cu/Linux/Lubuntu/14.10/lubuntu-14.10-desktop-amd64.iso",
                "http://isos.uci.cu/Linux/Lubuntu/14.10/lubuntu-14.10-desktop-i386.iso",
                "http://isos.uci.cu/Linux/clearos/clearos-professional-6.5.0-x86_64.iso",
                "http://isos.uci.cu/Linux/Nova/nova_migracion_UCI.iso"));


        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "6");

        list.parallelStream().forEach(x -> {
            try {
                URL primary = new URL(x);
                ReadableByteChannel rbc = Channels.newChannel(primary.openStream());
                Path path = Paths.get(primary.getFile());
                String primaryFILE = path.getFileName().toString();
                FileOutputStream fos = new FileOutputStream(primaryFILE);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        });*/

        /*list.stream().map(x -> {
            CompletableFuture future1 = CompletableFuture.supplyAsync(() -> {
                try {
                    URL primary = new URL(x);
                    ReadableByteChannel rbc = Channels.newChannel(primary.openStream());
                    Path path = Paths.get(primary.getFile());
                    String primaryFILE = path.getFileName().toString();
                    FileOutputStream fos = new FileOutputStream(primaryFILE);
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                    return true;
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                    return false;
                }
            });
            return future1;
        }).collect(Collectors.toList()).stream()
          .forEach(f -> f.join());*/

        /*CompletableFuture future1 = CompletableFuture.supplyAsync(() -> {
            try {
                URL primary = new URL(link1);
                ReadableByteChannel rbc = Channels.newChannel(primary.openStream());
                Path path = Paths.get(primary.getFile());
                String primaryFILE = path.getFileName().toString();
                FileOutputStream fos = new FileOutputStream(primaryFILE);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                return true;
            } catch (IOException e) {
                System.err.println(e.getMessage());
                return false;
            }
        });

        CompletableFuture future2 = CompletableFuture.supplyAsync(() -> {
            try {
                URL primary = new URL(link2);
                ReadableByteChannel rbc = Channels.newChannel(primary.openStream());
                Path path = Paths.get(primary.getFile());
                String primaryFILE = path.getFileName().toString();
                FileOutputStream fos = new FileOutputStream(primaryFILE);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                return true;
            } catch (IOException e) {
                System.err.println(e.getMessage());
                return false;
            }
        });

        System.out.println("++++++++++++++++++");


        future1.join();
        System.out.println(" 1++++++++++++++++++");
        future2.join();
        System.out.println(" 2++++++++++++++++++");*/

    }
}
