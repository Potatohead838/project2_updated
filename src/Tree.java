import processing.core.PImage;

import java.util.List;

public class Tree implements EntityHasHealth {
    private String id;
    private Point position;
    private List<PImage> images;
    private int actionPeriod;
    private int animationPeriod;
    private int health;
    private int imageIndex;

    public Tree(String id, Point position, int actionPeriod, int animationPeriod, int health, List<PImage> images) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.health = health;
        this.imageIndex = 0;
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
    public void setPosition(Point pos) {
        this.position = pos;
    }
    @Override
    public int getActionPeriod() {
        return this.actionPeriod;
    }
    @Override
    public void setHealth(int health) {
        this.health = health;
    }

    public boolean transformTree(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (this.health <= 0) {
            AnimationEntity stump = new Stump(this.id,
                    this.position,
                    imageStore.getImageList(imageStore, world.STUMP_KEY));

            world.removeEntity(world, this);
            scheduler.unscheduleAllEvents(scheduler, this);

            world.addEntity(world, stump);
            scheduler.scheduleActions(stump, scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    @Override
    public Point getPosition() {
        return this.position;
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
    public void execute(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        if (!transformTree(world, scheduler, imageStore)) {

            scheduler.scheduleEvent(scheduler, this,
                    new ActivityAction(this, world, imageStore),
                    this.actionPeriod);
        }
    }

    @Override
    public int getAnimationPeriod() {
        return this.animationPeriod;
    }

    @Override
    public int getHealth() {
        return this.health;
    }
}