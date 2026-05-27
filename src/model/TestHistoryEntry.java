package model;

public class TestHistoryEntry {
    private int id;
    private int userId;
    private String testDate;
    private int totalScore;
    private String loadLevel;

    public TestHistoryEntry(int id, int userId, String testDate, int totalScore, String loadLevel) {
        this.id = id;
        this.userId = userId;
        this.testDate = testDate;
        this.totalScore = totalScore;
        this.loadLevel = loadLevel;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getTestDate() { return testDate; }
    public int getTotalScore() { return totalScore; }
    public String getLoadLevel() { return loadLevel; }
}