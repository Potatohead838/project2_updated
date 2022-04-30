public interface ExecutableEntity extends Entity {

    int getActionPeriod();

    void execute(WorldModel world,
                 ImageStore imageStore,
                 EventScheduler scheduler);
}