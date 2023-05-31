public class GameException extends Exception{
}

class PlayerWonException extends GameException {
    String word;

    PlayerWonException(String word) {
        this.word = word;
    }

    @Override
    public String toString() {
        return word;
    }
}

class LostPlayerException extends GameException{

    String username;

    LostPlayerException(String username){
        this.username = username;
        this.toString();
    }

    @Override
    public String toString(){
        return "Player" + username + " was lost!" + " Reconnecting him/her to the game....";
    }
}   
