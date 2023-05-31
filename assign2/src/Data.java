import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

enum Status {
    OFFLINE,
    QUEUING,
    QUEUING_RANKED,
    PLAYING
}

class Data {
    private MyConcurrentHashMap<String, Integer> clientScores;
    private MyConcurrentHashMap<String, Status> clientStatus;
    private MyConcurrentHashMap<String, Client> clients;
    private ArrayList<GameEngine> games;
    private ReentrantLock lock;

    public Data() {
        clientScores = new MyConcurrentHashMap<>();
        clientStatus = new MyConcurrentHashMap<>();
        clients = new MyConcurrentHashMap<>();
        games = new ArrayList<>();
        lock = new ReentrantLock();
        fillData();
    }

    private void fillData(){
        BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("resources/scores.txt"));
			String line = reader.readLine();

			while (line != null) {
                String[] parts = line.split(";");
                clientScores.put(parts[0], Integer.parseInt(parts[1]));
                clientStatus.put(parts[0], Status.OFFLINE);
                line = reader.readLine();
			}
            
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public Client addClient(String username, Socket socket) throws IOException, InterruptedException {
        Client client = new Client(socket, username);
        clients.put(username, client);
        return client;
    }

    public int getNumClients() {
        return clients.size();
    }

    public void setStatus(String username, Status status){
        clientStatus.put(username, status);
    }

    public int getScore(String username){
        if(clientScores.containsKey(username)) return clientScores.get(username);
        return 0;
    }

    public void updateScore(String username, int score){
        if(clientScores.containsKey(username)) clientScores.put(username, clientScores.get(username) + score);
        else clientScores.put(username, score);
    }

    public void save() throws IOException{
        List<MyConcurrentHashMap.Node<String, Integer>> allData = clientScores.getAllData();
        try {
            FileWriter myWriter = new FileWriter("resources/scores.txt");
            for (MyConcurrentHashMap.Node<String, Integer> node : allData) {
                myWriter.write(node.key + ";" + node.value + "\n");
            }
            myWriter.close();
            
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing the records");
            e.printStackTrace();
        }   
    }

    public ArrayList<Client> getClientsByStatus(Status status){
        ArrayList<Client> list = new ArrayList<>();
        for(String username : clientStatus.getKeysByValue(status)){
            if(clients.containsKey(username)){
                list.add(clients.get(username));
            }
        }
        return list;
    }

    public Status getClientStatus(String username){
        return clientStatus.get(username);
    }

    public void addGame(GameEngine game){
        Boolean done = false;
        while(!done){
            try {
                if(lock.tryLock(1, TimeUnit.SECONDS)){
                    games.add(game);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
                done = true;
            }
        }
    }

    public Boolean reconnect(String username, Socket socket) throws IOException{
        Boolean done = false;
        Boolean found = false;
        while(!done){
            try {
                if(lock.tryLock(1, TimeUnit.SECONDS)){
                    for(GameEngine game : games){
                        found = game.reconnect(username, socket);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
                done = true;
            }
        }
        return found;
    }
}
