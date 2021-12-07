package Bomberman.Entities.Box;

import Bomberman.Entities.Bomb.Bomb;
import Bomberman.Entities.Entity;
import Bomberman.graphics.Screen;
import Bomberman.graphics.Sprite;
import Bomberman.graphics.Unit;

public class Brick extends Box {
    protected int frame;
    protected int brokenTime = 30;
    protected boolean broken = false;
    protected Entity belowEntity;
    public Brick(int x, int y) {
        super(x, y);
    }
    @Override
    public void update() {
        if(broken) {
            frame = (frame + 1) % 1000;
            if(brokenTime > 0)
                brokenTime--;
            else
                remove = true;
        }
    }
    @Override
    public void render(Screen screen) {
        if(broken) {
            if(frame >= 0) sprite = Sprite.broken_brick[0];
            if(frame >= 10) sprite = Sprite.broken_brick[1];
            if(frame >= 20) sprite = Sprite.broken_brick[2];
            screen.renderEntity(Unit.posToPixel(x), Unit.posToPixel(y), belowEntity);
        }
        else {
            sprite = Sprite.brick;
        }
        screen.renderEntity(Unit.posToPixel(x), Unit.posToPixel(y), this);
    }
    public void setBelowEntity(Entity belowEntity) {
        this.belowEntity = belowEntity;
    }
    @Override
    public boolean collide(Entity e) {
        if(e instanceof Bomb) {
            broken = true;
        }
        return false;
    }
}
