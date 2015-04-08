package cu.uci.mirror;

/**
 * Created by Rigoberto L. Salgado Reyes on 3/31/15.
 */
public class Package {
    private final String arch;
    private final long size;
    private final String path;

    public Package(String arch, long size, String path) {
        this.arch = arch;
        this.size = size;
        this.path = path;
    }

    public String getArch() {
        return arch;
    }

    public long getSize() {
        return size;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Package aPackage = (Package) o;

        return size == aPackage.size && arch.equals(aPackage.arch) && path.equals(aPackage.path);
    }

    @Override
    public int hashCode() {
        int result = arch.hashCode();
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + path.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Package{" +
                "arch='" + arch + '\'' +
                ", size=" + size +
                ", path='" + path + '\'' +
                '}';
    }
}
