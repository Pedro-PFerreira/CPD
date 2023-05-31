import java.util.ArrayList;

public class ClientGroup {
    private ArrayList<Client> clients;
    private int totalRank;
    private int currentPlayer;

    public ClientGroup(){
        clients = new ArrayList<>();
        totalRank = 0;
        currentPlayer = 0;
    }

    public int getNumClients(){
        return clients.size();
    }

    public void addClient(Client client, int rank){
        clients.add(client);
        totalRank += rank;
    }

    public int getTotalRank(){
        return totalRank;
    }

    public ArrayList<Client> getClients(){
        return clients;
    }

    public Client getNextClient(){
        Client client = clients.get(currentPlayer);
        currentPlayer = (currentPlayer + 1) % clients.size();
        return client;
    }
}
