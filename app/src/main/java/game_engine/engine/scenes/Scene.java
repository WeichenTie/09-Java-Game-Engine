package game_engine.engine.scenes;

public abstract class Scene {
    public Scene() {

    }
    public abstract void init();
    public abstract void event(float dt);
    public abstract void update(float dt);
    public abstract void draw(float dt);

}
