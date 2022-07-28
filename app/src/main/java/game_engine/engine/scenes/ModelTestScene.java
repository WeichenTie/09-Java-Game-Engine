package game_engine.engine.scenes;

import game_engine.engine.camera.Camera;
import game_engine.engine.camera.PerspectiveCamera;
import game_engine.engine.listeners.KeyListener;
import game_engine.engine.listeners.MouseListener;
import game_engine.engine.lwjgl.ebo.EBO;
import game_engine.engine.lwjgl.shader.Shader;
import game_engine.engine.lwjgl.vao.AttribLayouts;
import game_engine.engine.lwjgl.vao.VAO;
import game_engine.engine.lwjgl.vbo.VBO;

import static org.lwjgl.opengl.GL40.*;

import org.joml.Matrix4f;
import org.joml.Vector3f; 
import org.lwjgl.glfw.GLFW;


public class ModelTestScene extends Scene {
    int maxGrass = 25000;
    float rectBounds = 2f;
    float time = 0.0f;
 
    VAO modelVAO; 
    VBO modelVBO;
    EBO modelEBO;


    VAO quadVAO;
    VBO quadVBO;
    EBO quadEBO;

    Camera camera = new PerspectiveCamera(new Vector3f(0, 5, 0), new Vector3f(-15, -10, -15), 0);

    Shader quadShader;
    Shader cubeShader;
    Shader grassShader;
    float cubeVerts[] = {
        -0.70f, -0.70f, -0.70f,		    0.70f, 0.4f, 0.0f,
        -0.70f, 0.70f, -0.70f,			0.2f, 0.70f, 0.8f,
        0.70f, 0.70f, -0.70f,			0.3f, 0.0f, 0.70f,
        0.70f, -0.70f, -0.70f,			0.0f, 0.0f, 0.0f,

        -0.70f, -0.70f, 0.70f,			0.0f, 0.7f, 0.2f,
        -0.70f, 0.70f, 0.70f,			0.4f, 0.8f, 0.0f,
        0.70f, 0.70f, 0.70f,			0.0f, 0.2f, 0.7f,
        0.70f, -0.70f, 0.70f,			0.9f, 0.5f, 0.8f,
    };

    
    float rectVerts[] = {
        -rectBounds, -0.70f, -rectBounds,
        -rectBounds, 0.00f, -rectBounds,
         rectBounds, 0.00f, -rectBounds,
         rectBounds, -0.70f, -rectBounds,

        -rectBounds, -0.70f, rectBounds,
        -rectBounds, 0.00f, rectBounds,
         rectBounds, 0.00f, rectBounds,
         rectBounds, -0.70f, rectBounds,
    };

    float quadVerts[] = {
        -rectBounds, 0.01f, rectBounds,
         rectBounds, 0.01f, rectBounds,
         rectBounds, 0.01f, -rectBounds,
        -rectBounds, 0.01f, -rectBounds
    };

    int quadIndices[] = {
        0, 1, 3, 1, 2, 3
    };

    int cubeIndices[] = {
        // front face
        0, 1, 2, 2, 3, 0,
        // back face
        4, 7, 6, 6, 5, 4,
        // top face
        1, 5, 6, 6, 2, 1,
        // bottom face
        4, 7, 3, 3, 0, 4,
        // left face
        0, 4, 5, 5, 1, 0,
        // right face
        2, 6, 7, 7, 3, 2
    };

    
    float density_f = 1;
    int tmpVAO, tmpVBO, tmpEBO;
    private VAO grassVAO;
    private VBO grassVBO;
    private EBO grassEBO;
    public ModelTestScene() {
    }

