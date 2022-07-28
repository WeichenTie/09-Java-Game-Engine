package game_engine.engine.lwjgl.ebo;
import static org.lwjgl.opengl.GL40.*;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
public class EBO {
    private int id;
    
    public EBO () {
        id = glGenBuffers();
    }

    public void bufferData(int[] indices, int usage) {
        bind();
        //IntBuffer buffer = IntBuffer.wrap(indices).flip();

        IntBuffer buffer = BufferUtils.createIntBuffer(indices.length);
        buffer.put(indices).flip();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, usage);
        unbind();
    }

    public void bind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
    }
    public void unbind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void delete() {
        glDeleteBuffers(id);
    }
}
