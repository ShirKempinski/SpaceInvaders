import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import biuoop.DrawSurface;
import biuoop.KeyboardSensor;

/**
 * Game holds the sprites and the collidables, and is in charge of the animation.
 */
public class GameLevel implements Animation {
    private SpriteCollection sprites;
    private GameEnvironment environment;
    private BlockRemover blockRemover;
    private Counter score;
    private Counter lives;
    private Sprite background;
    private Boolean running;
    private AnimationRunner runner;
    private KeyboardSensor keyboard;
    private Invaders invaders;
    private ArrayList<Ball> bullets;
    private int levelNumber;
    private int invadersSpeed;
    private boolean allInvadersDied;
    private SpaceShip spaceShip;
    private Block deathRegion;

    /**
     * constructor.
     * @param levelNumber - the level number.
     * @param invadersSpeed - invaders Speed
     * @param keyboard - the keyboard sensor.
     * @param runner - the animation runner.
     * @param score - current scores
     * @param lives - current lives
     */
    public GameLevel(int levelNumber, int invadersSpeed, KeyboardSensor keyboard, AnimationRunner runner,
            Counter score, Counter lives) {
        this.levelNumber = levelNumber;
        this.invadersSpeed = invadersSpeed;
        this.keyboard = keyboard;
        this.runner = runner;
        this.sprites = new SpriteCollection();
        this.environment = new GameEnvironment();
        this.blockRemover = new BlockRemover(this, new Counter(0));
        this.score = score;
        this.lives = lives;
        this.background = new BackgroundColor(Color.BLACK);
        this.running = true;
        this.bullets = new ArrayList<Ball>();
        this.allInvadersDied = false;
    }

    /**
     * Add a new Collidable to the collidables list.
     *
     * @param c - the collidable
     */
    public void addCollidable(Collidable c) {
        this.environment.addCollidable(c);
    }

    /**
     * Add a new Sprite to the sprites list.
     *
     * @param s - the sprite
     */
    public void addSprite(Sprite s) {
        this.sprites.addSprite(s);
    }

    /**
     * Remove the given collidable from the game.
     * @param c the collidable.
     */
    public void removeCollidable(Collidable c) {
        this.environment.getCollidables().remove(c);
    }

    /**
     * Remove the sprite from the game.
     * @param s the sprite.
     */
    public void removeSprite(Sprite s) {
        if (s instanceof Block) {
            if (((Block) s).isInvader()) {
                this.invaders.removeInvader((Block) s);
            }
        }
        this.sprites.getSprites().remove(s);
    }

    /**
     * Remove the bullet from the game.
     * @param b the bullet.
     */
    public void removeBullet(Ball b) {
        this.sprites.getSprites().remove(b);
    }
    /**
     * getBlockRemover.
     * @return the block remover
     */
    public BlockRemover getBlockRemover() {
        return this.blockRemover;
    }

    /**
     * getScore.
     * @return the scores.
     */
    public Counter getScore() {
        return this.score;
    }

    /**
     * getLives.
     * @return the lives.
     */
    public Counter getLives() {
        return this.lives;
    }

    /**
     * Initialize a new game: create the Blocks and Ball (and Paddle) and add them to the game.
     */
    public void initialize() {

        this.background.addToGame(this);

        HashMap<Integer, Fill> fill = new HashMap<Integer, Fill>();
        fill.put(0, new Fill(Color.BLACK));
        this.deathRegion = new Block(new Rectangle(new Point(0, 20), 800, 1), 0, Color.BLACK, fill);
        this.deathRegion.addToGame(this);
        BallRemover ballRemover = new BallRemover(this);
        this.deathRegion.addHitListener(ballRemover);

        ScoreTrackingListener scoreListener = new ScoreTrackingListener(this.score);
        ScoreIndicator scoreIndicator = new ScoreIndicator(this);
        scoreIndicator.addToGame(this);

        LivesIndicator livesIndicator = new LivesIndicator(this);
        livesIndicator.addToGame(this);

        LevelIndicator levelIndicator = new LevelIndicator(this.levelNumber);
        levelIndicator.addToGame(this);

        setShield();
        setInvaders(scoreListener);
    }

