package model;

public class ProgramExercise {
    private int id;
    private int programId;
    private int exerciseId;
    private int durationSec;
    private int repeats;
    private int orderNum;

    public ProgramExercise() {}

    public ProgramExercise(int programId, int exerciseId, int durationSec, int repeats, int orderNum) {
        this.programId = programId;
        this.exerciseId = exerciseId;
        this.durationSec = durationSec;
        this.repeats = repeats;
        this.orderNum = orderNum;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProgramId() { return programId; }
    public void setProgramId(int programId) { this.programId = programId; }

    public int getExerciseId() { return exerciseId; }
    public void setExerciseId(int exerciseId) { this.exerciseId = exerciseId; }

    public int getDurationSec() { return durationSec; }
    public void setDurationSec(int durationSec) { this.durationSec = durationSec; }

    public int getRepeats() { return repeats; }
    public void setRepeats(int repeats) { this.repeats = repeats; }

    public int getOrderNum() { return orderNum; }
    public void setOrderNum(int orderNum) { this.orderNum = orderNum; }
}