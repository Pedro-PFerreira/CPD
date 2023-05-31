import java.io.IOException;
import java.util.TimerTask;

public class DataTimer extends TimerTask {
    private Data data;

    public DataTimer(Data data) {
        this.data = data;
    }

    public void run() {
        try {
            data.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