    /**
     * play one turn start the animation loop.
     */
    public void playOneTurn() {
        this.spaceShip = new SpaceShip(this.keyboard, new Rectangle(new Point(360, 570), 100, 10),
                Color.BLACK, 500);
        this.spaceShip.addToGame(this);

        this.runner.run(new CountdownAnimation(2, 3, this.sprites));
        this.running = true;
        this.runner.run(this);
        for (Ball b: this.bullets) {
            b.removeFromGame(this);
        }
        removeCollidable(this.spaceShip);
        this.sprites.getSprites().remove(this.spaceShip);
    }

    @Override
    public void doOneFrame(DrawSurface d, double dt) {
        this.sprites.drawAllOn(d);
        this.sprites.notifyAllTimePassed(dt);
        if (this.keyboard.isPressed("p")) {
            this.runner.run(new KeyPressStoppableAnimation(this.keyboard, KeyboardSensor.SPACE_KEY, new PauseScreen()));
        }
        if (this.invaders.invadersLeft() == 0) {
            this.running = false;
            this.allInvadersDied = true;
        }
        if (this.invaders.isTimeToShoot()) {
            Point shootingPoint = this.invaders.nextShootingPoint();
            int x = (int) shootingPoint.getX();
            int y = (int) shootingPoint.getY();
            Ball bullet = new Ball(x, y, 3, Color.RED, Velocity.fromAngleAndSpeed(180, 100), this.environment);
            bullet.addToGame(this);
            this.bullets.add(bullet);
            this.invaders.updateThatJustShot();
        }
        if (this.keyboard.isPressed(KeyboardSensor.SPACE_KEY) && this.spaceShip.isOkToShoot()) {
            int x = (int) this.spaceShip.getCollisionRectangle().getUpperLeft().getX() + 50;
            Ball bullet = new Ball(x, 560, 3, Color.MAGENTA, Velocity.fromAngleAndSpeed(0, 500), this.environment);
            bullet.addToGame(this);
            this.bullets.add(bullet);
            this.spaceShip.updateThatJustShot();
        }
        if (this.invaders.didHitShield() || this.spaceShip.wasHit()) {
            this.lives.decrease(1);
            this.running = false;
            this.invaders.setMatrix();
        }
    }

    /**
     * set the game's invaders.
     * @param scoreListener scores
     */
    public void setInvaders(ScoreTrackingListener scoreListener) {
        ArrayList<Block> newBlocks = new ArrayList<Block>();
        Fill invaderFill = new Fill("invader.jpg");
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 10; j++) {
                HashMap<Integer, Fill> fills = new HashMap<Integer, Fill>();
                fills.put(1, invaderFill);
                Block b = new Block(new Rectangle(new Point(20 + 60 * j, 50 + i * 50), 40, 30),
                        1, Color.BLACK, fills);
                b.setInvader(true);
                b.addHitListener(this.blockRemover);
                this.blockRemover.getCounter().increase(1);
                b.addHitListener(scoreListener);
                newBlocks.add(b);
            }
        }
        this.invaders = new Invaders(newBlocks, this.invadersSpeed);
        this.invaders.addToGame(this);
    }

    /**
     * set the shields.
     */
    public void setShield() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 35; j++) {
                HashMap<Integer, Fill> fills = new HashMap<Integer, Fill>();
                fills.put(1, new Fill(Color.CYAN));
                Block b = new Block(new Rectangle(new Point(65 + j * 5, 450 + i * 5), 5, 5), 1, null, fills);
                b.addToGame(this);
                b.addHitListener(this.blockRemover);
            }
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 35; j++) {
                HashMap<Integer, Fill> fills = new HashMap<Integer, Fill>();
                fills.put(1, new Fill(Color.CYAN));
                Block b = new Block(new Rectangle(new Point(315 + j * 5, 450 + i * 5), 5, 5), 1, null, fills);
                b.addToGame(this);
                b.addHitListener(this.blockRemover);
            }
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 35; j++) {
                HashMap<Integer, Fill> fills = new HashMap<Integer, Fill>();
                fills.put(1, new Fill(Color.CYAN));
                Block b = new Block(new Rectangle(new Point(565 + j * 5, 450 + i * 5), 5, 5), 1, null, fills);
                b.addToGame(this);
                b.addHitListener(this.blockRemover);
            }
        }
    }

    @Override
    public boolean shouldStop() {
        return !this.running;
    }

    /**
     * isLevelDone.
     * @return allInvadersDied
     */
    public boolean isLevelDone() {
        return this.allInvadersDied;
    }
}