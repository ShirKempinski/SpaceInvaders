import biuoop.DrawSurface;
import biuoop.KeyboardSensor;

/**
 * End screen.
 */
public class EndScreen implements Animation {

    private boolean stop;
    private Sprite loseScreen;

    /**
     * constructor.
     * @param k - the keyboard sensor.
     * @param finalScore - the player''s score.
     */
    public EndScreen(KeyboardSensor k, int finalScore) {
       this.stop = false;
       this.loseScreen = new LoseScreen(finalScore);
    }

    @Override
    public void doOneFrame(DrawSurface d, double dt) {
        this.loseScreen.drawOn(d);
    }

    @Override
    public boolean shouldStop() {
        return this.stop;
    }
}