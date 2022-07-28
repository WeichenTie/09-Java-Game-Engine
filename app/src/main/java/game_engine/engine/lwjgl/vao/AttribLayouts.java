package game_engine.engine.lwjgl.vao;

import java.util.ArrayList;
import java.util.List;

public class AttribLayouts {
    private List<VAOAttrib> attribs = new ArrayList<VAOAttrib>();
    private int stride = 0;

    static public int getTypeSize(int type) {
        // TODO :: ADD TYPE SIZE
        return 4;
    }

    public void addAttrib(int count, int type, boolean normalise) {
        VAOAttrib attrib = new VAOAttrib(count, type, normalise, stride);
        attribs.add(attrib);
        stride += count * getTypeSize(type);
    }

    public List<VAOAttrib> getAttribs() {
        return attribs;
    }
    public int getStride() {
        return stride;
    }
}
