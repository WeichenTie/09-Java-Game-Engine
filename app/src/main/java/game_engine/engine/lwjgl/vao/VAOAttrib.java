package game_engine.engine.lwjgl.vao;

public class VAOAttrib {
    final int count;
    final int type;
    final boolean normalise;
    final int offset;
    public VAOAttrib(int count, int type, boolean normalise, int offset) {
        this.count = count;
        this.type = type;
        this.normalise = normalise;
        this.offset = offset;
    } 
}
