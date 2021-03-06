package ciid2015.exquisitedatacorpse;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;

public class AppNetworkLogger {

    private static final int MIN_PORT_NUMBER = 12000;

    private int mPort;

    private final String mLogFile;

    private final OscP5 mOSC;

    private final NetAddress mBroadcastLocation;

    private AppNetworkLogger(String pServer, String pLogFile) {
        mPort = MIN_PORT_NUMBER;
        mLogFile = pLogFile;
        while (!available(mPort)) {
            mPort++;
        }
        mOSC = new OscP5(this, mPort);
        mBroadcastLocation = new NetAddress(pServer, 32000);

        prepareExitHandler();
        connect();
    }

    public void oscEvent(OscMessage theOscMessage) {
        String mMessage = System.currentTimeMillis() + " | " + theOscMessage.toString() + " | " + getAsString(theOscMessage.arguments());
        System.out.println(mMessage);
        append(mLogFile, mMessage);
    }

    public static String getAsString(Object[] theObject) {
        StringBuilder s = new StringBuilder();
        for (Object theObject1 : theObject) {
            s.append(theObject1).append("\t");
        }
        return s.toString();
    }

    public final void connect() {
        OscMessage m = new OscMessage("/server/connect");
        m.add(mPort);
        mOSC.send(m, mBroadcastLocation);
    }

    public final void disconnect() {
        OscMessage m = new OscMessage("/server/disconnect");
        m.add(mPort);
        mOSC.send(m, mBroadcastLocation);
    }

    private void prepareExitHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                System.out.println("### disconnecting client*");
                disconnect();
            }
        }));
    }

    private static boolean available(int port) {
        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }

        return false;
    }

    private void append(String pFile, String pString) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(pFile, true)));
            out.println(pString);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            new AppNetworkLogger(args[0], args[1]);
        } else {
            new AppNetworkLogger("edc.local", System.getProperty("user.dir") + "/" + "logfile.txt");
        }
    }
}
