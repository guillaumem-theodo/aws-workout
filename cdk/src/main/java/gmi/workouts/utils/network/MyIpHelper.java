package gmi.workouts.utils.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class MyIpHelper {

    public static String getMyIPAddressCIDR() {
        try {
            return getIp() + "/32";
        } catch (Exception e) {
            return "0.0.0.0/0";
        }
    }

    private static String getIp() throws Exception {
        URL whatIsMyIp = new URL("http://checkip.amazonaws.com");
        try(BufferedReader in = new BufferedReader(new InputStreamReader(whatIsMyIp.openStream()))) {
            return in.readLine();
        }
    }
}
