package cu.uci.rigo;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Rigoberto L. Salgado Reyes on 3/31/15.
 */
public class StreamMain {
    public static void main(String[] args){
        //System.out.println("okok");

        ArrayList<String> list = new ArrayList<>(Arrays.asList(
                "http://isos.uci.cu/Linux/CentOS/7.0/CentOS-7.0-1406-x86_64-NetInstall.iso",
                "http://isos.uci.cu/Linux/Debian/Wheezy/debian-7.5.0-amd64-netinst.iso",
                "http://isos.uci.cu/Linux/Lubuntu/14.10/lubuntu-14.10-desktop-amd64.iso",
                "http://isos.uci.cu/Linux/Lubuntu/14.10/lubuntu-14.10-desktop-i386.iso",
                "http://isos.uci.cu/Linux/clearos/clearos-professional-6.5.0-x86_64.iso",
                "http://isos.uci.cu/Linux/Nova/nova_migracion_UCI.iso"));

        list.stream().forEach(x -> {
            System.out.println(x);
        });

    }
}
