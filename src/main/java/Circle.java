import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

import static parameters.Parameters.*;
import static processing.core.PApplet.*;
import static processing.core.PConstants.PI;

public class Circle {
    private static PApplet pApplet;
    PVector position, former;
    float radius;
    ArrayList<Circle> linked;
    int index;
    boolean hasMoved;

    Circle(PVector p, float r, int i) {
        position = p;
        radius = r;
        former = null;
        linked = new ArrayList<>();
        index = i;
        hasMoved = false;
    }

    public static void setPApplet(PApplet pApplet) {
        Circle.pApplet = pApplet;
    }

    void render() {
        if (hasMoved) {
            pApplet.stroke(STROKE_COLOR.red(), STROKE_COLOR.green(), STROKE_COLOR.blue(), MID_ALPHA);
            pApplet.circle(position.x, position.y, .5f * radius);
            hasMoved = false;
        }
        for (Circle d : linked) {
            pApplet.stroke(STROKE_COLOR.red(), STROKE_COLOR.green(), STROKE_COLOR.blue(), MID_ALPHA);
            pApplet.line(position.x, position.y, d.position.x, d.position.y);
            trace(position, d.position, pApplet);
            d.linked.remove(this);
        }
        linked.clear();
        if (former != null) {
            pApplet.stroke(STROKE_COLOR.red(), STROKE_COLOR.green(), STROKE_COLOR.blue(), LOW_ALPHA);
            pApplet.line(former.x, former.y, position.x, position.y);
        }
        former = position.copy();
    }

    void trace(PVector A, PVector B, PApplet pApplet) {
        pApplet.stroke(STROKE_COLOR.red(), STROKE_COLOR.green(), STROKE_COLOR.blue(), HIGH_ALPHA);
        float a = PVector.sub(B, A).heading() + PI / 2f;
        float d = dist(A.x, A.y, B.x, B.y);
        for (int i = 0; i < d / 2.; i++) {
            float t = pApplet.random(1);
            for (float k = 0; k < pApplet.random(TRACE_LOW_FACTOR, TRACE_HIGH_FACTOR) * d;
                 k += 1 + TRACE_GAUSSIAN_FACTOR * pApplet.randomGaussian()) {
                pApplet.point(lerp(A.x, B.x, t) + k * cos(a), lerp(A.y, B.y, t) + k * sin(a));
            }
        }
    }
}
