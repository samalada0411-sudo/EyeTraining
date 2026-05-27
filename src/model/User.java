package model;

public class User {
    private int id;
    private String name;
    private int age;
    private int workHours;
    private boolean glasses;
    private String eyeDisease;
    private String visionAcuity;

    public User() {}

    public User(String name, int age) {
        this.name = name;
        this.age = age;
        this.workHours = 0;
    }

    public User(String name, int age, int workHours) {
        this.name = name;
        this.age = age;
        this.workHours = workHours;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public int getWorkHours() { return workHours; }
    public void setWorkHours(int workHours) { this.workHours = workHours; }

    public boolean isGlasses() { return glasses; }
    public void setGlasses(boolean glasses) { this.glasses = glasses; }

    public String getEyeDisease() { return eyeDisease; }
    public void setEyeDisease(String eyeDisease) { this.eyeDisease = eyeDisease; }

    public String getVisionAcuity() { return visionAcuity; }
    public void setVisionAcuity(String visionAcuity) { this.visionAcuity = visionAcuity; }
}