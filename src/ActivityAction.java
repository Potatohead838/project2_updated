import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class ActivityAction implements Action {
    private ExecutableEntity entity;
    private WorldModel world;
    private ImageStore imageStore;

    public ActivityAction(
            ExecutableEntity entity,
            WorldModel world,
            ImageStore imageStore)
    {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
    }

    @Override
    public void execute(EventScheduler scheduler) {
        entity.execute(this.world, this.imageStore, scheduler);

        }

}