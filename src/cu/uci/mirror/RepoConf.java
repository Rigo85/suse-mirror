package cu.uci.mirror;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Rigoberto L. Salgado Reyes on 3/30/15.
 */
public class RepoConf {

    @SerializedName("repositories")
    private ArrayList<String> repositories = new ArrayList<>();

    @SerializedName("concurrentDownloads")
    private int concurrentDownloads = 0;

    @SerializedName("outputDirectory")
    private String outputDirectory = "";

    @SerializedName("arch")
    private String arch = "all";

    @SerializedName("proxyHost")
    private String proxyHost = "";

    @SerializedName("proxyPort")
    private String proxyPort = "";

    @SerializedName("proxyUser")
    private String proxyUser = "";

    @SerializedName("proxyPasswd")
    private String proxyPasswd = "";

    public String getProxyHost() {
        return proxyHost = "";
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public String getProxyPasswd() {
        return proxyPasswd;
    }

    public void setProxyPasswd(String proxyPasswd) {
        this.proxyPasswd = proxyPasswd;
    }

    public ArrayList<String> getRepositories() {
        return repositories;
    }

    public void setRepositories(ArrayList<String> repositories) {
        this.repositories = repositories;
    }

    public int getConcurrentDownloads() {
        return concurrentDownloads;
    }

    public void setConcurrentDownloads(int concurrentDownloads) {
        this.concurrentDownloads = concurrentDownloads;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public String getArch() {
        return arch;
    }

    public void setArch(String arch) {
        this.arch = arch;
    }

    @Override
    public String toString() {
        return "RepoConf{" +
                "repositories=" + repositories +
                ", concurrentDownloads=" + concurrentDownloads +
                ", outputDirectory='" + outputDirectory + '\'' +
                ", arch='" + arch + '\'' +
                ", proxyHost='" + proxyHost + '\'' +
                ", proxyPort='" + proxyPort + '\'' +
                ", proxyUser='" + proxyUser + '\'' +
                ", proxyPasswd='" + proxyPasswd + '\'' +
                '}';
    }
}
