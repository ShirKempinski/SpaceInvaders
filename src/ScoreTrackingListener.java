/**
 * ScoreTrackingListener update the game's score counter when blocks are being hit and removed.
 */
public class ScoreTrackingListener implements HitListener {

    private Counter currentScore;

    /**
     * constructor.
     * @param scoreCounter the current score counter.
     */
    public ScoreTrackingListener(Counter scoreCounter) {
        this.currentScore = scoreCounter;
    }

    @Override
    public void hitEvent(Block beingHit, Ball hitter) {
        if (hitter.getVelocity().getAngle() == 0) {
            this.currentScore.increase(100);
        }
    }
}