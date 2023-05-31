import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

class GameEngine implements Runnable {
    private static final int NUM_ROUNDS = 1;
    private final Boolean isRanked;
    private final ClientGroup team1;
    private final ClientGroup team2;
    private int scoreTeam1;
    private int scoreTeam2;
    private Map<String, Integer> scores;
    private final Game game;
    private int currentTeam;

    public GameEngine(ArrayList<Client> clients, Boolean isRanked, Data data) {
        this.isRanked = isRanked;
        this.team1 = new ClientGroup();
        this.team2 = new ClientGroup();
        this.scoreTeam1 = 0;
        this.scoreTeam2 = 0;
        this.game = new Game();
        createTeams(clients, data);
        initScores(clients);
    }

    public Game getGame() {
        return this.game;
    }

    public void createTeams(ArrayList<Client> clients, Data data) {
        Random rand = new Random();

        // Dispôr os players aleatoriamente, procurando equipas com a mesma quantidade
        // de players ou o mais próximo possível
        if (!isRanked) {
            for (Client client : clients) {
                int score = data.getScore(client.getUsername());
                if (team2.getNumClients() < team1.getNumClients())
                    team2.addClient(client, score);
                else if (team1.getNumClients() < team2.getNumClients())
                    team1.addClient(client, score);
                else {
                    int randomizedTeam = rand.nextInt(1) + 1;
                    if (randomizedTeam == 1)
                        team1.addClient(client, score);
                    else
                        team2.addClient(client, score);
                }
            }
        }
        // Atribuir players um por um, sempre à equipa com menor rank. Em caso de
        // empate, colocar player na equipa com menos jogadores.
        else {
            clients = sortClientsByRank(clients, data);
            for (Client client : clients) {
                int score = data.getScore(client.getUsername());
                if (team1.getTotalRank() == team2.getTotalRank()) {
                    if (team1.getNumClients() > team2.getNumClients()) {
                        team2.addClient(client, score);
                    } else if (team1.getNumClients() < team2.getNumClients()) {
                        team1.addClient(client, score);
                    } else {
                        int randomizedTeam = rand.nextInt(1) + 1;
                        if (randomizedTeam == 1)
                            team1.addClient(client, score);
                        else
                            team2.addClient(client, score);
                    }
                } else if (team1.getTotalRank() > team2.getTotalRank()) {
                    team2.addClient(client, score);
                } else {
                    team1.addClient(client, score);
                }
            }
        }
    }

    private ArrayList<Client> sortClientsByRank(ArrayList<Client> clients, Data data) {
        ArrayList<Client> newClients = new ArrayList<>();

        while (clients.size() > 0) {
            int i = 0;
            int maxRank = 0;

            for (int j = 0; j < clients.size(); j++) {
                int score = data.getScore(clients.get(i).getUsername());
                if (maxRank < score) {
                    i = j;
                    maxRank = score;
                }
            }

            newClients.add(clients.get(i));
            clients.remove(i);
        }
        return newClients;
    }

    private void initScores(ArrayList<Client> clients) {
        scores = new HashMap<String, Integer>();
        for (Client client : clients)
            scores.put(client.getUsername(), 0);
    }

    private Client nextPlayer() {
        Client client;
        if (currentTeam == 1)
            client = team1.getNextClient();
        else
            client = team2.getNextClient();
        currentTeam = 3 - currentTeam;
        return client;
    }

    private Boolean guess(Client client) throws GameException {
        String word;

        try {
            client.write("Try to guess:");
            word = client.read();
            game.wrongGuess(word);
            client.write("Wrong choice");
            scores.merge(client.getUsername(), -1, Integer::sum);
        } catch (PlayerWonException pwe) {
            client.write("\n............Congrats!!!.........\n.....You guessed the word" + pwe);
            scores.merge(client.getUsername(), 3, Integer::sum);
            return true;
        } catch (IOException e) {
            throw new LostPlayerException(client.getUsername());
        } catch (GameException e) {
            e.printStackTrace();
        }

        return false;
    }

    private Boolean botGuess() {
        String word = game.getRandomWord();
        try {
            game.wrongGuess(word);
            System.out.println("Wrong word");
        } catch (PlayerWonException e) {
            System.out.println("THE BOT WON OMG!");
        } catch (GameException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Map<String, Integer> getScores() {
        return scores;
    }

    public Boolean reconnect(String username, Socket socket) throws IOException {
        for (Client c : team1.getClients()) {
            if (c.getUsername().equals(username)) {
                c.setSocket(socket);
                c.write("You were reconnected to the game");
                return true;
            }
        }

        for (Client c : team2.getClients()) {
            if (c.getUsername().equals(username)) {
                c.setSocket(socket);
                c.write("You were reconnected to the game");
                return true;
            }
        }

        return false;
    }

    @Override
    public void run() {
        int firstGuessing = 1;

        for (int n = 0; n < NUM_ROUNDS; n++) {
            currentTeam = firstGuessing;
            game.generateWord();
            Boolean guessed = false;
            Client player = new Client();

            while (!guessed) {
                try {
                    player = nextPlayer();
                    for (Client c : team1.getClients()) {
                        if (currentTeam != 2 || !c.getUsername().equals(player.getUsername())) {
                            c.write("Wainting for " + player.getUsername() + " from team " + currentTeam
                                    + " to guess...");
                        }
                    }

                    for (Client c : team2.getClients()) {
                        if (currentTeam != 1 || !c.getUsername().equals(player.getUsername())) {
                            c.write("Wainting for " + player.getUsername() + " from team " + currentTeam
                                    + " to guess...");
                        }
                    }

                    guessed = guess(player);
                    if (guessed)
                        break;

                    for (Client c : team1.getClients()) {
                        if (currentTeam != 2 || !c.getUsername().equals(player.getUsername())) {
                            c.write(player.getUsername() + " guessed " + game.getLastAnswer()
                                    + " but that was not the answer");
                        }
                    }

                    for (Client c : team2.getClients()) {
                        if (currentTeam != 1 || !c.getUsername().equals(player.getUsername())) {
                            c.write(player.getUsername() + " guessed " + game.getLastAnswer()
                                    + " but that was not the answer");
                        }
                    }

                } catch (LostPlayerException e) {
                    System.out.println(e);
                    guessed = botGuess();
                } catch (GameException e) {
                    e.printStackTrace();
                }
            }

            for (Client c : team1.getClients()) {
                c.write(player.getUsername() + " guessed the answer: " + game.getLastAnswer());
                if (n != NUM_ROUNDS - 1)
                    c.write("Next round starting...");
            }

            for (Client c : team2.getClients()) {
                c.write(player.getUsername() + " guessed the answer: " + game.getLastAnswer());
                if (n != NUM_ROUNDS - 1)
                    c.write("Next round starting...");
            }

            if (currentTeam == 1)
                scoreTeam1++;
            else
                scoreTeam2++;
            firstGuessing = 3 - firstGuessing;
        }

        for(Client client : team1.getClients()){
            if(scoreTeam1 > scoreTeam2) scores.merge(client.getUsername(), 10, Integer::sum);
            else if(scoreTeam1 == scoreTeam2) scores.merge(client.getUsername(), 5, Integer::sum);
        }

        for(Client client : team2.getClients()){
            if(scoreTeam2 > scoreTeam1) scores.merge(client.getUsername(), 10, Integer::sum);
            else if(scoreTeam2 == scoreTeam1) scores.merge(client.getUsername(), 5, Integer::sum);
        }
    }
}
