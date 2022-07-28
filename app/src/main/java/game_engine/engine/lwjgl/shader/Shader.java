package game_engine.engine.lwjgl.shader;

import game_engine.utils.FileReader;

import java.util.Map;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.HashMap;

import static org.lwjgl.opengl.GL40.*;



public class Shader {

    private static Map<String, Shader> allShaders = new HashMap<String, Shader>();

    private Map<Integer, Integer> shaders = new HashMap<Integer, Integer>();
    private Map<String, Integer> uniformLocations = new HashMap<String, Integer>();

    public int id;
    public String name;

    public Shader(String name) {
        Shader.allShaders.put(name, this);
    }

    public static Shader getShader(String name) {
        return allShaders.get(name);
    }

    private int createShader(int type, String source) {
        int shader = glCreateShader(type);
        glShaderSource(shader, source);
        glCompileShader(shader);
        compileShaderErrors(shader, type);
        return shader;
    }

    public void compile() {
        id = glCreateProgram();
        for (int shaderId : shaders.values()) {
            glAttachShader(id, shaderId);
        }
        glLinkProgram(id);
        compileProgramErrors();
    }

    public void addVertexShader(String path) {
        shaders.put(GL_VERTEX_SHADER , createShader(GL_VERTEX_SHADER, FileReader.readString(path)));
    }

    public void addGeometryShader(String path) {
        shaders.put(GL_GEOMETRY_SHADER , createShader(GL_GEOMETRY_SHADER, FileReader.readString(path)));
    }

    public void addDomainShader(String path) {
        shaders.put(GL_TESS_EVALUATION_SHADER , createShader(GL_TESS_EVALUATION_SHADER, FileReader.readString(path)));
    }

    public void addFragmentShader(String path) {
        shaders.put(GL_FRAGMENT_SHADER , createShader(GL_FRAGMENT_SHADER, FileReader.readString(path)));
    }

    private void compileShaderErrors(int shaderId, int type) {
        int success = glGetShaderi(shaderId, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(shaderId, GL_INFO_LOG_LENGTH);
            String log = glGetShaderInfoLog(shaderId, len);
            System.out.println(log);
        }
    }
    private void compileProgramErrors() {
        int success = glGetProgrami(id, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(id, GL_INFO_LOG_LENGTH);
            String log = glGetProgramInfoLog(id, len);
            System.out.println(log);
        }
    }

    public int getUniformLocation(String uniform) {
        Integer loc = uniformLocations.get(uniform);
        if (loc == null) {
            loc = glGetUniformLocation(id, uniform);
            uniformLocations.put(uniform, loc);
        }

        if (loc.equals(null)) 
            System.out.println("Warning: UNIFORM: '" + uniform + "' not found");

        return loc;
    }

    public void setUniformMatix4fv(String uniform, boolean transpose, Matrix4f matrix) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        matrix.get(buffer);
        glUniformMatrix4fv(getUniformLocation(uniform), transpose, buffer);
    }
    public void setUniform1f(String uniform, float number) {
        glUniform1f(getUniformLocation(uniform), number);
    }

    public void bind() {
        glUseProgram(id);
    }

    public void unbind() {
        glUseProgram(0);
    }
}
