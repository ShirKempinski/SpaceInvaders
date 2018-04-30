import java.util.ArrayList;

import biuoop.DrawSurface;

/**
 * the Invaders are a matrix of shooting blocks.
 */
public class Invaders implements Sprite {

    private Block[][] matrix;
    private ArrayList<Block> blocks;
    private boolean moveRight;
    private double mostLeftX;
    private int oneInvaderWidth;
    private double speed;
    private double originalSpeed;
    private boolean hitShield;
    private long lastShot;
    private boolean timeToShoot;

    /**
     * constructor.
     * @param blocks - the invaders.
     * @param speed - the aliens speed
     */
    public Invaders(ArrayList<Block> blocks, double speed) {
        this.moveRight = true;
        this.hitShield = false;
        this.oneInvaderWidth = blocks.get(0).getWidth();
        this.speed = speed;
        this.originalSpeed = speed;
        this.timeToShoot = true;
        this.lastShot = System.currentTimeMillis();
        this.blocks = blocks;

        int currentBlock = 0;
        this.matrix = new Block[5][10];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 10; j++) {
                this.matrix[i][j] = blocks.get(currentBlock);
                currentBlock++;
            }
        }
        this.mostLeftX = this.matrix[0][0].getUpperLeft().getX();
    }

    @Override
    public void drawOn(DrawSurface d) {
        for (Block b: this.blocks) {
            b.drawOn(d);
        }
    }

    @Override
    public void timePassed(double dt) {
        move(dt);
        long now = System.currentTimeMillis();
        if (now - this.lastShot > 500) {
            this.timeToShoot = true;
        }
    }

    /**
     * move the invaders.
     * @param dt - time
     */
    public void move(double dt) {
        double dx = dt * this.speed;
        if (this.moveRight) {
            if (this.mostLeftX + numberOfColumns() * (this.oneInvaderWidth + 20) - 20
                    + dx < 800) {
                // move right
                for (Block b: this.blocks) {
                    b.moveRect(b.getUpperLeft().getX() + dx, b.getUpperLeft().getY());
                }
            } else {
                this.moveDown();
            }
        } else {
            if (this.mostLeftX - dx > 0) {
                // move left
                for (Block b: this.blocks) {
                    b.moveRect(b.getUpperLeft().getX() - dx, b.getUpperLeft().getY());
                }
            } else {
                this.moveDown();
            }
        }
        setMostLeft();
        if (lowestY() >= 450) {
            this.hitShield = true;
        }
    }

    /**
     * move the matrix down and change the movement direction.
     */
    private void moveDown() {
        // move down
        for (Block b: this.blocks) {
            b.moveRect(b.getUpperLeft().getX(), b.getLowerRight().getY() - 20);
        }
        // change right bool to true and increase speed
        this.moveRight = !this.moveRight;
        this.speed *= 1.1;
    }

    @Override
    public void addToGame(GameLevel game) {
        game.addSprite(this);
        for (int i = 0; i < this.matrix.length; i++) {
            for (int j = 0; j < this.matrix[i].length; j++) {
                game.addCollidable(this.matrix[i][j]);
            }
        }
    }

    /**
     * removes the given block from the matrix and the list.
     * @param toRemove - the block
     */
    public void removeInvader(Block toRemove) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 10; j++) {
                if (this.matrix[i][j] != null && this.matrix[i][j].equals(toRemove)) {
                    this.matrix[i][j] = null;
                    this.blocks.remove(toRemove);
                }
            }
        }
    }
    /**
     * the invaders next shooting location.
     * @return the point
     */
    public Point nextShootingPoint() {
        int column = (int) (Math.random() * 9);
        int x = 0;
        int y = 0;
        for (int i = 4; i >= 0; i--) {
            if (this.matrix[i][column] != null) {
                x = (int) this.matrix[i][column].getUpperLeft().getX() + 20;
                y = (int) this.matrix[i][column].getLowerRight().getY() + 10;
                break;
            }
        }
        return new Point(x, y);
    }

    /**
     * getBlocks.
     * @return the blocks.
     */
    public ArrayList<Block> getBlocks() {
        return this.blocks;
    }

    /**
     * didHitShield.
     * @return yes or no
     */
    public boolean didHitShield() {
        return this.hitShield;
    }

    /**
     * setMatrix.
     */
    public void setMatrix() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 10; j++) {
                if (this.matrix[i][j] != null) {
                    this.matrix[i][j].moveRect(20 + 60 * j, 50 + i * 50);
                }
            }
        }
        this.moveRight = true;
        this.hitShield = false;
        this.speed = this.originalSpeed;
    }

    /**
     * isTimeToShoot.
     * @return boolean
     */
    public boolean isTimeToShoot() {
        return this.timeToShoot;
    }

    /**
     * updateThatJustShot.
     */
    public void updateThatJustShot() {
        this.lastShot = System.currentTimeMillis();
        this.timeToShoot = false;
    }

    /**
     * numberOfColumns.
     * @return the number.
     */
    public int numberOfColumns() {
        int left = 9;
        int right = 0;
        outerloop:
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 5; j++) {
                if (this.matrix[j][i] != null) {
                    left = i;
                    break outerloop;
                }
            }
        }
        outerloop:
        for (int i = 9; i >= 0; i--) {
            for (int j = 0; j < 5; j++) {
                if (this.matrix[j][i] != null) {
                    right = i;
                    break outerloop;
                }
            }
        }

        int num = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 5; j++) {
                if (this.matrix[j][i] != null) {
                    num++;
                    break;
                } else if (i > left && i < right && j == 4) {
                    num++;
                }
            }
        }
        return num;
    }

    /**
     * lowestY.
     * @return the lowest y.
     */
    public double lowestY() {
        double y = 0;
        for (Block b: this.blocks) {
            if (b.getLowerRight().getY() > y) {
                y = b.getLowerRight().getY();
            }
        }
        return y;
    }

    /**
     * setMostLeft.
     */
    public void setMostLeft() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 5; j++) {
                if (this.matrix[j][i] != null) {
                    this.mostLeftX = this.matrix[j][i].getUpperLeft().getX();
                    return;
                }
            }
        }
    }
    /**
     * invadersLeft.
     * @return the number of invaders
     */
    public int invadersLeft() {
        return this.blocks.size();
    }
}