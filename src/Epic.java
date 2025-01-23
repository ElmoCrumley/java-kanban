import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<String> subTasksList;
    int backlogLevel = 0;

    public Epic(String name, String description, int id) {
        super(name, description, id);
    }

    public ArrayList<String> getSubTasksList() {
        return subTasksList;
    }
}
