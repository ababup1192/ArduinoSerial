import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 *
 * @author P90Puma
 */
public class ArduinoSerialPortListener implements SerialPortEventListener {

    public static SerialPort serial_port;
    public static InputStream input;
    public static OutputStream output;

    public ArduinoSerialPortListener() throws Exception {
        init();
        // add event listeners
        serial_port.addEventListener((SerialPortEventListener) this);
        serial_port.notifyOnDataAvailable(true);
        //system needs time to work with the RXTX library on initial setup
        Thread.sleep(1500);

    }

    public ArduinoSerialPortListener(String serialport) throws Exception {
        init(serialport);
        // add event listeners
        serial_port.addEventListener((SerialPortEventListener) this);
        serial_port.notifyOnDataAvailable(true);
        //system needs time to work with the RXTX library on initial setup
        Thread.sleep(1500);
    }

    public void writeToArduino(String str) throws Exception {
        output.write(str.getBytes());
    }

    public static ArrayList<CommPortIdentifier> getSerialPorts() throws Exception {
        ArrayList<CommPortIdentifier> ports = new ArrayList<CommPortIdentifier>();
        CommPortIdentifier port;
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();
        System.out.println("Get List");
        while (portList.hasMoreElements()) {
            port = (CommPortIdentifier) portList.nextElement();
            if (port.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                ports.add(port);
            }
        }
        return ports;
    }

    public static void init(String serialport) throws Exception {

        CommPortIdentifier portId = null;
        if (serialport == null) {
            ArrayList<CommPortIdentifier> ports = getSerialPorts();
            if (ports.isEmpty()) {
                portId = CommPortIdentifier.getPortIdentifier("ttyUSB0"); //linux FTDI for now
            } else {
                portId = ports.get(0); //grab the first one.
            }

        } else {
            portId = CommPortIdentifier.getPortIdentifier(serialport);
        }

        System.out.println("Setup SerialToIR on port " + portId.getName()); //this will fail if any issues arise and be caught in the WS layer.

        serial_port = (SerialPort) portId.open("Arduino", 2000);
        serial_port.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

        input = serial_port.getInputStream();
        output = serial_port.getOutputStream();

    }

    public static void init() throws Exception {
        init(null);
    }

    /**
     * This should be called when you stop using the port. This will prevent
     * port locking on platforms like Linux.
     */
    public synchronized void close() {
        if (serial_port != null) {
            serial_port.removeEventListener();
            serial_port.close();
        }
    }

    /**
     * Handle an event on the serial port. Read the data and print it.
     */
    @Override
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                int available = input.available();
                byte chunk[] = new byte[available];
                input.read(chunk, 0, available);

                // Displayed results are codepage dependent
                System.out.print(new String(chunk));
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
        // Ignore all the other eventTypes, but you should consider the other ones.
    }
}