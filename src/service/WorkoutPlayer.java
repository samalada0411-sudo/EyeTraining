package service;

import controller.MainController;
import javax.swing.Timer;
import java.util.List;

public class WorkoutPlayer {

    private AudioGuide audioGuide;
    private boolean isRunning;
    private Timer timer;
    private int currentExerciseIndex;
    private List<MainController.ExerciseData> exercises;
    private WorkoutListener listener;

    public interface WorkoutListener {
        void onExerciseStart(String name, int order, int total);
        void onExerciseProgress(int secondsLeft, int totalSeconds);
        void onExerciseComplete(String name);
        void onWorkoutComplete();
    }

    public WorkoutPlayer() {
        this.audioGuide = new AudioGuide();
        this.isRunning = false;
    }

    public void startWorkout(List<MainController.ExerciseData> exercises, WorkoutListener listener) {
        this.exercises = exercises;
        this.listener = listener;
        this.currentExerciseIndex = 0;
        this.isRunning = true;

        Timer startDelay = new Timer(500, e -> playNextExercise());
        startDelay.setRepeats(false);
        startDelay.start();
    }

    private void playNextExercise() {
        if (!isRunning || currentExerciseIndex >= exercises.size()) {
            if (listener != null) {
                listener.onWorkoutComplete();
            }
            audioGuide.speak("Поздравляю! Вы завершили тренировку. Отлично поработали!");
            return;
        }

        MainController.ExerciseData ex = exercises.get(currentExerciseIndex);

        if (listener != null) {
            listener.onExerciseStart(ex.name, currentExerciseIndex + 1, exercises.size());
        }

        String instruction = getVoiceInstruction(ex);
        audioGuide.speak(instruction);

        Timer startTimer = new Timer(100, new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (!audioGuide.isSpeaking()) {
                    ((Timer)e.getSource()).stop();
                    startExerciseTimer(ex);
                }
            }
        });
        startTimer.start();
    }

    private void startExerciseTimer(MainController.ExerciseData ex) {
        int duration = ex.duration;
        int[] timeLeft = {duration};
        boolean isFocusExercise = ex.name.equals("Смена фокуса");

        audioGuide.speak("Начинаем!");

        Timer pauseTimer = new Timer(1500, e -> {
            ((Timer)e.getSource()).stop();

            timer = new Timer(1000, ev -> {
                timeLeft[0]--;
                if (listener != null) {
                    listener.onExerciseProgress(timeLeft[0], duration);
                }

                // Для упражнения "Смена фокуса" - подсказки каждые 5 секунд
                if (isFocusExercise) {
                    int remaining = timeLeft[0];
                    int elapsed = duration - remaining;

                    // Каждые 5 секунд (0, 5, 10, 15, 20, 25, 30...)
                    if (elapsed % 5 == 0 && elapsed > 0) {
                        // Чётные 5-секундные интервалы - "Близко", нечётные - "Далеко"
                        int cycle = elapsed / 5;
                        if (cycle % 2 == 1) {
                            audioGuide.speak("Близко");
                        } else {
                            audioGuide.speak("Далеко");
                        }
                    }
                }

                // Стандартные напоминания
                if (timeLeft[0] == 10) {
                    audioGuide.speak("Осталось 10 секунд");
                } else if (timeLeft[0] == 5) {
                    audioGuide.speak("Осталось 5 секунд");
                } else if (timeLeft[0] <= 0) {
                    ((Timer)ev.getSource()).stop();
                    audioGuide.speak("Упражнение закончено. Отлично!");
                    if (listener != null) {
                        listener.onExerciseComplete(ex.name);
                    }
                    currentExerciseIndex++;
                    playNextExercise();
                }
            });
            timer.start();
        });
        pauseTimer.setRepeats(false);
        pauseTimer.start();
    }

    public void stopWorkout() {
        isRunning = false;
        if (timer != null) {
            timer.stop();
        }
        audioGuide.stop();
        audioGuide.speak("Тренировка остановлена");
    }

    private String getVoiceInstruction(MainController.ExerciseData ex) {
        switch (ex.name) {
            case "Пальминг":
                return "Потрите ладони, закройте глаза и накройте их ладонями. Расслабьтесь.";
            case "Круговые движения":
                return "Не поворачивая головы, медленно вращайте глазами по кругу. Сначала 10 раз в одну сторону, затем 10 раз в другую.";
            case "Смена фокуса":
                return "Упражнение смена фокуса. Смотрите на палец в 15 сантиметрах от лица. Через 5 секунд переведите взгляд вдаль. Я буду подсказывать: Близко и Далеко.";
            case "Частое моргание":
                return "Быстро и легко моргайте в течение минуты. Не сжимайте веки.";
            case "Движения по сторонам":
                return "Медленно переводите взгляд вверх, затем вниз. Повторите 10 раз. Затем влево и вправо 10 раз.";
            case "Соляризация":
                return "Закройте глаза и поверните лицо к окну или лампе. Медленно поворачивайте голову.";
            case "Карандашная гимнастика":
                return "Медленно приближайте карандаш к носу, следя глазами, затем отводите обратно. Повторите 10 раз.";
            case "Фиксация на буквах":
                return "Сфокусируйтесь на букве, затем переводите взгляд на соседнюю букву.";
            case "Часы":
                return "Представьте циферблат. Переводите взгляд на каждую цифру по часовой стрелке, затем против.";
            case "Массаж век":
                return "Закройте глаза. Круговыми движениями массируйте веки кончиками пальцев.";
            case "Рисование носом":
                return "Представьте, что кончик носа - кисть. Рисуйте в воздухе круги, восьмёрки.";
            case "Взгляд вдаль":
                return "Смотрите вдаль 30 секунд, затем на ближний предмет 10 секунд. Повторяйте 2 минуты.";
            case "Медленное моргание":
                return "Медленно закройте глаза на 2 секунды, затем откройте. Повторите 10 раз.";
            case "Диагонали":
                return "Переводите взгляд вверх-вправо, затем вниз-влево. Затем вверх-влево и вниз-вправо.";
            case "Восьмёрки":
                return "Водите глазами по траектории знака бесконечности.";
            default:
                return "Начинаем упражнение " + ex.name;
        }
    }

    public void close() {
        if (audioGuide != null) {
            audioGuide.close();
        }
    }
}