import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<String> subTasksList;
    int backlogLevel = 0;

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<String> getEpicsSubTasksList() {
        return subTasksList;
    }

    public void setSubtaskToList(String subTaskName) {
        subTasksList.add(subTaskName);
    }
}
