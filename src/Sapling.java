import processing.core.PImage;

import java.util.List;
import java.util.Random;

public class Sapling implements EntityHasHealth {
    private String id;
    private Point position;
    private List<PImage> images;
    private int actionPeriod = WorldModel.SAPLING_ACTION_ANIMATION_PERIOD;
    private int animationPeriod = WorldModel.SAPLING_ACTION_ANIMATION_PERIOD;
    private int healthLimit = WorldModel.SAPLING_HEALTH_LIMIT;
    private int health;
    private int imageIndex;


    public Sapling(String id, Point position, List<PImage> images) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.health = 0;
        this.imageIndex = 0;
    }
    public Sapling(String id, Point position, List<PImage> images, int health) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.health = health;
        this.imageIndex = 0;
    }

    @Override
    public void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }

    @Override
    public int getAnimationPeriod() {
        return this.animationPeriod;
    }

    @Override
    public int getActionPeriod() {
        return this.actionPeriod;
    }

    @Override
    public void setPosition(Point pos) {
        this.position = pos;
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
    public int getImageIndex() {
        return this.imageIndex;
    }

    @Override
    public List<PImage> getImages() {
        return this.images;
    }

    private int getNumFromRange(int max, int min)
    {
        Random rand = new Random();
        return min + rand.nextInt(
                max
                        - min);
    }

    private boolean transformSapling(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (this.health <= 0) {
            Stump stump = new Stump(this.id,
                    this.position,
                    imageStore.getImageList(imageStore, world.STUMP_KEY));

            world.removeEntity(world, this);
            scheduler.unscheduleAllEvents(scheduler, this);

            world.addEntity(world, stump);
            scheduler.scheduleActions(stump, scheduler, world, imageStore);

            return true;
        }
        else if (this.health >= this.healthLimit)
        {
            Tree tree = new Tree("tree_" + this.id,
                    this.position,
                    getNumFromRange(world.TREE_ACTION_MAX, world.TREE_ACTION_MIN),
                    getNumFromRange(world.TREE_ANIMATION_MAX, world.TREE_ANIMATION_MIN),
                    getNumFromRange(world.TREE_HEALTH_MAX, world.TREE_HEALTH_MIN),
                    imageStore.getImageList(imageStore, world.TREE_KEY));

            world.removeEntity(world, this);
            scheduler.unscheduleAllEvents(scheduler, this);

            world.addEntity(world, tree);
            scheduler.scheduleActions(tree, scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    @Override
    public void execute(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        this.health++;
        if (!transformSapling(world, scheduler, imageStore))
        {
            scheduler.scheduleEvent(scheduler, this,
                    new ActivityAction(this, world, imageStore),
                    actionPeriod);
        }
    }

    @Override
    public int getHealth() {
        return this.health;
    }

    @Override
    public void setHealth(int health) {
        this.health = health;
    }
}