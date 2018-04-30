import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import biuoop.DrawSurface;

/**
 * Block is a collidable rectangle.
 */
public class Block implements Collidable, Sprite, HitNotifier {

    private Rectangle rect;
    private int hits;
    private ArrayList<HitListener> hitListeners;
    private Color stroke;
    private Map<Integer, Fill> fill;
    private boolean isInvader;

    /**
     * constractor.
     *
     * @param rect - the shape of the block
     * @param fills - the block's fillings
     * @param stroke - the block's strok's color.
     * @param hits - counter.
     */
    public Block(Rectangle rect, int hits, Color stroke, Map<Integer, Fill> fills) {
        this.rect = rect;
        this.stroke = stroke;
        this.hits = hits;
        this.hitListeners = new ArrayList<HitListener>();
        this.fill = fills;
    }

    /**
     * constructor.
     * @param x - rect upperLeft
     * @param y - rec upperLeft
     * @param width - w
     * @param height - h
     * @param hits - hits
     * @param stroke - stroke
     * @param fill - fill
     */
    public Block(double x, double y, double width, double height, int hits, Color stroke, Fill fill) {
        this.rect = new Rectangle(new Point(x, y), width, height);
        this.stroke = stroke;
        this.hits = hits;
        this.hitListeners = new ArrayList<HitListener>();
        Map<Integer, Fill> fills = new HashMap<Integer, Fill>();
        fills.put(1, fill);
        this.fill = fills;
        this.isInvader = false;
    }
    /**
     * getCollisionRectangle.
     *
     * @return this rectangle
     */
    public Rectangle getCollisionRectangle() {
        return this.rect;
    }

    /**
     * draw the block on the given DrawSurface.
     *
     * @param surface - to draw on.
     */
    public void drawOn(DrawSurface surface) {
        if (this.fill.get(this.hits).getImage() == null) {
            surface.setColor(this.fill.get(this.hits).getColor());
              surface.fillRectangle((int) this.rect.getUpperLeft().getX(), (int) this.rect.getUpperLeft().getY(),
                    (int) this.rect.getWidth(), (int) this.rect.getHeight());
        } else {
            BufferedImage image = this.fill.get(this.hits).getImage();
            surface.drawImage((int) this.rect.getUpperLeft().getX(), (int) this.rect.getUpperLeft().getY(), image);
        }
        if (this.stroke != null) {
            surface.setColor(this.stroke);
            surface.drawRectangle((int) this.rect.getUpperLeft().getX(), (int) this.rect.getUpperLeft().getY(),
                    (int) this.rect.getWidth(), (int) this.rect.getHeight());
        }
    }

    /**
     * getWidth.
     * @return the rect width
     */
    public int getWidth() {
        return (int) this.rect.getWidth();
    }

    /**
     * Remove this block from the game.
     * @param game the game.
     */
    public void removeFromGame(GameLevel game) {
        game.removeCollidable(this);
        game.removeSprite(this);
    }

    /**
     * hit calculates the new velocity expected after the hit, based on the
     * force the object inflicted on us.
     *
     * @param collisionPoint - the intersecting point of the block and the ball.
     * @param currentVelocity - of the ball.
     * @param hitter - the hitter ball.
     * @return the new velocity.
     */
    public Velocity hit(Ball hitter, Point collisionPoint, Velocity currentVelocity) {
        collisionPoint = new Point(Math.round(collisionPoint.getX()), Math.round(collisionPoint.getY()));
        double newDx = currentVelocity.getDX();
        double newDy = currentVelocity.getDY();

        this.notifyHit(hitter);
        if (this.rect.getLeft().isPointOnSegment(collisionPoint)
                || this.rect.getRight().isPointOnSegment(collisionPoint)) {
            newDx = -newDx;
        }

        if (this.rect.getUp().isPointOnSegment(collisionPoint)
                || this.rect.getDown().isPointOnSegment(collisionPoint)) {
            newDy = -newDy;
        }

        return new Velocity(newDx, newDy);
    }

    /**
     * timePased.
     * @param dt ignored.
     */
    public void timePassed(double dt) {
        return;
    }

    /**
     * Add this block to the collidables and the sprites lists.
     *
     * @param game - the game.
     */
    public void addToGame(GameLevel game) {
        game.addCollidable(this);
        game.addSprite(this);
    }

    @Override
    public void addHitListener(HitListener hl) {
        this.hitListeners.add(hl);
    }

    @Override
    public void removeHitListener(HitListener hl) {
        this.hitListeners.remove(hl);
    }

    /**
     * getHits.
     * @return the block's hits.
     */
    public int getHits() {
        return this.hits;
    }

    /**
     * notifies all of the registered HitListener objects by calling their
     * hitEvent method.
     * @param hitter - the hitter ball.
     */
    public void notifyHit(Ball hitter) {
        // Make a copy of the hitListeners before iterating over them.
        ArrayList<HitListener> listeners = new ArrayList<HitListener>(this.hitListeners);
        // Notify all listeners about a hit event:
        for (HitListener hl : listeners) {
            hl.hitEvent(this, hitter);
        }
    }

    /**
     * move the block by it's rect.
     * @param x - new upperleft x
     * @param y - new upperleft y
     */
    public void moveRect(double x, double y) {
        this.rect = new Rectangle(new Point(x, y), this.getWidth(), this.getHeight());
    }
    /**
     * getUpperLeft.
     * @return ths rect Upper left
     */
    public Point getUpperLeft() {
        return this.rect.getUpperLeft();
    }

    /**
     * getHeight.
     * @return the rect height
     */
    public int getHeight() {
        return (int) this.rect.getHeight();
    }

    /**
     * getLowerRight.
     * @return lower right point
     */
    public Point getLowerRight() {
        return this.rect.getLowerRight();
    }

    /**
     * isInvader.
     * @return bool
     */
    public boolean isInvader() {
        return isInvader;
    }

    /**
     * isInvader.
     * @param bool - bool
     */
    public void setInvader(boolean bool) {
        this.isInvader = bool;
    }
}
