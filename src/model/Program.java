package model;

import java.time.LocalDate;

public class Program {
    private int id;
    private int userId;
    private String programType;
    private LocalDate generationDate;
    private boolean isCustom;
    private int totalDuration;

    public Program() {}

    public Program(int userId, String programType, boolean isCustom, int totalDuration) {
        this.userId = userId;
        this.programType = programType;
        this.isCustom = isCustom;
        this.totalDuration = totalDuration;
        this.generationDate = LocalDate.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getProgramType() { return programType; }
    public void setProgramType(String programType) { this.programType = programType; }

    public LocalDate getGenerationDate() { return generationDate; }
    public void setGenerationDate(LocalDate generationDate) { this.generationDate = generationDate; }

    public boolean isCustom() { return isCustom; }
    public void setCustom(boolean custom) { isCustom = custom; }

    public int getTotalDuration() { return totalDuration; }
    public void setTotalDuration(int totalDuration) { this.totalDuration = totalDuration; }
}