package game_engine.engine.lwjgl.vbo;

import static org.lwjgl.opengl.GL40.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class VBO {
    
    private int id;
    
    public VBO () {
        id = glGenBuffers();
    }

    public void bufferData(float[] vertices, int usage) {
        bind();
        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices).flip();
        //FloatBuffer.wrap(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, usage);
        unbind();
    }

    public void bufferData(int[] vertices, int usage) {
        bind();
        IntBuffer buffer = IntBuffer.wrap(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, usage);
        unbind();
    }


    public void bind() {
        glBindBuffer(GL_ARRAY_BUFFER, id);
    }
    public void unbind() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    public void delete() {
        glDeleteBuffers(id);
    }
}
