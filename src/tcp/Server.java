package tcp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    private int port;
    private String logFilePath;
    private String clientCounterFilePath;
    private File clientCounterRaf;

    public Server(int port, String logFilePath, String clientCounterFilePath) {
        this.port = port;
        this.logFilePath = logFilePath;
        this.clientCounterFilePath = clientCounterFilePath;
        this.clientCounterRaf = new File(clientCounterFilePath);
        initializeRaf();
    }

    private void initializeRaf() {
      try {
          RandomAccessFile raf = new RandomAccessFile(clientCounterRaf, "rw");
          raf.seek(0);
          raf.writeInt(0);
      } catch (FileNotFoundException e) {
          e.printStackTrace();
      } catch (IOException e) {
          e.printStackTrace();
      }
    }

    private void execute() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("SERVER: started!");
        System.out.println("SERVER: waiting for connections...");

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("SERVER: new client");
            Worker curr = new Worker(socket, new File(logFilePath), clientCounterRaf);
            curr.start();
        }
    }

    @Override
    public void run() {
        try {
            execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv("SERVER_PORT"));
        String logFilePath = System.getenv("LOG_FILE");
        String clientCounterFilePath = System.getenv("CLIENT_COUNTER_FILE");
        Server server = new Server(port, logFilePath, clientCounterFilePath);
        server.start();
    }
}
