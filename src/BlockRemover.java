/**
 * BlockRemover is in charge of removing blocks from the game, as well as keeping count
 * of the number of blocks that remain.
 */
public class BlockRemover implements HitListener {

    private GameLevel game;
    private Counter remainingInvaders;

    /**
     * constructor.
     * @param game - the game.
     * @param remainingInvaders - the invaders.
     */
    public BlockRemover(GameLevel game, Counter remainingInvaders) {
        this.game = game;
        this.remainingInvaders = remainingInvaders;
    }

    /**
     * getCounter.
     * @return the remaining blocks counter
     */
    public Counter getCounter() {
        return this.remainingInvaders;
    }

    // Invaders that are hit should be removed from the game.
    @Override
    public void hitEvent(Block beingHit, Ball hitter) {
        if (hitter.getVelocity().getAngle() == 0) {
            this.game.getBlockRemover().getCounter().decrease(1);
        }
        hitter.removeFromGame(this.game);
        beingHit.removeFromGame(this.game);
        beingHit.removeHitListener(this);
    }
}