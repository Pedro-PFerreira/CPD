import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket socket;
    private OutputStream output;
    private PrintWriter writer;
    private InputStream input;
    private BufferedReader reader;
    private String username;

    Client(){
        this.username = "bot";
    }

    Client(Socket socket, String username) throws IOException, InterruptedException{
        this.socket = socket;
        this.username = username;
        input = socket.getInputStream();
        reader = new BufferedReader(new InputStreamReader(input));
        output = socket.getOutputStream();
        writer = new PrintWriter(output, true);
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String read() throws IOException{
        String message = "";
        boolean done = false;
        while (!done){
            try{
                message = reader.readLine();
                done = true;
            } catch (IOException e){
                close();
            }
        }
        return message;
    }

    public void write(String message){
        writer.println(message);
        writer.flush();
    }

    public void close() {
        try {
            socket.close();
            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSocket(Socket socket) throws IOException{
        this.socket = socket;
        input = socket.getInputStream();
        reader = new BufferedReader(new InputStreamReader(input));
        output = socket.getOutputStream();
        writer = new PrintWriter(output, true);
    }

    public Socket getSocket(){
        return this.socket;
    }
}
