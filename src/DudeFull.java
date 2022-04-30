import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DudeFull implements AnimationEntity {
    private String id;
    private Point position;
    private List<PImage> images;
    private int resourceLimit;
    private int actionPeriod;
    private int animationPeriod;
    private int resourceCount;
    private int imageIndex;

    public DudeFull(String id, Point position, int actionPeriod, int resourceLimit, int animationPeriod, List<PImage> images) {
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
    public void setPosition(Point pos) {
        this.position = pos;
    }

    @Override
    public int getAnimationPeriod() {
        return this.animationPeriod;
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

    private boolean adjacent(Point p1, Point p2) {
        return (p1.x == p2.x && Math.abs(p1.y - p2.y) == 1) || (p1.y == p2.y
                && Math.abs(p1.x - p2.x) == 1);
    }

    private boolean moveToFull(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (adjacent(this.position, target.getPosition())) {
            return true;
        }
        else {
            Point nextPos = this.nextPositionDude(world, target.getPosition());

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

    public void transformFull(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        DudeNotFull miner = new DudeNotFull(this.id,
                this.position, this.actionPeriod,
                this.animationPeriod,
                this.resourceLimit,
                this.images);

        world.removeEntity(world, this);
        scheduler.unscheduleAllEvents(scheduler, this);

        world.addEntity(world, miner);
        scheduler.scheduleActions(miner, scheduler, world, imageStore);
    }

    private Point nextPositionDude(
            WorldModel world, Point destPos)
    {
        int horiz = Integer.signum(destPos.x - this.position.x);
        Point newPos = new Point(this.position.x + horiz, this.position.y);

        if (horiz == 0 || world.isOccupied(world, newPos) && !(world.getOccupancyCell(world, newPos) instanceof Stump)) {
            int vert = Integer.signum(destPos.y - this.position.y);
            newPos = new Point(this.position.x, this.position.y + vert);

            if (vert == 0 || world.isOccupied(world, newPos) &&  !(world.getOccupancyCell(world, newPos) instanceof Stump)) {
                newPos = this.position;
            }
        }

        return newPos;
    }

    @Override
    public int getActionPeriod() {
        return this.actionPeriod;
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
    public void execute(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget =
                world.findNearest(position, new ArrayList<>(Arrays.asList(House.class)));

        if (fullTarget.isPresent() && moveToFull(world,
                fullTarget.get(), scheduler))
        {
            transformFull(world, scheduler, imageStore);
        }
        else {
            scheduler.scheduleEvent(scheduler, this,
                    new ActivityAction(this, world, imageStore),
                    actionPeriod);
        }
    }
}