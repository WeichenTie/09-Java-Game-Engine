package game_engine.engine.camera;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class PerspectiveCamera extends Camera {
    private Vector3f position;
    private Vector3f direction;
    private Vector3f right;
    private Vector3f up;
    private Vector3f angles = new Vector3f(); // Pitch, yaw, roll
    public PerspectiveCamera(Vector3f position, Vector3f direction, float angle) {
        this.position = position;
        this.direction = direction.normalize();
        this.angles.x = Math.asin(-direction.y) / (float)Math.PI * 180.0f;
        this.angles.y = Math.atan2(-direction.x, direction.z) / (float)Math.PI * 180.0f;
        this.angles.z = angle / (float)Math.PI * 180;

        UpdateViewMatrix();
    }
    
    private void UpdateViewMatrix() {
        direction.normalize();
        right = new Vector3f(direction).cross(0, 1, 0).normalize();
        up = new Vector3f(right).cross(direction).normalize();
        //System.out.println(direction);
    }
    

    public void translate(Vector3f translation) {
        Vector3f dx = new Vector3f(right).mul(translation.x);
        Vector3f dy = new Vector3f(0,1,0).mul(translation.y);
        Vector3f dz = new Vector3f(direction).mul(translation.z);
        position = new Vector3f(position).add(dx).add(dy).add(dz);
       // System.out.println(direction);
    }

    @Override
    public Matrix4f getViewMatrix() {
        Vector3f target = new Vector3f(position).add(direction);
        return new Matrix4f().lookAt(position, target, up);
    }

    @Override
    public void rotate(Vector3f rotation) {
        angles.x += rotation.x;
        angles.y += rotation.y;
        angles.z += rotation.z;

        if (angles.x > 89.0f) {
            angles.x = 89.0f;
        }

        if (angles.x < -89.0f) {
            angles.x = -89.0f;
        }

        Matrix4f rotationMat = new Matrix4f()
                                    .rotate(Math.toRadians(angles.y), new Vector3f(0,1,0))
                                    .rotate(Math.toRadians(angles.x), new Vector3f(1,0,0))
                                    .rotate(Math.toRadians(angles.z), new Vector3f(0,0,1));
        Vector4f newDir = new Vector4f(0,0,1,0).mul(rotationMat);
        direction = new Vector3f(newDir.x, newDir.y, newDir.z);
        UpdateViewMatrix();
    }
}
