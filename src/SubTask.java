public class SubTask extends Task {

    private String epicsName;

    public SubTask(String name, String description, int id) {
        super(name, description, id);
    }

    public String getEpicsName() {
        return epicsName;
    }

    public void setEpicsName(String epicsName) {
        this.epicsName = epicsName;
    }
}
