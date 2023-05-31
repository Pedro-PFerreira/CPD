import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

class ClientHandler implements Runnable {
    private Socket socket;
    private Data data;
    private String username;
    private ReentrantLock lock;

    public ClientHandler(Socket socket, Data data, String username) {
        this.socket = socket;
        this.data = data;
        this.username = username;
        this.lock = new ReentrantLock();
    }

    private ArrayList<Client> newGameQueue(Data data, int numClients, Boolean isRanked) {
        ArrayList<Client> players = null;
        Boolean done = false;

        while (!done) {
            try {
                if (lock.tryLock(1, TimeUnit.SECONDS)) {
                    ArrayList<Client> clients;
                    if (isRanked)
                        clients = data.getClientsByStatus(Status.QUEUING_RANKED);
                    else
                        clients = data.getClientsByStatus(Status.QUEUING);
                    if (clients.size() >= numClients) {
                        players = new ArrayList<>();
                        for (int i = 0; i < numClients; i++) {
                            Client client = clients.get(i);
                            players.add(client);
                            data.setStatus(client.getUsername(), Status.PLAYING);
                        }
                    }
                }
                else{
                    System.out.println("Wainting to try queing");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
                done = true;
            }
        }

        return players;
    }

    @Override
    public void run() {
        try {
            Boolean foundLastGame = false;
            if (data.getClientStatus(username) == Status.PLAYING) {
                foundLastGame = data.reconnect(username, socket);
            } else if (!foundLastGame) {
                Boolean isRanked = false;
                Client client = data.addClient(username, socket);
                String input = "";
                client.write("Select a Gamemode");
                client.write("1) Casual");
                client.write("2) Ranked");

                while (!input.equals("1") && !input.equals("2")) {
                    input = client.read();
                }

                if (input.equals("1")) {
                    data.setStatus(username, Status.QUEUING);
                } else {
                    data.setStatus(username, Status.QUEUING_RANKED);
                    isRanked = true;
                }

                ArrayList<Client> players = null;

                // Número Mínimo de Players para o Jogo Começar -> 4 (2 por Equipa)
                if ((players = newGameQueue(data, 4, isRanked)) != null) {
                    GameEngine engine = new GameEngine(players, isRanked, data);
                    data.addGame(engine);
                    Thread game = new Thread(engine, "Game Engine");
                    game.start();
                    game.join();

                    if(isRanked){
                        for(Entry<String, Integer> entry : engine.getScores().entrySet()){
                            data.updateScore(entry.getKey(), entry.getValue());
                        }
                    }

                    for(Client c : players){
                        data.setStatus(c.getUsername(), Status.OFFLINE);
                        c.write("Going back to menu");
                        new Thread(new ClientHandler(c.getSocket(), data, c.getUsername()), "Client Handler").start();
                    }
                }
                else{
                    client.write("Waiting for players to join...");
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
