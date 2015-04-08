package cu.uci.mirror;

import com.google.gson.Gson;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.xerces.parsers.DOMParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.FileVisitResult.CONTINUE;

/**
 * Created by Rigoberto L. Salgado Reyes on 3/30/15.
 */
public class DownloadManager {

    private RepoConf conf = null;
    private final String REPODATA = "repodata";
    private ArrayList<Package> packages = null;

    public DownloadManager(String confFile) throws IOException {
        try (FileReader fr = new FileReader(new File(confFile))) {
            Gson g = new Gson();
            conf = g.fromJson(fr, RepoConf.class);
        } finally {
            if (conf == null) conf = new RepoConf();
        }

        //todo missing proxy configuration.
        if (conf.getConcurrentDownloads() != 0) {
            System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism",
                    String.valueOf(conf.getConcurrentDownloads()));
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");

        System.out.format("Starting the download: %s%n", dateFormat.format(new Date()));
        init();
        System.out.format("Finishing the download: %s%n", dateFormat.format(new Date()));
        System.out.format("Starting the clean: %s%n", dateFormat.format(new Date()));
        clean();
        System.out.format("Finishing the clean: %s%n", dateFormat.format(new Date()));
    }

    private static class Primary {
        private final String primaryXML;
        private final String rootURL;

        public Primary(String primaryXML, String rootURL) {
            this.primaryXML = primaryXML;
            this.rootURL = rootURL;
        }

        public String getPrimaryXML() {
            return primaryXML;
        }

        public String getRootURL() {
            return rootURL;
        }

        @Override
        public String toString() {
            return "Primary{" +
                    "primaryXML='" + primaryXML + '\'' +
                    ", rootURL='" + rootURL + '\'' +
                    '}';
        }
    }

    private void init() {
        final Stream<Primary> primaryFiles = conf.getRepositories().parallelStream().map(x -> {
            String strUrl = Paths.get(x, REPODATA).toString().replace("http:/", "http://");
            if (!testURL(strUrl)) {
                strUrl = Paths.get(x, "suse", REPODATA).toString().replace("http:/", "http://");
            }
            mkdir(strUrl);
            String gzFileUrl = repoPrimaryFile(strUrl);
            String xmlFile = null;
            if (gzFileUrl != null) {
                download(gzFileUrl, true);
                Path path = Paths.get(gzFileUrl);
                String gzFile = Paths.get(conf.getOutputDirectory(), path.subpath(1, path.getNameCount()).toString()).toString();
                xmlFile = decompress(gzFile);
            }
            return new Primary(xmlFile, Paths.get(strUrl).getParent().toString().replace("http:/", "http://"));
        }).collect(Collectors.toList()).parallelStream().filter(x -> x.getPrimaryXML() != null);

        primaryFiles.sequential().forEach(x -> {
            process(x, Paths.get(x.getPrimaryXML()).getParent().getParent().toString(), false);
            final long sum = packages.parallelStream().collect(Collectors.summingLong(Package::getSize));
            System.out.format("Total amount to download %s from %s%n", humanReadableByteCount(sum, true), x.getRootURL());
            packages.parallelStream().forEach(y -> download(y.getPath(), false));
        });
    }

    //todo validate URL with regex.
    private void mkdir(String strPath) {
        Path path = Paths.get(strPath);
        try {
            Files.createDirectories(Paths.get(conf.getOutputDirectory(), path.subpath(1, path.getNameCount()).toString()));
        } catch (IOException e) {
            System.err.format("Problems in creating directories \"%s\"%nMessage: \"%s\"%n",
                    Paths.get(conf.getOutputDirectory(), path.subpath(1, path.getNameCount()).toString()).toString(), e);
        }
    }

    private boolean testURL(String strUrl) {
        try {
            URL url = new URL(strUrl);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.connect();
            return urlConn.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            return false;
        }
    }

    private String repoPrimaryFile(String urlString) {
        try {
            Document doc = Jsoup.connect(urlString).get();
            for (Element e : doc.select("a")) {
                String link = e.attr("abs:href");
                if (link.endsWith("primary.xml.gz"))
                    return link;
            }
            return null;
        } catch (Exception e) {
            System.err.format("Problems in connecting to \"%s\"%nMessage: \"%s\"%n", urlString, e);
            return null;
        }
    }

    private void download(String link, boolean b) {
        String primaryFILE;
        try {
            URL primary = new URL(link);
            ReadableByteChannel rbc = Channels.newChannel(primary.openStream());
            Path path = Paths.get(link);
            primaryFILE = Paths.get(conf.getOutputDirectory(), path.subpath(1, path.getNameCount()).toString()).toString();
            FileOutputStream fos = new FileOutputStream(primaryFILE);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            rbc.close();
            fos.close();
        } catch (Exception e) {
            System.err.format("Problems in downloading \"%s\"%nMessage: \"%s\"%n", link, e);
            primaryFILE = null;
        }

        if (b && primaryFILE != null) {
            try {
                final Path path1 = Paths.get(primaryFILE);
                Files.find(path1.getParent(), 1,
                        (path, basicFileAttributes) -> !Files.isDirectory(path) && !path.getFileName().equals(path1.getFileName()))
                        .forEach(x -> {
                            try {
                                Files.delete(x);
                            } catch (Exception e) {
                                System.err.format("%s%n", e);
                            }
                        });
            } catch (Exception e) {
                System.err.format("Problems in updating primary file \"%s\"%nMessage: \"%s\"%n", primaryFILE, e);
            }
        }
    }

