package org.usfirst.frc3946.Utilities;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//You may want to add this class to a package, we recommend:
//package org.usfirst.frc3946.Utilities;

import com.sun.squawk.util.StringTokenizer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Utility;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;

/**
 *
 * @author Gustave Michel
 */
public class RaspberryPi {
    
    private String url = "socket://10.39.46.12:10000";
    private int bufferSize = 64;
    private char delimiter = ',';
    
    private SocketConnection m_socket;
    private InputStream m_is;
    private OutputStream m_os;
    
    String m_rawData;
    byte[] m_receivedData;
    
    private boolean m_connected = false;
    
    Thread m_thread;
    private boolean m_enabled =false;
    private boolean m_run = true;
    
    /**
     * Used to interface the RaspberryPi's Thread to the Robot's Subsystem and Commands
     * All fields need to be static, and all methods need to be synchronized and static.
     */
    public static class DataKeeper {
        private static int m_distance = 0;
        private static int m_offset = 0;
        private static double m_time = 0;
        private static boolean m_report = false;
        
        public static synchronized void setReport(boolean report) {
            m_report = report;
            SmartDashboard.putBoolean("PiReport", m_report);
        }
        
        public static synchronized void setDistance(int distance) {
            m_distance = distance;
            SmartDashboard.putNumber("PiDistance", m_distance);
        }
        public static synchronized void setOffset(int offset) {
            m_offset = offset;
            SmartDashboard.putNumber("PiOffset", m_offset);
        }
        public static synchronized void setTime(double time) {
            m_time = time;
            SmartDashboard.putNumber("PiTime", m_time);
        }
        
        public static synchronized boolean getReport() {
            return m_report;
        }
        
        public static synchronized int getDistance() {
            return m_distance;
        }
        public static synchronized int getOffset() {
            return m_offset;
        }
        public static synchronized double getTime() {
            return m_time;
        }
    }
    
    private class RaspberryPiThread extends Thread {
        RaspberryPi m_raspberryPi;
        public int distance;
        public int offset;
        public double time;
        private boolean report;
    
        public RaspberryPiThread(RaspberryPi raspberryPi) {
            super("RaspberryPiSocket");
            m_raspberryPi = raspberryPi;
        }
        
        public void run() {
            while(m_run) {
                if(m_raspberryPi.isEnabled()) { //Checks for Thread to run
                    if(m_raspberryPi.isConnected()) {
                        report = true;
                        try {
                            String[] data = m_raspberryPi.tokenizeData(m_raspberryPi.getRawData()); //Get and examine Data
                            time = Timer.getFPGATimestamp(); //Timestamp used to check if data was updated from outside thread (through DataKeeper)
                            if(data.length < 2) { //Error Check
                                report = false; //If a remote was made
                            } else {
                                try {
                                    distance = Integer.parseInt(data[1]); //Get data and parse it to proper data types
                                    offset = Integer.parseInt(data[0]);
                                } catch(NumberFormatException ex) {
                                    report = false;
                                }
                            }
                        } catch (IOException ex) {
                            report = false;
                        }
                        DataKeeper.setReport(report);
                                
                        if(report) { //Store Data in DataKeeper
                            DataKeeper.setDistance(distance);
                            DataKeeper.setOffset(offset);
                            DataKeeper.setTime(time);
                        }
                    } else {
                        try {
                            m_raspberryPi.connect();
                        } catch (IOException ex) {
                            DataKeeper.setReport(false);
                        }
                    }
                }
                try {
                    Thread.sleep(375); //Wait half second before getting Data again
                } catch(InterruptedException ex) {}
            }
        }
    }
    
    public RaspberryPi() {
        m_enabled = false;
        m_thread = new RaspberryPiThread(this);
        try{
         connect();
        } catch (IOException ex){
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
        
        m_thread.start();
    }
    
    public synchronized void connect() throws IOException {
        m_socket = (SocketConnection) Connector.open(url);//, Connector.READ_WRITE, true);
        m_is = m_socket.openInputStream();
        m_os = m_socket.openOutputStream();
        m_connected = true;
        
    }
    
    public synchronized void disconnect() throws IOException {
        m_socket.close();
        m_is.close();
        m_os.close();
        m_connected = false;
    }
    
    public synchronized boolean isConnected() {
        //need to actually test the connection 
        //to figure out if we're connected or not
        try{
            m_os.write('\n'); //request Data
            m_connected = true;
        } catch(IOException ex){
            m_connected = false;
        } catch(Exception ex) {
            m_connected = false;
            
        }
        
        return m_connected;
    }
    
    public synchronized boolean isEnabled() {
        return m_enabled;
    }
    
    public int getOffset() {
        return DataKeeper.getOffset();
    }
    
    public int getDistance() {
        return DataKeeper.getDistance();
    }
    
    public double getTime() {
        return DataKeeper.getTime();
    }
    
    public boolean getReport() {
        return DataKeeper.getReport();
    }
    
    public synchronized void start() {
        m_enabled = true;
    }
    
    public synchronized void stop() {
        m_enabled = false;
    }
    
    /**
     * Requests data from RaspberryPi
     * @return String returned from RaspberryPi
     * @throws IOException 
     */
    public synchronized String getRawData() throws IOException {
        byte[] input; //temporary holder
        
        if (m_connected) {
            m_os.write('G'); //request Data
            System.out.println("Requested Data");
            
            if(m_is.available() <= bufferSize) {
                input = new byte[m_is.available()]; //storage space sized to fit!
                m_receivedData = new byte[m_is.available()]; //using stream to simply null detection
                m_is.read(input); //Read in data from Pi
                for(int i = 0; (input != null) && (i < input.length); i++) {
                    m_receivedData[i] = input[i]; //transfer input to full size storage
                }
            } else {
                System.out.println("PI OVERFLOW");
                m_is.skip(m_is.available()); //reset if more is stored than buffer
                return null;
            }
            
            m_rawData = ""; //String to transfer received data to
            System.out.println("Raw Data: "+m_receivedData.length);
            for (int i = 0; i < m_receivedData.length; i++) {
                m_rawData += (char) m_receivedData[i]; //Cast bytes to chars and concatinate them to the String
            }
            System.out.println(m_rawData);
            return m_rawData;
        } else {
            connect();
            return null;
        }
    }
    
    /**
     * Separates input String into many Strings based on the delimiter given above
     * @param input String to be tokenized
     * @return String Array of Tokenized Input String
     */
    public synchronized String[] tokenizeData(String input) {
        StringTokenizer tokenizer = new StringTokenizer(input, String.valueOf(delimiter));
        String output[] = new String[tokenizer.countTokens()];
        
        for(int i = 0; i < output.length; i++) {
            output[i] = tokenizer.nextToken();
        }
        return output;
    }
}
