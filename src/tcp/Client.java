package tcp;


import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Client extends Thread {
    private int serverPort;
    private String serverName;
    private String filePathName;
    private File filePath;
    private static List<String> randomStrings = new ArrayList<>();
    private static final Random random = new Random();
    public Client(int serverPort, String serverName, String filePathName) {
        this.serverPort = serverPort;
        this.serverName = serverName;
        this.filePathName = filePathName;
        this.filePath =  new File(this.filePathName);
        initList();
    }

    private void execute() throws IOException {
        Socket socket = new Socket(InetAddress.getByName(serverName), serverPort);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedWriter fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, true)));
        System.out.println("CLIENT: starting...");

        try {
            writer.write(randomStrings.get(random.nextInt(5)));
            writer.write("\n");
            writer.flush();

            String response = reader.readLine();
            fileWriter.write("Client received: " + response);
            System.out.println("Client received: " + response);
            fileWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.flush();
            writer.close();
            reader.close();
            fileWriter.flush();
            fileWriter.close();
            socket.close();
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

    private void initList() {
        randomStrings.add("Hello World");
        randomStrings.add("GoodMorning World");
        randomStrings.add("GoodAfternoon World");
        randomStrings.add("GoodEvening World");
        randomStrings.add("GoodBye World");
    }

    public static void main(String[] args) {
        String serverPort = System.getenv("SERVER_PORT");
        String serverName = System.getenv("SERVER_NAME");
        String filePathName = "./ClientsLogs";
        if (serverPort == null) {
            throw new RuntimeException("Server port cannot be null");
        }
        Client client = new Client(Integer.parseInt(serverPort), serverName, filePathName);
        client.start();
    }
}
