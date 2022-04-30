import processing.core.PImage;

import java.util.List;

public class Obstacle implements AnimationEntity{
    private String id;
    private Point position;
    private List<PImage> images;
    private int animationPeriod;
    private int imageIndex;

    public Obstacle(String id, Point position, int animationPeriod, List<PImage> images) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.animationPeriod = animationPeriod;
        this.imageIndex = 0;
    }
    @Override
    public Point getPosition() {
        return this.position;
    }
    @Override
    public int getImageIndex() {
        return this.imageIndex;
    }

    @Override
    public void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public List<PImage> getImages() {
        return this.images;
    }

    @Override
    public void setPosition(Point pos) {
        this.position = pos;
    }

    @Override
    public int getAnimationPeriod() {
        return animationPeriod;
    }

    // weird
    @Override
    public int getActionPeriod() {
        return 0;
    }

    //weird
    @Override
    public void execute(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        //do nothing
    }
}