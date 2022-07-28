package game_engine.engine.camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class Camera {
    public abstract Matrix4f getViewMatrix();
    public abstract void translate(Vector3f translation);
    public abstract void rotate(Vector3f rotation);
}
