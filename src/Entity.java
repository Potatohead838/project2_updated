import processing.core.PImage;

import java.util.List;

public interface Entity {
    Point getPosition();
    String getId();
    int getImageIndex();
    void setImageIndex(int imageIndex);
    List<PImage> getImages();
    void setPosition(Point pos);
}