    @Override
    public void init() {
        modelVAO = new VAO();
        modelVBO = new VBO();
        modelEBO = new EBO();
        quadVAO = new VAO();
        quadVBO = new VBO();
        quadEBO = new EBO();

        grassVAO = new VAO();
        grassVBO = new VBO();



        //grassEBO = new EBO(); 

        quadShader = new Shader("Quad");
        quadShader.addVertexShader("./shaders/quad/quad.vert");
        quadShader.addFragmentShader("./shaders/quad/quad.frag");
        quadShader.addDomainShader("./shaders/quad/domain.glsl");
        quadShader.compile();

        quadVBO.bufferData(quadVerts, GL_STATIC_DRAW);
        AttribLayouts quadLayout = new AttribLayouts();
        quadLayout.addAttrib(3, GL_FLOAT, false);
        quadVAO.addAttribs(quadVBO, quadLayout);
        quadEBO.bufferData(quadIndices, GL_STATIC_DRAW);


        cubeShader = new Shader("CubeShader");
        cubeShader.addVertexShader("./shaders/cube/cube.vert");
        cubeShader.addFragmentShader("./shaders/cube/cube.frag");
        cubeShader.compile(); 

        modelVBO.bufferData(rectVerts, GL_STATIC_DRAW);
        AttribLayouts cubeLayout = new AttribLayouts();
        cubeLayout.addAttrib(3, GL_FLOAT, false);
        modelVAO.addAttribs(modelVBO, cubeLayout);
        modelEBO.bufferData(cubeIndices, GL_STATIC_DRAW);


        grassShader = new Shader("GrassShader");
        grassShader.addVertexShader("./shaders/grass/vertex.glsl");
        grassShader.addDomainShader("./shaders/grass/domain.glsl");
        grassShader.addGeometryShader("./shaders/grass/geometry.glsl");
        grassShader.addFragmentShader("./shaders/grass/fragment.glsl");
        grassShader.compile();

        AttribLayouts grassLayout = new AttribLayouts();
        grassLayout.addAttrib(3, GL_FLOAT, false);
        grassVAO.addAttribs(quadVBO, grassLayout);
        
        float[] grassVerts = new float[maxGrass * 3];
        for (int i = 0; i < maxGrass; i += 3) {
            grassVerts[i] = ((float)Math.random() - 0.5f) * 2 * rectBounds;
            grassVerts[i + 1] = 0.0f;
            grassVerts[i + 2] = ((float)Math.random() - 0.5f) * 2 * rectBounds;
        }

        grassVBO.bufferData(grassVerts, GL_STATIC_DRAW);
        glPatchParameteri(GL_PATCH_VERTICES, 3);
    }

    @Override
    public void event(float dt) {
    }
    @Override
    public void update(float dt) {
        time += dt;
        float velocity = 15.0f;
        Vector3f displacement = new Vector3f(); 
        if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_W)) {
            displacement.z = velocity * dt;
        } 
        if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_A)) {
			displacement.x = -velocity * dt;
		}
        if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_S)) {
			displacement.z = -velocity * dt;
		}
        if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_D)) {
			displacement.x = velocity * dt;
		}
        if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) {
			displacement.y = -velocity * dt;
		}
        if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
			displacement.y = velocity * dt;
		}
        if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_UP)) {
			density_f += 100 * dt;
		} 
        if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
			density_f -= 100 * dt;
		} 
        camera.translate(displacement);

        float sens = 6.0f;
        camera.rotate(new Vector3f(-MouseListener.getDy() * dt * sens, MouseListener.getDx() * dt * sens, 0.0f * dt * sens));

        //System.out.println(dt);


        

        float patch_outer_level[] = {density_f, density_f, density_f};
        glPatchParameterfv(GL_PATCH_DEFAULT_OUTER_LEVEL, patch_outer_level);
        float patch_inner_level[] = {density_f};
        glPatchParameterfv(GL_PATCH_DEFAULT_INNER_LEVEL, patch_inner_level);
    }
    @Override
    public void draw(float dt) {

        Matrix4f perspective = new Matrix4f().perspective(90.0f/180f, 16.0f/9.0f, 0.1f, 1000.0f);

        cubeShader.bind();
        modelVAO.bind();
        modelEBO.bind();
        cubeShader.setUniformMatix4fv("u_Model", false, new Matrix4f());
        cubeShader.setUniformMatix4fv("u_View", false, camera.getViewMatrix());
        cubeShader.setUniformMatix4fv("u_Projection", false, perspective);
        glDrawElements(GL_TRIANGLES, cubeIndices.length, GL_UNSIGNED_INT, 0);
        
        // glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
        // quadShader.bind(); 
        // quadVAO.bind();
        // quadEBO.bind();
        // quadShader.setUniformMatix4fv("u_Model", false, new Matrix4f());
        // quadShader.setUniformMatix4fv("u_View", false, camera.getViewMatrix());
        // quadShader.setUniformMatix4fv("u_Projection", false, perspective);
        // glDrawElements(GL_PATCHES, quadIndices.length, GL_UNSIGNED_INT, 0);
        // glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        grassShader.bind();
        grassShader.setUniformMatix4fv("u_Model", false, new Matrix4f());
        grassShader.setUniformMatix4fv("u_View", false, camera.getViewMatrix());
        grassShader.setUniformMatix4fv("u_Projection", false, new Matrix4f().perspective(90.0f/180f, 16.0f/9.0f, 0.1f, 1000.0f));
        grassShader.setUniform1f("u_Time", time);
        grassVAO.bind();
        quadEBO.bind();
        glDrawElements(GL_PATCHES, quadIndices.length, GL_UNSIGNED_INT, 0);
    }
}
