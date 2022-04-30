public class AnimationAction implements Action {

    private AnimationEntity entity;
    private int repeatCount;

    public AnimationAction(
            AnimationEntity entity,
            int repeatCount)
    {
        this.entity = entity;
        this.repeatCount = repeatCount;
    }

    private void nextImage(Entity entity) {
        entity.setImageIndex((entity.getImageIndex() + 1) % entity.getImages().size());
    }

    @Override
    public void execute(EventScheduler scheduler) {
        nextImage(this.entity);

        if (this.repeatCount != 1) {
            scheduler.scheduleEvent(scheduler, this.entity,
                    new AnimationAction(this.entity,
                            Math.max(this.repeatCount - 1,
                                    0)),
                    scheduler.getAnimationPeriod(this.entity));
        }
    }
}