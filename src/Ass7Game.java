import java.io.File;

import biuoop.GUI;
import biuoop.KeyboardSensor;

/**
 * Ass7Game.
 */
public class Ass7Game {
    /**
     * main.
     * @param args - the levels set file path.
     */
    public static void main(String[] args) {
        GUI gui = new GUI("Space Invaders", 800, 600);
        File scores =  new File("highscores.ser");
        AnimationRunner runner = new AnimationRunner(gui);
        KeyboardSensor k = gui.getKeyboardSensor();


        Menu<Task<Void>> mainMenu = new MenuAnimation<Task<Void>>(k, runner);
        mainMenu.addSelection("s", "Start New Game", new StartTask(gui, k, scores, runner));
        mainMenu.addSelection("h", "High Scores Table", new HighScoresTask(k, scores, runner));
        mainMenu.addSelection("q", "Quit", new QuitTask(gui));

        while (true) {
            runner.run(mainMenu);
            Task<Void> task = mainMenu.getStatus();
            task.run();
        }
    }
}
