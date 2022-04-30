import processing.core.PImage;

import java.util.List;

public class Stump implements AnimationEntity {
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;

    public Stump(String id, Point position, List<PImage> images) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
    }
    @Override
    public Point getPosition() {
        return this.position;
    }
    @Override
    public void setPosition(Point pos) {
        this.position = pos;
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
    public int getImageIndex() {
        return this.imageIndex;
    }

    @Override
    public void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }

    //weird
    @Override
    public int getAnimationPeriod() {
        return 0;
    }

    //weirder
    @Override
    public int getActionPeriod() {
        return 0;
    }

    //weeirdest
    @Override
    public void execute(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

    }
}