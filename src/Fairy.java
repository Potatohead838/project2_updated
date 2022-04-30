import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Fairy implements AnimationEntity {
    private String id;
    private Point position;
    private List<PImage> images;
    private int actionPeriod;
    private int animationPeriod;
    private int imageIndex;

    public Fairy(String id, Point position, int actionPeriod, int animationPeriod, List<PImage> images) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.imageIndex = 0;
    }

    @Override
    public int getAnimationPeriod() {
        return this.animationPeriod;
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
    public Point getPosition() {
        return this.position;
    }
    @Override
    public int getActionPeriod() {
        return this.actionPeriod;
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



    private Point nextPositionFairy(WorldModel world, Point destPos)
    {
        int horiz = Integer.signum(destPos.x - this.position.x);
        Point newPos = new Point(this.position.x + horiz, this.position.y);

        if (horiz == 0 || world.isOccupied(world, newPos)) {
            int vert = Integer.signum(destPos.y - this.position.y);
            newPos = new Point(this.position.x, this.position.y + vert);

            if (vert == 0 || world.isOccupied(world, newPos)) {
                newPos = this.position;
            }
        }

        return newPos;
    }

    private boolean moveToFairy(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (adjacent(this.position, target.getPosition())) {
            world.removeEntity(world, target);
            scheduler.unscheduleAllEvents(scheduler, target);
            return true;
        }
        else {
            Point nextPos = nextPositionFairy(world, target.getPosition());

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

    @Override
    public void execute(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyTarget =
                world.findNearest(position, new ArrayList<>(Arrays.asList(Stump.class)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().getPosition();

            if (moveToFairy(world, fairyTarget.get(), scheduler)) {
                AnimationEntity sapling = new Sapling("sapling_" + id, tgtPos,
                        imageStore.getImageList(imageStore, world.SAPLING_KEY));

                world.addEntity(world, sapling);
                scheduler.scheduleActions(sapling, scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(scheduler, this,
                new ActivityAction(this, world, imageStore),
                this.actionPeriod);
    }
}