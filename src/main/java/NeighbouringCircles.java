import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

import static parameters.Parameters.*;
import static save.SaveUtil.saveSketch;

public class NeighbouringCircles extends PApplet {
    private Collection collection;

    public static void main(String[] args) {
        PApplet.main(NeighbouringCircles.class);
    }

    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
        randomSeed(SEED);
    }

    @Override
    public void setup() {
        background(BACKGROUND_COLOR.red(), BACKGROUND_COLOR.green(), BACKGROUND_COLOR.blue());
        fill(FILL_COLOR.red(), FILL_COLOR.green(), FILL_COLOR.blue(), FILL_COLOR.alpha());
        noStroke();
        rect(MAX_SIZE / 2, MAX_SIZE / 2, width - MAX_SIZE, height - MAX_SIZE);
        noFill();

        collection = new Collection();
        Collection.setPApplet(this);
        Circle.setPApplet(this);
    }

    @Override
    public void draw() {
        while (collection.move()) {
            // Nothing else to do
        }
        collection.render();
        if (collection.circles.size() == 0) {
            collection.add(
                    new Circle(PVector.random2D(this).div(10).add(width / 2f, height / 2f),
                            randomMod(), collection.circles.size()));
        } else {
            ArrayList<Circle> circles = new ArrayList<>();
            circles.add(collection.circles.get(0));
            int n = collection.countNeighbours(0);

            for (int i = 1; i < collection.circles.size(); i++) {
                int m = collection.countNeighbours(i);
                if (m < n) {
                    circles.clear();
                    circles.add(collection.circles.get(i));
                    n = m;
                } else {
                    if (m == n) {
                        circles.add(collection.circles.get(i));
                    }
                }
            }
            Circle cir = circles.get(floor(random(circles.size())));
            float r = randomMod();
            float a = collection.getAngle(cir) + random(-ANGULAR_DEVIATION, ANGULAR_DEVIATION);
            collection.add(
                    new Circle(PVector.fromAngle(a).mult(cir.radius + r).add(cir.position),
                            r, collection.circles.size()));
        }
        if (collection.circles.size() >= TARGET_COLLECTION_SIZE) {
            noLoop();
            saveSketch(this);
        }
    }

    private int randomMod() {
        return floor(MIN_SIZE + (MAX_SIZE - MIN_SIZE) * random(random(1)));
    }
}
