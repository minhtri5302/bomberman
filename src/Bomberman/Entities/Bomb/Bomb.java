package Bomberman.Entities.Bomb;

import Bomberman.Entities.Dynamic.Bomber;
import Bomberman.Entities.Entity;
import Bomberman.GameContainer;
import Bomberman.Sound.Sound;
import Bomberman.Sound.SoundController;
import Bomberman.graphics.Screen;
import Bomberman.graphics.Sprite;
import Bomberman.graphics.Unit;

import java.util.ArrayList;
import java.util.List;

public class Bomb extends Entity {
    protected int timeToExplode = 120;
    protected int explodeTime = 20;
    protected int flameSize;
    protected boolean exploded = false;
    private int frame;
    protected List<Flame> flameList;

    public Bomb(int x, int y, int flameSize, GameContainer gameContainer) {
        super(x, y);
        this.frame = 0;
        this.gameContainer = gameContainer;
        this.flameSize = flameSize;
        this.sprite = Sprite.bomb[0];
    }

    public boolean isExploded() {
        return exploded;
    }

    public void setExploded(boolean exploded) {
        this.exploded = exploded;
    }

    @Override
    public void update() {
        frame = (frame + 1) % 1000;
        timeToExplode = Math.max(--timeToExplode, 0);
        if (timeToExplode == 0) {
            if (exploded) {
                for (Flame flame : flameList)
                    flame.update();
            } else
                explore();
            explodeTime = Math.max(--explodeTime, 0);
            if (explodeTime == 0)
                remove = true;
        }
    }

    @Override
    public void render(Screen screen) {
        if (timeToExplode == 0) {
            renderFlames(screen);
        } else {
            loadSprite();
            screen.renderEntity((int) x, (int) y, this);
        }
    }

    public void renderFlames(Screen screen) {
        if (exploded && explodeTime > 0) {
            for (Flame flame : flameList) {
                flame.render(screen);
            }
        }
    }

    public void explore() {
        if (exploded) return;
        Sound sound = new Sound(SoundController.BOMB_EXPLODE);
        sound.play();
        exploded = true;
        flameList = new ArrayList<>();
        addFlames(0);
        addFlames(1);
        addFlames(2);
        addFlames(3);
        flameList.add(new Flame(x, y, 4, false, gameContainer));
        Bomber bomber = gameContainer.getBomber();
        if (bomber != null && bomber.posX() == posX() && bomber.posY() == posY() + 1) bomber.collide(this);
    }

    public void addFlames(int direction) {
        int posX = Unit.pixelToPos(x);
        int posY = Unit.pixelToPos(y);

        int flameMaxSize = flameSize;
        for (int i = 1; i <= flameSize; ++i) {
            if (direction == 0) posY--;
            if (direction == 1) posX++;
            if (direction == 2) posY++;
            if (direction == 3) posX--;
            Entity e = gameContainer.getEntity(posX, posY, null);
            if (!e.collide(this)) {
                flameMaxSize = i - 1;
                break;
            }
        }
        posX = Unit.pixelToPos(x);
        posY = Unit.pixelToPos(y);
        for (int i = 1; i <= flameMaxSize; ++i) {
            if (direction == 0) posY--;
            if (direction == 1) posX++;
            if (direction == 2) posY++;
            if (direction == 3) posX--;

            if (i == flameMaxSize && gameContainer.getExistedFlame(posX,posY) == 0)
                flameList.add(new Flame(Unit.posToPixel(posX), Unit.posToPixel(posY), direction, true, gameContainer));
            else flameList.add(new Flame(Unit.posToPixel(posX), Unit.posToPixel(posY), direction, false, gameContainer));
            gameContainer.setExistedFlame(posX,posY ,1);
        }
    }
    private void loadSprite() {
        int _frame = (frame / 10) % 3;
        sprite = Sprite.bomb[_frame];
    }

    public List<Flame> getFlameList() {
        return flameList;
    }

    @Override
    public boolean collide(Entity e) {
        if (e instanceof Bomber) {
            double diffX = x - e.getX();
            double diffY = y - e.getY() + sprite.SIZE;
            return (diffX >= -15 && diffX <= 10 && diffY <= 14 && diffY >= -12);
        }
        if (e instanceof Bomb) {
            timeToExplode = 0;
            return false;
        }
        return false;
    }
}
