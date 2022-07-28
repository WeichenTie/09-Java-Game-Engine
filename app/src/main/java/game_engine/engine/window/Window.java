package game_engine.engine.window;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import game_engine.engine.listeners.KeyListener;
import game_engine.engine.listeners.MouseListener;
import game_engine.engine.scenes.ModelTestScene;
import game_engine.engine.scenes.Scene;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {
    private int width, height;
    private String title;
    private long glfwWindow;
    private static Window window = null;

    private Scene activeScene = new ModelTestScene();

    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Window";
    }

    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
            return window;
        }
        return window;
    }

    public void run() {
        System.out.println("HELLO LWJGL " + Version.getVersion() + "!");
        init();
        activeScene.init();
        loop();
        // Free the window callbacks and destroy the window
		glfwFreeCallbacks(glfwWindow);
		glfwDestroyWindow(glfwWindow);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
        
        // Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Setup a mouse callbacks
		//glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetInputMode(glfwWindow, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        // Setup a key callbacks
		glfwSetKeyCallback(glfwWindow, (KeyListener::keyCallback));

        // Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(glfwWindow, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				glfwWindow,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

        // Make OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        
        // Enable v-sync
        glfwSwapInterval(1);

        glfwShowWindow(glfwWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
        GL.createCapabilities();
    }
    private void loop() {

        glPointSize(20);
        glEnable(GL_BLEND);
        glEnable(GL_POINT_SMOOTH);
        glfwWindowHint(GLFW_SAMPLES, 16);
        glEnable(GL_MULTISAMPLE); 
        // Set the clear color
		glClearColor(0.392f, 0.584f, 92.9f, 1f);
        glEnable(GL_DEPTH_TEST);
        glfwPollEvents();
        MouseListener.update(glfwWindow);
        float beginTime = (float)GLFW.glfwGetTime();
        float endTime;
        float dt = -1;
		while (!glfwWindowShouldClose(glfwWindow)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
			// Poll for window events. The key callback above will only be
			// invoked during this call.
            MouseListener.update(glfwWindow);
			glfwPollEvents();
            activeScene.event(dt);
            activeScene.update(dt);
            activeScene.draw(dt);
            glfwSwapBuffers(glfwWindow); // swap the color buffers
            endTime = (float)GLFW.glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
            //System.out.println("FPS: " + (1/dt));
		}
    }
    
}
