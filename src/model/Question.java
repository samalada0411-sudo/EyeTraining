package model;

public class Question {
    private int id;
    private String questionText;
    private String category;
    private int weight;
    private String options;

    public Question() {}

    public Question(String questionText, String category, int weight, String options) {
        this.questionText = questionText;
        this.category = category;
        this.weight = weight;
        this.options = options;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }

    public String[] getOptionsArray() {
        if (options == null || options.isEmpty()) return null;
        return options.split(",");
    }
}