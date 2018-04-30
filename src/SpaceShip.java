import java.awt.Color;

import biuoop.DrawSurface;
import biuoop.KeyboardSensor;

/**
 * Space ship.
 */
public class SpaceShip implements Sprite, Collidable {

        private KeyboardSensor keyboard;
        private Rectangle rect;
        private Color color;
        private int speed;
        private boolean wasHit;
        private long lastShot;
        private boolean okToShoot;

        /**
         * constructor.
         *
         * @param k - the keyboard sensor.
         * @param r - the rectangle.
         * @param c - the passle's color.
         * @param speed - the game speed.
         */
        public SpaceShip(KeyboardSensor k, Rectangle r, Color c, int speed) {
            this.keyboard = k;
            this.rect = r;
            this.color = c;
            this.speed = speed;
            this.wasHit = false;
            this.lastShot = System.currentTimeMillis();
            this.okToShoot = true;
        }

        /**
         * Moves the space ship left.
         * @param dt - time
         */
        public void moveLeft(double dt) {
            if (this.rect.getUpperLeft().getX() <= 20) {
                return;
            }
            this.rect = new Rectangle(new Point(this.rect.getUpperLeft().getX() - this.speed * dt,
                    this.rect.getUpperLeft().getY()), this.rect.getWidth(), this.rect.getHeight());
        }

        /**
         * Moves the space ship right.
         * @param dt - time
         */
        public void moveRight(double dt) {
            if (this.rect.getUpperLeft().getX() + this.rect.getWidth() >= 780) {
                return;
            }
            this.rect = new Rectangle(new Point(this.rect.getUpperLeft().getX() + this.speed * dt,
                    this.rect.getUpperLeft().getY()), this.rect.getWidth(), this.rect.getHeight());    }

        /**
         * check if the "left" or "right" keys are pressed, and if so move it accordingly.
         * @param dt - time
         */
        public void timePassed(double dt) {
            if (this.keyboard.isPressed(KeyboardSensor.LEFT_KEY)) {
                moveLeft(dt);
            }

            if (this.keyboard.isPressed(KeyboardSensor.RIGHT_KEY)) {
                moveRight(dt);
            }

            long now = System.currentTimeMillis();
            if (now - this.lastShot > 350) {
                this.okToShoot = true;
            }
        }

        /**
         * isOkToShoot.
         * @return isOkToShoot
         */
        public boolean isOkToShoot() {
            return this.okToShoot;
        }

        /**
         * updateThatJustShot.
         */
        public void updateThatJustShot() {
            this.lastShot = System.currentTimeMillis();
            this.okToShoot = false;
        }

        /**
         * Draw the space ship.
         *
         * @param d - the surface.
         */
        public void drawOn(DrawSurface d) {
            d.setColor(this.color);
            d.fillRectangle((int) this.rect.getUpperLeft().getX(), (int) this.rect.getUpperLeft().getY(),
                    (int) this.rect.getWidth(), (int) this.rect.getHeight());
            d.setColor(Color.WHITE);
            d.drawRectangle((int) this.rect.getUpperLeft().getX(), (int) this.rect.getUpperLeft().getY(),
                    (int) this.rect.getWidth(), (int) this.rect.getHeight());
        }

        /**
         * get the collision rectangle.
         *
         * @return the collision rectangle
         */
        public Rectangle getCollisionRectangle() {
            return this.rect;
        }

        /**
         * hit.
         *
         * @param collisionPoint  - the intersecting point of the block and the ball.
         * @param currentVelocity - of the ball.
         * @param hitter - the hitting ball.
         * @return the same velocity.
         */
        public Velocity hit(Ball hitter, Point collisionPoint, Velocity currentVelocity) {
            if (this.rect.getUp().isPointOnSegment(collisionPoint)) {
                this.wasHit = true;
            }
            return currentVelocity;
         }

        /**
         * wasHit.
         * @return hit
         */
        public boolean wasHit() {
            return this.wasHit;
        }

        /**
         * Add this space ship to the game.
         *
         * @param g - the game.
         */
        public void addToGame(GameLevel g) {
            g.addCollidable(this);
            g.addSprite(this);
        }
    }
