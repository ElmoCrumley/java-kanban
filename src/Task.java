import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private Object status;

    public Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = Status.NEW.name();
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public Object getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Task otherTask = (Task) obj;
        return Objects.equals(name, otherTask.name) && // проверяем все поля
                Objects.equals(description, otherTask.description) && // нужно логическое «и»
                (id == otherTask.id) &&
                Objects.equals(status, otherTask.status); // примитивы сравниваем через ==
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (name != null) {
            hash = hash + name.hashCode();
        }
        hash = hash * 31;

        if (description != null) {
            hash = hash + description.hashCode();
        }
        return hash;
    }

}

