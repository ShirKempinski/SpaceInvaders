import biuoop.DialogManager;
import biuoop.GUI;
import biuoop.KeyboardSensor;

import java.io.File;
import java.io.IOException;

/**
 * Game flow is in charge of creating the different levels, and moving from one level to the next.
 */
public class GameFlow {

    private GUI gui;
    private KeyboardSensor keyboardSensor;
    private AnimationRunner animationRunner;
    private Counter lives;
    private Counter score;
    private File f;

    /**
     * constructor.
     * @param ar - AnimationRunner.
     * @param ks - KeyboardSensor.
     * @param gui - GUI.
     * @param f - file.
     */
    public GameFlow(GUI gui, AnimationRunner ar, KeyboardSensor ks, File f) {
        this.keyboardSensor = ks;
        this.animationRunner = ar;
        this.score = new Counter(0);
        this.lives = new Counter(3);
        this.f = f;
        this.gui = gui;
    }

    /**
     * run the game levels.
     */
    public void runLevels() {
        HighScoresTable scores = HighScoresTable.loadFromFile(this.f);
        int levelNumber = 1;
        int invadersSpeed = 50;

        while (true) {
            GameLevel level = new GameLevel(levelNumber, invadersSpeed, this.keyboardSensor, this.animationRunner,
                    this.score, this.lives);
            level.initialize();
            // while player have lives.
            while (level.getLives().getValue() > 0 && !level.isLevelDone()) {
                level.playOneTurn();
            }
            //if player have no more lives.
            if (level.getLives().getValue() <= 0) {
                break;
            }
            //move level up.
            levelNumber++;
            //add speed to aliens.
            invadersSpeed += 10;
        }

        KeyPressStoppableAnimation kpsLose = new KeyPressStoppableAnimation(this.keyboardSensor,
                KeyboardSensor.SPACE_KEY, new EndScreen(this.keyboardSensor, this.score.getValue()));
        while (!kpsLose.shouldStop()) {
            this.animationRunner.run(kpsLose);
        }

        if (scores.getRank(this.score.getValue()) <= scores.size() || scores.getHighScores().isEmpty()) {
            DialogManager dialog = this.gui.getDialogManager();
            String name = dialog.showQuestionDialog("Name", "What is your name?", "");
            scores.add(new ScoreInfo(name, this.score.getValue()));
            try {
              scores.save(this.f);
          } catch (IOException e) {
              e.printStackTrace();
          }
        }
        new HighScoresTask(this.keyboardSensor, this.f, this.animationRunner).run();
    }
}