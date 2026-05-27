package model;

public class Exercise {
    private int id;
    private String name;
    private String description;
    private int duration;
    private String method;
    private String howToDo;
    private String type;
    private int difficulty;

    public Exercise() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public void setMethod(String method) { this.method = method; }

    public String getHowToDo() { return howToDo; }
    public void setHowToDo(String howToDo) { this.howToDo = howToDo; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getDifficulty() { return difficulty; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
}