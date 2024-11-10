import processing.core.PApplet;
import processing.core.PVector;
import processing.data.IntList;

import java.util.ArrayList;

import static parameters.Parameters.*;
import static processing.core.PApplet.*;

public class Collection {
    private static PApplet pApplet;

    ArrayList<Circle> circles;
    IntList[][] cells;
    float cellSize;

    Collection() {
        circles = new ArrayList<>();
        cellSize = MAX_SIZE;
        cells = new IntList[ceil(WIDTH / cellSize)][ceil(HEIGHT / cellSize)];
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                cells[i][j] = new IntList();
            }
        }
    }

    public static void setPApplet(PApplet pApplet) {
        Collection.pApplet = pApplet;
    }

    public void add(Circle c) {
        circles.add(c);
    }

    public void populateCells() {
        for (IntList[] cell : cells) {
            for (IntList intList : cell) {
                intList.clear();
            }
        }

        for (Circle c : circles) {
            cells[max(0, min(cells.length - 1, floor(c.position.x / cellSize)))]
                    [max(0, min(cells[0].length - 1, floor(c.position.y / cellSize)))].append(c.index);
        }
    }

    private int[] getOverlapping() {
        for (Circle c : circles) {
            for (int i = max(0, floor(c.position.x / cellSize) - 1);
                 i < min(cells.length, floor(c.position.x / cellSize) + 2); i++) {
                for (int j = max(0, floor(c.position.y / cellSize) - 1);
                     j < min(cells[0].length, floor(c.position.y / cellSize) + 2); j++) {
                    for (int ind : cells[i][j]) {
                        if (ind != c.index) {
                            Circle d = circles.get(ind);
                            if (PVector.sub(c.position, d.position).magSq() < sq(c.radius + d.radius)) {
                                if (!c.linked.contains(d)) {
                                    c.linked.add(d);
                                    d.linked.add(c);
                                    c.hasMoved = true;
                                    d.hasMoved = true;
                                }
                                return new int[]{c.index, d.index};
                            }
                        }
                    }
                }
            }
        }
        return new int[]{-1, -1};
    }

    public boolean move() {
        populateCells();
        int[] indexes = getOverlapping();
        if (indexes[0] == -1) {
            return false;
        }
        Circle c1 = circles.get(indexes[0]);
        Circle c2 = circles.get(indexes[1]);
        PVector v = PVector.sub(c1.position, c2.position).div(2);
        v.limit(1);
        PVector v1 = v.copy().rotate(pApplet.random(-ANGULAR_DEVIATION, ANGULAR_DEVIATION));
        PVector v2 = v.rotate(pApplet.random(-ANGULAR_DEVIATION, ANGULAR_DEVIATION));
        c1.position.add(v1);
        c2.position.sub(v2);

        return true;
    }

    public void render() {
        for (Circle c : circles) {
            c.render();
        }
    }

    public int countNeighbours(int index) {
        Circle c = circles.get(index);
        if (c.position.x < 4 * MAX_SIZE
                || c.position.x > WIDTH - 4 * MAX_SIZE
                || c.position.y < 4 * MAX_SIZE
                || c.position.y > HEIGHT - 4 * MAX_SIZE) {
            return MAX_INT;
        }
        int counter = 0;
        for (int i = max(0, floor(c.position.x / cellSize) - 2);
             i < min(cells.length, floor(c.position.x / cellSize) + 3); i++) {
            for (int j = max(0, floor(c.position.y / cellSize) - 2);
                 j < min(cells[0].length, floor(c.position.y / cellSize) + 3); j++) {
                for (int ind : cells[i][j]) {
                    Circle d = circles.get(ind);
                    if (PVector.sub(c.position, d.position).magSq() < sq(2 * MAX_SIZE)) {
                        counter++;
                    }
                }
            }
        }
        return counter;
    }

    float getAngle(Circle c) {
        PVector vector = new PVector(0, 0);
        for (int i = max(0, floor(c.position.x / cellSize) - 2);
             i < min(cells.length, floor(c.position.x / cellSize) + 3); i++) {
            for (int j = max(0, floor(c.position.y / cellSize) - 2);
                 j < min(cells[0].length, floor(c.position.y / cellSize) + 3); j++) {
                for (int ind : cells[i][j]) {
                    Circle d = circles.get(ind);
                    if (PVector.sub(c.position, d.position).magSq() < sq(2 * MAX_SIZE)) {
                        vector.add(PVector.sub(c.position, d.position));
                    }
                }
            }
        }
        return vector.heading();
    }
}
