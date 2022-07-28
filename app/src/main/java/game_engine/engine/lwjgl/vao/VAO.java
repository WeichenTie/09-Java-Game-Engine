package game_engine.engine.lwjgl.vao;
import static org.lwjgl.opengl.GL40.*;

import java.util.List;

import game_engine.engine.lwjgl.vbo.VBO;

public class VAO {
    private int id = 0;
    public VAO () {
        id = glGenVertexArrays();
    }

    public void addAttribs(VBO vbo, AttribLayouts attribLayouts) {
        bind();
        vbo.bind();
        List<VAOAttrib> attribList = attribLayouts.getAttribs();
        for (int i = 0; i < attribList.size(); i++) {
            VAOAttrib attrib = attribList.get(i);
            glEnableVertexAttribArray(i);
            glVertexAttribPointer(i, attrib.count, attrib.type, attrib.normalise, attribLayouts.getStride(), attrib.offset);
        }
        unbind();
        vbo.unbind();
    }

    public void bind() {
        glBindVertexArray(id);
    }

    public void unbind() {
        glBindVertexArray(0);
    }
    public void delete() {
        glDeleteVertexArrays(id);
    }
}
