package cu.uci.rigo;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.xerces.parsers.DOMParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;



/**
 * Created by Rigoberto L. Salgado Reyes on 3/29/15.
 */
public class Main {

    public static void decompress(String gzipFile, String unGzipFile) {
        final int BUFFER = 2048;
        try {
            FileInputStream fin = new FileInputStream(gzipFile);
            BufferedInputStream in = new BufferedInputStream(fin);
            FileOutputStream out = new FileOutputStream(unGzipFile);
            GzipCompressorInputStream gzip = new GzipCompressorInputStream(in);

            final byte[] buffer = new byte[BUFFER];
            int n;
            while (-1 != (n = gzip.read(buffer))) {
                out.write(buffer, 0, n);
            }
            out.close();
            gzip.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void download(String link) {
        try {
            URL primary = new URL(link);
            ReadableByteChannel rbc = Channels.newChannel(primary.openStream());
            Path path = Paths.get(primary.getFile());
            String primaryFILE = path.getFileName().toString();
            FileOutputStream fos = new FileOutputStream(primaryFILE);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void traverse(org.w3c.dom.Node node) {

        org.w3c.dom.Node metaData = node.getFirstChild();

        if (metaData != null) {
            org.w3c.dom.NodeList children = metaData.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element element = (Element) child;

                    NodeList archList = element.getElementsByTagName("arch");
                    if (archList != null && archList.getLength() > 0) {
                        System.out.println(archList.item(0).getTextContent());
                    }

                    NodeList sizeList = element.getElementsByTagName("size");
                    if (sizeList != null && sizeList.getLength() > 0) {
                        Element item = (Element) sizeList.item(0);
                        System.out.println(item.getAttribute("package"));
                    }

                    NodeList locList = element.getElementsByTagName("location");
                    if (locList != null && locList.getLength() > 0) {
                        Element item = (Element) locList.item(0);
                        System.out.println(item.getAttribute("href"));
                    }
                }
                System.out.println("--------------------------------------------------------------");
            }
        }
    }

    public static void process(String xmlFile) {
        DOMParser parser = new DOMParser();

        try {
            parser.parse(xmlFile);
            org.w3c.dom.Document document = parser.getDocument();
            traverse(document);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean testURL(String strUrl) {
        try {
            URL url = new URL(strUrl);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.connect();
            return urlConn.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {

        final String REPODATA = "repodata";
        final String urlRepo1 = "http://suse.uci.cu/repo/oss/";

        try {
            String strpath = Paths.get(urlRepo1, REPODATA).toString().replace("http:/", "http://");
            if(!testURL(strpath)){
                strpath = Paths.get(urlRepo1, "suse", REPODATA).toString().replace("http:/", "http://");
            }

            Document doc = Jsoup.connect(strpath).get();
            doc.select("a").stream().forEach(x -> {
                String link = x.attr("abs:href");
                if (link.endsWith("primary.xml.gz")) {
                    System.out.printf("%s%n", link);
                    try {
                        URL primary = new URL(link);
                        Path path = Paths.get(primary.getFile());
                        String primaryFILE = path.getFileName().toString();
                          download(link);

                         decompress(primaryFILE, "primary.xml");

                         process("primary.xml");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //
                }
            });

        } catch (IOException e) {
            System.err.println("==> " + e.getMessage());
        }
    }
}
