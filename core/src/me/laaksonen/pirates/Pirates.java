package me.laaksonen.pirates;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

class Effect {
    private float x;
    private float y;
    private Texture texture;
    private Sprite sprite;
    private long creationTime;
    private long timeToLive;

    public Effect(float x, float y, Texture texture, long creationTime, long timeToLive) {
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.creationTime = creationTime;
        this.timeToLive = timeToLive;
        setSprite(new Sprite(texture));
        getSprite().setSize(4.2f,4.1f);
        getSprite().setCenter(x,y);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Sprite getSprite() {
        return sprite;
    }

    private void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }
}

class ExplosionEffect{
    private Effect effect;

    public ExplosionEffect(float x, float y, Texture texture, long creationTime) {
        effect = new Effect(x,y,texture,creationTime, (long) 1500);
    }

    public Effect getEffect() {
        return effect;
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }

}

class GameObject {
    private float x;
    private float y;
    private Texture texture;
    private Sprite sprite;

    public float getX() {
        return x;
    }

    void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    void setY(float y) {
        this.y = y;
    }

    public Texture getTexture() {
        return texture;
    }

    void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Sprite getSprite() {
        return sprite;
    }

    void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }
}

class CannonBall extends GameObject {
    private Vector2 vector;
    private double direction;
    private Polygon polygon;
    private long creationTime;

