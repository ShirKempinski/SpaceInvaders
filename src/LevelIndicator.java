import java.awt.Color;

import biuoop.DrawSurface;

/**
 * Level indicator is in charge of displaying the current level.
 */
public class LevelIndicator implements Sprite {

    private int levelNumber;

    /**
     * constructor.
     * @param levelNumber - the level's number.
     */
    public LevelIndicator(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    @Override
    public void drawOn(DrawSurface d) {
        d.setColor(Color.BLACK);
        d.drawText(620, 15, "Level Number " + this.levelNumber, 14);
    }

    @Override
    public void timePassed(double dt) {
        return;
    }

    @Override
    public void addToGame(GameLevel game) {
        game.addSprite(this);
    }
}