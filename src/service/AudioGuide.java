package service;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class AudioGuide {

    private String os;
    private Process currentProcess;
    private AtomicBoolean isSpeaking;

    public AudioGuide() {
        this.os = System.getProperty("os.name").toLowerCase();
        this.isSpeaking = new AtomicBoolean(false);
        System.out.println("AudioGuide инициализирован для ОС: " + os);
    }

    public void speak(String text) {
        if (text == null || text.isEmpty()) return;

        // Останавливаем предыдущую речь
        stop();

        System.out.println("[Голос]: " + text);

        try {
            if (os.contains("win")) {
                // Windows - PowerShell
                String command = "powershell -Command \"Add-Type -AssemblyName System.Speech; " +
                        "$speak = New-Object System.Speech.Synthesis.SpeechSynthesizer; " +
                        "$speak.Speak('" + escapeText(text) + "'); " +
                        "while ($speak.State -ne 'Ready') { Start-Sleep -Milliseconds 100 }\"";
                currentProcess = Runtime.getRuntime().exec(command);

                // Ждём завершения в отдельном потоке
                final Process process = currentProcess;
                new Thread(() -> {
                    try {
                        if (process != null) {
                            isSpeaking.set(true);
                            process.waitFor();
                            isSpeaking.set(false);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        if (process != null && process.isAlive()) {
                            process.destroy();
                        }
                    }
                }).start();

            } else if (os.contains("mac")) {
                // Mac OS
                currentProcess = Runtime.getRuntime().exec(new String[]{"say", text});
                final Process process = currentProcess;
                new Thread(() -> {
                    try {
                        if (process != null) {
                            isSpeaking.set(true);
                            process.waitFor();
                            isSpeaking.set(false);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        if (process != null && process.isAlive()) {
                            process.destroy();
                        }
                    }
                }).start();

            } else if (os.contains("nix") || os.contains("nux")) {
                // Linux
                currentProcess = Runtime.getRuntime().exec(new String[]{"espeak", text});
                final Process process = currentProcess;
                new Thread(() -> {
                    try {
                        if (process != null) {
                            isSpeaking.set(true);
                            process.waitFor();
                            isSpeaking.set(false);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        if (process != null && process.isAlive()) {
                            process.destroy();
                        }
                    }
                }).start();

            } else {
                System.out.println("[Голос (не поддерживается)]: " + text);
            }
        } catch (IOException e) {
            System.out.println("[Голос (ошибка)]: " + text);
        }
    }

    public void stop() {
        if (currentProcess != null && currentProcess.isAlive()) {
            currentProcess.destroy();
            currentProcess = null;
        }
        isSpeaking.set(false);
    }

    public boolean isSpeaking() {
        return isSpeaking.get();
    }

    private String escapeText(String text) {
        return text.replace("'", "''").replace("\"", "\\\"");
    }

    public void close() {
        stop();
        System.out.println("[Голос]: ресурсы освобождены");
    }
}