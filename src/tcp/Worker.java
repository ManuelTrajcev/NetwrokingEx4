package tcp;

import java.io.*;
import java.net.Socket;

public class Worker extends Thread {
    private Socket socket;
    private static final int clientCounter = 0;
    private File logFile;
    private File clientCounterFile;

    public Worker(Socket socket, File logFile, File clientCounterFile) {
        this.socket = socket;
        this.logFile = logFile;
        this.clientCounterFile = clientCounterFile;
    }

    @Override
    public void run() {
        try {
            execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void execute() throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile, true)));
        RandomAccessFile clientCounterRaf = new RandomAccessFile(clientCounterFile, "rw");

        try {
            Integer clientCount = increaseCounter(clientCounterRaf);

            String response = reader.readLine();
            writer.write("You are client number " + clientCount);;
            writer.write("\n");
            writer.flush();

            fileWriter.append("From client number " + clientCount + " recieved: " + response + '\n');
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.flush();
            writer.close();
            fileWriter.flush();
            fileWriter.close();
            reader.close();
            socket.close();
        }

    }

    private static synchronized int increaseCounter(RandomAccessFile raf) {
        Integer currClientCount = 0;

        try {
            currClientCount = raf.readInt();
            currClientCount++;
            raf.seek(0);
            raf.writeInt(currClientCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return currClientCount;
    }

}