    private String decompress(String gzipFile) {
        final int BUFFER = 2048;
        try {
            FileInputStream fin = new FileInputStream(gzipFile);
            BufferedInputStream in = new BufferedInputStream(fin);
            String unGzipFile = gzipFile.substring(0, gzipFile.length() - 3);
            FileOutputStream out = new FileOutputStream(unGzipFile);
            GzipCompressorInputStream gzip = new GzipCompressorInputStream(in);
            final byte[] buffer = new byte[BUFFER];
            int n;
            while (-1 != (n = gzip.read(buffer))) {
                out.write(buffer, 0, n);
            }
            out.close();
            gzip.close();

            return unGzipFile;
        } catch (Exception e) {
            System.err.format("Problems in decompressing \"%s\"%nMessage: \"%s\"%n", gzipFile, e);

            return null;
        }
    }

    private void process(Primary p, String parent, boolean deleting) {
        DOMParser parser = new DOMParser();
        try {
            parser.parse(p.getPrimaryXML());
            org.w3c.dom.Document document = parser.getDocument();
            traverse(document, p, parent, deleting);
        } catch (Exception e) {
            System.err.format("Problems in processing \"%s\"%nMessage: \"%s\"%n", p.getPrimaryXML(), e);
        }
    }

    private void traverse(Node node, Primary p, String parent, boolean deleting) {
        org.w3c.dom.Node metaData = node.getFirstChild();
        if (metaData != null) {
            org.w3c.dom.NodeList children = metaData.getChildNodes();
            packages = null;
            Runtime.getRuntime().gc();
            packages = new ArrayList<>(children.getLength());
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) child;

                    String arch = "all";
                    long size = 0;
                    String path = "";

                    NodeList archList = element.getElementsByTagName("arch");
                    if (archList != null && archList.getLength() > 0) {
                        arch = archList.item(0).getTextContent();
                    }

                    NodeList sizeList = element.getElementsByTagName("size");
                    if (sizeList != null && sizeList.getLength() > 0) {
                        org.w3c.dom.Element item = (org.w3c.dom.Element) sizeList.item(0);
                        size = Integer.parseInt(item.getAttribute("package"));
                    }

                    NodeList locList = element.getElementsByTagName("location");
                    if (locList != null && locList.getLength() > 0) {
                        org.w3c.dom.Element item = (org.w3c.dom.Element) locList.item(0);
                        path = item.getAttribute("href");
                    }

                    try {
                        Files.createDirectories(Paths.get(parent, Paths.get(path).getParent().toString()));
                    } catch (IOException e) {
                        System.err.format("Problems in creating directory %s%nMessage: %s%n",
                                Paths.get(parent, Paths.get(path).getParent().toString()), e.getMessage());
                    }

                    final Path rpmPath = Paths.get(parent, path);
                    long tmpSize;
                    try {
                        tmpSize = Files.size(rpmPath);
                    } catch (Exception e) {
                        tmpSize = 0;
                    }
                    if (Files.notExists(rpmPath) || tmpSize != size || deleting) {
                        packages.add(new Package(arch, size, Paths.get(p.getRootURL(), path).toString().replace("http:/", "http://")));
                    }
                }
            }
        }
    }

    private FileVisitor<Path> getFileVisitor(ArrayList<Path> list) {

        class DirVisitor<Path> extends SimpleFileVisitor<Path> {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (!file.toString().endsWith(".xml") && !file.toString().endsWith(".gz")) {
                    list.add((java.nio.file.Path) file);
                }
                return CONTINUE;
            }
        }

        return new DirVisitor<>();
    }

    private void clean() {
        ArrayList<Path> list = new ArrayList<>();
        FileVisitor<Path> visitor = getFileVisitor(list);
        try {
            Files.walkFileTree(Paths.get(conf.getOutputDirectory()), visitor);
        } catch (Exception e) {
            System.err.format("Problems in cleaning of local mirror%nMessage: \"%s\"%n", e);
        }

        conf.getRepositories().stream().sequential().forEach(x -> {
            final Path p = Paths.get(x);
            String urlRoot = Paths.get(conf.getOutputDirectory(), p.subpath(1, p.getNameCount()).toString(), REPODATA).toString();
            if (Files.notExists(Paths.get(urlRoot))) {
                urlRoot = Paths.get(conf.getOutputDirectory(), p.subpath(1, p.getNameCount()).toString(), "suse", REPODATA).toString();
            }

            try {
                final Optional<Path> first = Files.find(Paths.get(urlRoot), 1,
                        (path, basicFileAttributes) -> path.toString().endsWith(".xml")).findFirst();

                process(new Primary(first.get().toString(), Paths.get(urlRoot).getParent().toString()),
                        Paths.get(urlRoot).getParent().toString(), true);

                final List<Path> collect = list.parallelStream()
                        .filter(w -> packages.stream().allMatch(y -> !y.getPath().equals(w.toString())))
                        .collect(Collectors.toList());

                list.clear();
                list.addAll(collect);
            } catch (Exception e) {
                System.err.format("%s%n", e);
            }
        });

        System.out.format("Total amount to remove %s from local mirror%n",
                humanReadableByteCount(list.parallelStream().collect(Collectors.summingLong((Path z) -> {
                    try {
                        return Files.size(z);
                    } catch (IOException e) {
                        System.out.printf("%s%n", e);
                        return 0l;
                    }
                })), true));

        list.parallelStream().forEach((Path z) -> {
            try {
                Files.delete(z);
            } catch (Exception e) {
                System.err.format("%s%n", e);
            }
        });
    }

    private String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.2f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
