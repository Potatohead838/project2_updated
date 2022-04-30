import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DudeNotFull implements AnimationEntity {
    private String id;
    private Point position;
    private List<PImage> images;
    private int resourceLimit;
    private int actionPeriod;
    private int animationPeriod;
    private int resourceCount;
    private int imageIndex;

    public DudeNotFull(String id, Point position, int actionPeriod, int resourceLimit, int animationPeriod, List<PImage> images) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.resourceLimit = resourceLimit;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.resourceCount = 0;
        this.imageIndex = 0;
    }
    @Override
    public void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }

    @Override
    public int getActionPeriod() {
        return this.actionPeriod;
    }

    private boolean adjacent(Point p1, Point p2) {
        return (p1.x == p2.x && Math.abs(p1.y - p2.y) == 1) || (p1.y == p2.y
                && Math.abs(p1.x - p2.x) == 1);
    }
    @Override
    public void setPosition(Point pos) {
        this.position = pos;
    }

    @Override
    public int getAnimationPeriod() {
        return this.animationPeriod;
    }
    @Override
    public int getImageIndex() {
        return this.imageIndex;
    }


    private boolean moveToNotFull (
            WorldModel world,
            EntityHasHealth target,
            EventScheduler scheduler)
    {
        if (adjacent(this.position, target.getPosition())) {
            this.resourceCount += 1;
            target.setHealth(target.getHealth() - 1);
            return true;
        }
        else {
            Point nextPos = nextPositionDude(world, target.getPosition());

            if (!this.position.equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(world, nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(scheduler, occupant.get());
                }

                world.moveEntity(world, this, nextPos);
            }
            return false;
        }
    }

    private Point nextPositionDude(
            WorldModel world, Point destPos)
    {
        int horiz = Integer.signum(destPos.x - this.position.x);
        Point newPos = new Point(this.position.x + horiz, this.position.y);

        if (horiz == 0 || world.isOccupied(world, newPos) && world.getOccupancyCell(world, newPos) instanceof Stump) {
            int vert = Integer.signum(destPos.y - this.position.y);
            newPos = new Point(this.position.x, this.position.y + vert);

            if (vert == 0 || world.isOccupied(world, newPos) &&  world.getOccupancyCell(world, newPos) instanceof Stump) {
                newPos = this.position;
            }
        }

        return newPos;
    }
    public boolean transformNotFull(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (this.resourceCount >= this.resourceLimit) {
            DudeFull miner = new DudeFull(this.id,
                    this.position, this.actionPeriod,
                    this.animationPeriod,
                    this.resourceLimit,
                    this.images);

            world.removeEntity(world, this);
            scheduler.unscheduleAllEvents(scheduler, this);

            world.addEntity(world, miner);
            scheduler.scheduleActions(miner, scheduler, world, imageStore);

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
        Optional<Entity> target =
                world.findNearest(position, new ArrayList<>(Arrays.asList(Tree.class, Sapling.class)));

        if (!target.isPresent() || !moveToNotFull(world,
                (EntityHasHealth) target.get(),
                scheduler)
                || !transformNotFull(world, scheduler, imageStore))
        {
            scheduler.scheduleEvent(scheduler, this,
                    new ActivityAction(this, world, imageStore),
                    actionPeriod);
        }
    }
}