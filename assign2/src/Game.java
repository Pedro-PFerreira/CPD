import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Game {
    private ArrayList<String> words;
    private String answer;
    private String lastAnswer;

    public Game() {
        words = new ArrayList<>();
        fillWords();
    }

    public void wrongGuess(String word) throws GameException{
        System.out.println("Answer to the game is \"" + answer + "\" and \"" + word + "\" was given");
        lastAnswer = word;
        if(word.toLowerCase().equals(answer.toLowerCase())){
            throw new PlayerWonException(word);
        }
    }

    private void fillWords(){
        BufferedReader reader;

		try {
			reader = new BufferedReader(new FileReader("resources/animals.txt"));
			String line = reader.readLine();

			while (line != null) {
				words.add(line);
                line = reader.readLine();
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void generateWord(){
        answer = words.get(new Random().nextInt(words.size()));
    }

    public String getRandomWord(){
        return words.get(new Random().nextInt(words.size()));
    }

    public String getLastAnswer(){
        return lastAnswer;
    }
}
