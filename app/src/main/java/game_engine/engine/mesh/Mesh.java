package game_engine.engine.mesh;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;


class MeshVertex {
    Vector3f position;
    Vector3f normal;
    Vector3f color;
    Vector2f texUV;
}

class Triangle {
    float[] indices;

    public Triangle(int a, int b, int c) {

    }
}

public class Mesh {
    private List<MeshVertex> vertices = new ArrayList<>();
    private List<Integer> indices = new ArrayList<>();

    public void loadVerticesFromFile() {

    }

    public void addVertex() {

    }


}