    public CannonBall(float x, float y, Vector2 vector, float radius) {
        setX(x);
        setY(y);
        setTexture(new Texture(Gdx.files.internal("cannonBall.png")));
        setSprite(new Sprite(new Texture(Gdx.files.internal("cannonBall.png"))));
        getSprite().setSize(radius*2,radius*2);
        this.vector = vector;
        this.polygon = new Polygon();
        polygon.setVertices(new float[]{0,0,0.25f,0,0.25f,0.25f,0.25f,0});
        this.creationTime = TimeUtils.nanoTime();
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public Vector2 getVector() {
        return vector;
    }

    public void setVector(Vector2 vector) {
        this.vector = vector;
    }

    public double getDirection() {
        return direction;
    }

    public void setDirection(double direction) {
        this.direction = direction;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public void draw(SpriteBatch batch) {
        getSprite().draw(batch);
    }

    public void move() {
        this.getSprite().setX(this.getSprite().getX()+vector.x*0.05f);
        this.getSprite().setY(this.getSprite().getY()+vector.y*0.05f);
        polygon.setPosition(getSprite().getX(),getSprite().getY());
    }
}

class GameCanvas {
    private ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
    private ArrayList<Effect> effects = new ArrayList<Effect>();

    public void addGameObject(GameObject gameObject) {
        gameObjects.add(gameObject);
    }

    public void draw(SpriteBatch batch) {
        for(GameObject gameObject : gameObjects) {
            gameObject.getSprite().draw(batch);
        }
        for(Effect effect : effects) {
            effect.getSprite().draw(batch);
        }
    }
}

class Boat extends GameObject {
    private ArrayList<CannonBall> cannonBalls = new ArrayList<CannonBall>();
    private ArrayList<Texture> textures = new ArrayList<Texture>();
    private Vector2 vector;
    private double heading;
    private Polygon polygon;
    private float[] vertices = {
            3.3f,0.0f,
            2.1f,1.3f,
            1.4f,3.3f,
            1.4f,9.35f,
            3.3f,10.6f,
            5.2f,3.3f,
            4.5f,1.3f};
    private long lastBounceTime;
    private long deathTime;
    private boolean alive;
    private float speedMultiplier;

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    public void setSpeedMultiplier(float speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    public long getDeathTime() {
        return deathTime;
    }

    private void setDeathTime(long deathTime) {
        this.deathTime = deathTime;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public long getLastBounceTime() {
        return lastBounceTime;
    }

    public void setLastBounceTime(long lastBounceTime) {
        this.lastBounceTime = lastBounceTime;
    }

    public void setCannonBalls(ArrayList<CannonBall> cannonBalls) {
        this.cannonBalls = cannonBalls;
    }

    public ArrayList<Texture> getTextures() {
        return textures;
    }

    public void setTextures(ArrayList<Texture> textures) {
        this.textures = textures;
    }


    public ArrayList<CannonBall> getCannonBalls() {
        return cannonBalls;
    }

    public void addCannonBall(CannonBall cannonBall) {
        cannonBalls.add(cannonBall);
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public float[] getVertices() {
        return vertices;
    }

    public void setVertices(float[] vertices) {
        this.vertices = vertices;
    }

    public Vector2 getVector() {
        return vector;
    }

    public void setVector(Vector2 vector) {
        this.vector = vector;
    }

    public Boat(Texture t1, Texture t2, Texture t3, Texture t4, float x, float y, Vector2 victor) {
        textures.add(t1);
        textures.add(t2);
        textures.add(t3);
        textures.add(t4);
        setTexture(t1);
        setX(x);
        setY(y);
        setVector(victor);
        setSprite(new Sprite(t1));
        createPolygon();
        alive = true;
        setHeading(270);
    }

    private void createPolygon() {
        polygon = new Polygon(vertices);
    }


    public void draw(SpriteBatch batch) {
        getSprite().draw(batch);
    }

    public void fireCannons() {
        Vector2 leftCannonVector = new Vector2((float) Math.cos(Math.toRadians(heading+45)), (float) Math.sin(Math.toRadians(heading+45)));
        Vector2 rightCannonVector = new Vector2((float) Math.cos(Math.toRadians(heading-45)), (float) Math.sin(Math.toRadians(heading-45)));
        CannonBall leftCannon = new CannonBall(0,0, new Vector2(leftCannonVector.x*(Math.abs(vector.x)+Math.abs(vector.y)),leftCannonVector.y*(Math.abs(vector.x)+Math.abs(vector.y))),0.2f);
        leftCannon.getSprite().setCenter(this.getSprite().getX()+this.getSprite().getOriginX(),this.getSprite().getY()+this.getSprite().getOriginY());
        CannonBall rightCannon = new CannonBall(0,0, new Vector2(rightCannonVector.x*(Math.abs(vector.x)+Math.abs(vector.y)),rightCannonVector.y*(Math.abs(vector.x)+Math.abs(vector.y))),0.2f);
        rightCannon.getSprite().setCenter(this.getSprite().getX()+this.getSprite().getOriginX(),this.getSprite().getY()+this.getSprite().getOriginY());
        cannonBalls.add(leftCannon);
        cannonBalls.add(rightCannon);
    }

    public void turn() {
        setHeading(vector.angle());
        getSprite().setRotation(vector.angle()-270);
        getPolygon().setRotation(vector.angle()-270);
    }

    public void move() {
        getSprite().setX(getSprite().getX() + vector.x  * Gdx.graphics.getDeltaTime());
        getSprite().setY(getSprite().getY() + vector.y  * Gdx.graphics.getDeltaTime());
        getPolygon().setPosition(getSprite().getX(),getSprite().getY());
    }

    public void removeCannonBall(CannonBall cannonBall) {
        cannonBalls.remove(cannonBall);
    }

    public void cycleTexture() {
        int i = textures.indexOf(getSprite().getTexture());
        if(i < textures.size()-1) {
            float x = getSprite().getX();
            float y = getSprite().getY();
            setSprite(new Sprite(textures.get(i+1)));
            getSprite().setSize(6.6f,11.3f);
            getSprite().setOrigin(3.3f,5.65f);
            getSprite().setX(x);
            getSprite().setY(y);
            getSprite().setRotation(getVector().angle()-270);
        }
        if(i == textures.size()-2){
            alive = false;
            setDeathTime(TimeUtils.millis());
        }

    }
}

class IterableRemover {

    public void cleanUpExplosions(ArrayList<ExplosionEffect> list) {
        Iterator<ExplosionEffect> iterator = list.iterator();
        while(iterator.hasNext()) {
            ExplosionEffect effect = iterator.next();
            if (TimeUtils.timeSinceMillis(effect.getEffect().getCreationTime()) > effect.getEffect().getTimeToLive()) {
                iterator.remove();
            }
        }
    }

    public void cleanUpBoats(ArrayList<Boat> list) {
        Iterator<Boat> iterator = list.iterator();
        while(iterator.hasNext()) {
            Boat boaterino = iterator.next();
            if(!boaterino.isAlive()) {
                // boaterino.getSprite().setSize(boaterino.getSprite().getWidth()-0.0066f,boaterino.getSprite().getHeight()-0.0113f);
                if (TimeUtils.timeSinceMillis(boaterino.getDeathTime()) > 10000) {
                    iterator.remove();
                }
            }
        }
    }

    public void cannonBallCleanUp(Boat b) {
        Iterator<CannonBall> iterator = b.getCannonBalls().iterator();
        while(iterator.hasNext()) {
            CannonBall cannonBall = iterator.next();
            if (TimeUtils.timeSinceNanos(cannonBall.getCreationTime()) > 1000000000) {
                iterator.remove();
            }
        }
    }
}

public class Pirates implements ApplicationListener {

    enum gameStates {
        MAINMENU,GAME,GAMEOVER
    }

    class MyGestureListener implements GestureDetector.GestureListener {

        @Override
        public boolean touchDown(float x, float y, int pointer, int button) {

            return false;
        }

        @Override
        public boolean tap(float x, float y, int count, int button) {
            if(count == 2) {
                Vector3 touchPos = new Vector3(x, y, 0);
                cam.unproject(touchPos);
                playerBoat.getSprite().setX(touchPos.x);
                playerBoat.getSprite().setY(touchPos.y);
                playerBoat.getPolygon().setPosition(touchPos.x,touchPos.y);
            }
            return false;
        }

        @Override
        public boolean longPress(float x, float y) {

            return false;
        }

        @Override
        public boolean fling(float velocityX, float velocityY, int button) {

            return false;
        }

        @Override
        public boolean pan(float x, float y, float deltaX, float deltaY) {

            return false;
        }

        @Override
        public boolean panStop(float x, float y, int pointer, int button) {

            return false;
        }

        @Override
        public boolean zoom (float originalDistance, float currentDistance){
            cam.zoom += (originalDistance - currentDistance)*0.00001f;
            return false;
        }

        @Override
        public boolean pinch (Vector2 initialFirstPointer, Vector2 initialSecondPointer, Vector2 firstPointer, Vector2 secondPointer){

            return false;
        }
        @Override
        public void pinchStop () {
        }
    }

	private static final int worldWidth = 256;
	private static final int worldHeight = 256;
	private OrthographicCamera cam;
	private SpriteBatch batch;
    private long cannonTimer;
    private ArrayList<Boat> armada = new ArrayList<Boat>();
    private ArrayList<ExplosionEffect> explosionEffects = new ArrayList<ExplosionEffect>();
    private Boat playerBoat;
	private Polygon firstIslandPolygon;
    private Polygon botPolygon;
    private Polygon topPolygon;
    private Polygon leftPolygon;
    private Polygon rightPolygon;
    private ShapeRenderer shapeRenderer;
	private Sprite mapSprite;
	private boolean paused = false;
	private Music bgm;
	private Sound cannonSound;
	private Sound explosionSound;
	private Sound youDiedSound;
	private float xOffset;
	private float yOffset;
	private gameStates state = gameStates.GAME;
	private BitmapFont font;
	private Sprite youDiedSprite;
	private IterableRemover iterableRemover;
	private float shipSpeed = 2;
	private float maxSpeed = 7.5f;
	private float minSpeed = 2;


	@Override
	public void create() {
	    font = new BitmapFont();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        cannonTimer = TimeUtils.nanoTime();
        youDiedSprite = new Sprite(new Texture(Gdx.files.internal("youdied.PNG")));
        iterableRemover = new IterableRemover();
        initArmada();
        initPlayerBoat();
        initGeometry();
	    initMap();
	    initCamera();
        initMusic();
	    initSoundEffects();
	    initPosition();
        Gdx.input.setInputProcessor(new GestureDetector(new MyGestureListener()));
    }

	@Override
	public void render() {
	    if(!paused) {
            handleInput();
            turnAndMoveArmada();
            moveCannonBalls();
            clampCamera();
            shipCleanUp();
            explosionCleanUp();
            removeOldCannonBalls();
            bounceEnemiesFromEdges();
            cam.update();
            batch.setProjectionMatrix(cam.combined);
            cam.update();
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            batch.begin();
            drawGameObjects();
            checkCannonBallHit();
            if(state == gameStates.GAMEOVER) {
                youDiedSprite.setSize(cam.viewportWidth*cam.zoom,cam.viewportHeight/4*cam.zoom);
                youDiedSprite.setCenter(cam.position.x,cam.position.y);
                youDiedSprite.draw(batch);
            }
            batch.end();
            debugRender();
            checkIslandCollisions();
            checkShipCollision();
        }
    }

    @Override
    public void pause() {
        paused = true;
        bgm.pause();
    }

    @Override
    public void resume() {
        paused = false;
        bgm.play();
    }

    private void initPosition() {
	    xOffset = Gdx.input.getAccelerometerY();
	    yOffset = Gdx.input.getAccelerometerZ();
    }

    private void initSoundEffects() {
        cannonSound = Gdx.audio.newSound(Gdx.files.internal("cannonfire.wav"));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.mp3"));
        youDiedSound = Gdx.audio.newSound(Gdx.files.internal("youdied.mp3"));
    }

    private void initMusic() {
	    bgm = Gdx.audio.newMusic(Gdx.files.internal("seashanty.mp3"));
	    bgm.setLooping(true);
	    bgm.play();
    }

    private void initCamera() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        cam = new OrthographicCamera(76.8f, 76.8f * (h / w));
        cam.update();
    }

    private void initMap() {
        mapSprite = new Sprite(new Texture(Gdx.files.internal("Map.png")));
        mapSprite.setPosition(0, 0);
        mapSprite.setSize(worldWidth, worldHeight);
    }

    private void initGeometry() {
        float[] botVertices = {0,0,0,1,256,1,256,0};
        botPolygon = new Polygon(botVertices);

        float[] topVertices = {0,256,0,255,256,256,256,255};
        topPolygon = new Polygon(topVertices);

        float[] leftVertices = {0,256,0,255,0,0,0,1};
        leftPolygon = new Polygon(leftVertices);

        float[] rightVertices = {256,0,255,0,256,256,255,256};
        rightPolygon = new Polygon(rightVertices);

        firstIslandPolygon = new Polygon();
        float[] firstIslandVertices = {
                0.0f, 89.6f,
                38.4f, 89.6f,
                51.2f, 96.0f,
                51.2f, 102.4f,
                44.8f, 115.2f,
                38.4f, 115.2f,
                32.0f, 128.0f,
                25.6f, 128.0f,
                12.8f, 128.0f,
                0.0f, 121.6f};
        firstIslandPolygon = new Polygon(firstIslandVertices);
        firstIslandPolygon.setPosition(0,0);
    }

    private void initPlayerBoat() {
        playerBoat = new Boat(new Texture(Gdx.files.internal("ship (2).png")),new Texture(Gdx.files.internal("ship (8).png")),new Texture(Gdx.files.internal("ship (14).png")),new Texture(Gdx.files.internal("ship (20).png")),0,0, new Vector2());
        playerBoat.getSprite().setSize(6.6f,11.3f);
        playerBoat.getSprite().setOrigin(3.3f,5.65f);
        playerBoat.getSprite().setCenter(worldWidth/2,worldHeight/2);
        playerBoat.getPolygon().setPosition(worldWidth/2,worldHeight/2);
        playerBoat.getPolygon().setOrigin(3.3f,5.65f);
    }

    private void initArmada() {
        for(int i = 0; i < 10; i++) {
            Boat enemyBoat = new Boat(new Texture(Gdx.files.internal("ship (3).png")),new Texture(Gdx.files.internal("ship (9).png")),new Texture(Gdx.files.internal("ship (15).png")),new Texture(Gdx.files.internal("ship (21).png")),0,0, new Vector2());
            enemyBoat.getSprite().setSize(6.6f,11.3f);
            enemyBoat.getSprite().setOrigin(3.3f,5.65f);
            enemyBoat.getSprite().setCenter(new Random().nextFloat()*worldWidth,new Random().nextFloat()*worldHeight);
            enemyBoat.getPolygon().setOrigin(3.3f,5.65f);
            float randomAngle = new Random().nextFloat()*361;
            float randomFactorVectorVictor = new Random().nextFloat()*3+2;
            enemyBoat.setVector(new Vector2((float) Math.sin(Math.toRadians(randomAngle))*randomFactorVectorVictor, (float) Math.cos(Math.toRadians(randomAngle))*randomFactorVectorVictor));
            armada.add(enemyBoat);
        }
    }

    private void drawGameObjects() {
        mapSprite.draw(batch);
        drawCannonBalls();
        drawArmada();
        playerBoat.draw(batch);
        drawExplosions();
    }

    private void drawExplosions() {
	    for(ExplosionEffect effect : explosionEffects) {
	        effect.getEffect().getSprite().draw(batch);
        }
    }

    private void turnAndMoveArmada() {
	    for(Boat boat : armada) {
            if(boat.isAlive()) {
                boat.turn();
                boat.move();
            }
        }
    }

    private void moveCannonBalls() {
        for(CannonBall cannonBall : playerBoat.getCannonBalls()) {
            cannonBall.move();
        }
    }

    private void drawCannonBalls() {
        for(CannonBall cannonBall : playerBoat.getCannonBalls()) {
            cannonBall.draw(batch);
            cannonBall.move();
        }
    }

    private void drawArmada() {
        for(Boat boat : armada) {
            boat.draw(batch);
        }
    }

    private void checkShipCollision() {
	    for(Boat b : armada) {
            if(Intersector.overlapConvexPolygons(playerBoat.getPolygon(), b.getPolygon()) && state == gameStates.GAME) {
                Gdx.app.log("COLLISION","SHIP CRASH AHOY");
                state = gameStates.GAMEOVER;
                youDiedSound.play();
            }
            for(Boat b2 : armada) {
                if(Intersector.overlapConvexPolygons(b.getPolygon(), b2.getPolygon()) && b2 != b) {
                    Gdx.app.log("COLLISION","SHIP CRASH AHOY");
                    b.cycleTexture();
                    b2.cycleTexture();
                }
            }
        }
    }

    private void checkIslandCollisions() {
        if(Intersector.overlapConvexPolygons(playerBoat.getPolygon(), firstIslandPolygon) && state == gameStates.GAME) {
            Gdx.app.log("COLLISION","LAND AHOY");
            state = gameStates.GAMEOVER;
            youDiedSound.play();
        }
    }

    private void explosionCleanUp() {
	    iterableRemover.cleanUpExplosions(explosionEffects);
    }

    private void shipCleanUp() {
        iterableRemover.cleanUpBoats(armada);
    }

    private void checkCannonBallHit() {
        for(Boat b: armada) {
            Iterator<CannonBall> iterator = playerBoat.getCannonBalls().iterator();
            while(iterator.hasNext()) {
                CannonBall cannonBall = iterator.next();
                if(Intersector.overlapConvexPolygons(b.getPolygon(), cannonBall.getPolygon())) {
                    iterator.remove();
                    explosionEffects.add(new ExplosionEffect(cannonBall.getPolygon().getX(),cannonBall.getPolygon().getY(),new Texture(Gdx.files.internal("explosion3.png")),TimeUtils.millis()));
                    explosionSound.play();
                    if(b.isAlive()) {
                        b.cycleTexture();
                    }
                }
            }
        }
    }

    private void removeOldCannonBalls() {
        iterableRemover.cannonBallCleanUp(playerBoat);
    }

    private void fireCannonTimer() {
        if (TimeUtils.timeSinceNanos(cannonTimer) > 1000000000) {
            playerBoat.fireCannons();
            cannonSound.play();
            cannonTimer = TimeUtils.nanoTime();
        }
    }

    private void bounceEnemiesFromEdges() {
	    for(Boat b: armada) {
            if((Intersector.overlapConvexPolygons(b.getPolygon(), botPolygon) || Intersector.overlapConvexPolygons(b.getPolygon(), topPolygon)) && TimeUtils.timeSinceNanos(b.getLastBounceTime()) > 2000000000) {
                b.setVector(Reflect(b.getVector(),new Vector2(0,1)));
                b.setLastBounceTime(TimeUtils.nanoTime());
            } else if((Intersector.overlapConvexPolygons(b.getPolygon(), leftPolygon) || Intersector.overlapConvexPolygons(b.getPolygon(), rightPolygon)) && TimeUtils.timeSinceNanos(b.getLastBounceTime()) > 2000000000) {
                b.setVector(Reflect(b.getVector(),new Vector2(1,0)));
                b.setLastBounceTime(TimeUtils.nanoTime());
            }
        }
    }

	private void debugRender() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.polygon(playerBoat.getPolygon().getTransformedVertices());
        for(Boat b : armada) {
            shapeRenderer.polygon(b.getPolygon().getTransformedVertices());
        }
        shapeRenderer.polygon(topPolygon.getVertices());
        shapeRenderer.polygon(botPolygon.getVertices());
        shapeRenderer.polygon(leftPolygon.getVertices());
        shapeRenderer.polygon(rightPolygon.getVertices());
        shapeRenderer.polygon(firstIslandPolygon.getTransformedVertices());
        shapeRenderer.end();
    }

	private void clampCamera() {
        cam.position.set(playerBoat.getSprite().getX()+playerBoat.getSprite().getOriginX(),playerBoat.getSprite().getY()+playerBoat.getSprite().getOriginY(),0);
        cam.zoom = MathUtils.clamp(cam.zoom, 0.1f, 256/cam.viewportWidth);
		float effectiveViewportWidth = cam.viewportWidth * cam.zoom;
		float effectiveViewportHeight = cam.viewportHeight * cam.zoom;
		cam.position.x = MathUtils.clamp(cam.position.x, effectiveViewportWidth / 2f, 256 - effectiveViewportWidth / 2f);
		cam.position.y = MathUtils.clamp(cam.position.y, effectiveViewportHeight / 2f, 256 - effectiveViewportHeight / 2f);
	}

	private void handleInput() {
	    if(state == gameStates.GAME) {
            if(Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
                Vector2 accelerometerVector = new Vector2(Gdx.input.getAccelerometerY(),Gdx.input.getAccelerometerZ());
                playerBoat.setVector(accelerometerVector);
                playerBoat.turn();
                playerBoat.move();
                fireCannonTimer();
            }
             if(!Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
                playerBoat.setVector(new Vector2((float) Math.cos(Math.toRadians(playerBoat.getHeading()))*shipSpeed, (float) Math.sin(Math.toRadians(playerBoat.getHeading()))*shipSpeed));
                playerBoat.move();
            }

            if(Gdx.input.isKeyPressed(Input.Keys.A)) {
                playerBoat.setHeading(playerBoat.getHeading()+0.5f);
                playerBoat.getSprite().rotate(0.5f);
                playerBoat.getPolygon().rotate(0.5f);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.D)) {
                playerBoat.setHeading(playerBoat.getHeading()-0.5f);
                playerBoat.getSprite().rotate(-0.5f);
                playerBoat.getPolygon().rotate(-0.5f);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                fireCannonTimer();
            }

            if(Gdx.input.isKeyPressed(Input.Keys.W)) {
                if(shipSpeed < maxSpeed) {
                    shipSpeed += 0.1f;
                }
            }
            if(Gdx.input.isKeyPressed(Input.Keys.S)) {
                if(shipSpeed > minSpeed) {
                    shipSpeed -= 0.1f;
                }
            }
            if(Gdx.input.isTouched() && !Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
                Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                cam.unproject(touchPos);
                playerBoat.getSprite().setX(touchPos.x);
                playerBoat.getSprite().setY(touchPos.y);
                playerBoat.getPolygon().setPosition(touchPos.x,touchPos.y);
            }
        }



        if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
            cam.zoom += 0.01f;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.E)) {
            cam.zoom -= 0.01f;
        }

    }

    public static Vector2 Reflect(Vector2 vector, Vector2 normal) {
        Vector2 Reflect = new Vector2();
        float dot = vector.x * normal.x + vector.y * normal.y;
        Reflect.x = vector.x - 2 * dot * normal.x;
        Reflect.y = vector.y - 2 * dot * normal.y;
        return Reflect;
    }

	@Override
	public void resize(int width, int height) {
		cam.viewportWidth = 76.8f;
		cam.viewportHeight = 76.8f * height/width;
		cam.update();
	}

	@Override
	public void dispose() {
		mapSprite.getTexture().dispose();
		batch.dispose();
		bgm.dispose();
		for(Boat b : armada) {
		    b.getTexture().dispose();
        }
        playerBoat.getTexture().dispose();
	}

}
