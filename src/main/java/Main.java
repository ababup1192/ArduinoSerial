public class Main {
    public static void main(String[] args){
        try {
            ArduinoSerialPortListener arduinoSerialPortListener = new ArduinoSerialPortListener("/dev/tty.usbmodem1411");
            for(int i=0;i<1000;i++){
                arduinoSerialPortListener.writeToArduino("y");
                Thread.sleep(1000);
                arduinoSerialPortListener.writeToArduino("n");
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
