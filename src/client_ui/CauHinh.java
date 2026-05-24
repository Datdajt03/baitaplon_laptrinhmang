package client_ui;

import java.io.*;
import java.util.Properties;

public class CauHinh {
    public static String SERVER_IP = "localhost";
    public static int RMI_PORT = 1099;
    public static String UPLOAD_DIR = "";
    public static String DOWNLOAD_DIR = "src/luutru/download/";

    private static final String CONFIG_FILE = "client_config.properties";

    static {
        loadConfig();
    }

    public static void loadConfig() {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(CONFIG_FILE)) {
            props.load(in);
            SERVER_IP = props.getProperty("server_ip", "localhost");
            try {
                RMI_PORT = Integer.parseInt(props.getProperty("rmi_port", "1099"));
            } catch (NumberFormatException e) {
                RMI_PORT = 1099;
            }
            UPLOAD_DIR = props.getProperty("upload_dir", "");
            DOWNLOAD_DIR = props.getProperty("download_dir", "src/luutru/download/");
        } catch (IOException e) {
            // File doesn't exist yet, keep default values
        }
    }

    public static void saveConfig() {
        Properties props = new Properties();
        props.setProperty("server_ip", SERVER_IP);
        props.setProperty("rmi_port", String.valueOf(RMI_PORT));
        props.setProperty("upload_dir", UPLOAD_DIR);
        props.setProperty("download_dir", DOWNLOAD_DIR);
        try (OutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "Client Config Settings");
        } catch (IOException e) {
            System.err.println("Could not save config: " + e.getMessage());
        }
    }
}
