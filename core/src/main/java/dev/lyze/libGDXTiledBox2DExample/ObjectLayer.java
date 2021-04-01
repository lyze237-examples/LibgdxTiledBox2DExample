package dev.lyze.libGDXTiledBox2DExample;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ObjectLayer extends ScreenAdapter
{
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer mapRenderer;

    private final World world;
    private final Box2DDebugRenderer worldRenderer;
    private final int tileSize;

    private final Viewport viewport;

    public ObjectLayer()
    {
        world = new World(new Vector2(0, -9.81f), true);
        worldRenderer = new Box2DDebugRenderer();

        map = new TmxMapLoader().load("ObjectLayer.tmx");
        tileSize = ((TiledMapTileLayer) map.getLayers().get(0)).getTileWidth();
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        // This ensures that 30x20 tiles are always going to be visible, doesn't matter what the windows resolution is.
        viewport = new ExtendViewport(30 * 16, 20 * 16);

        parseMap();
    }

    private void parseMap()
    {
        MapObjects collisions = map.getLayers().get("Collisions").getObjects();
        for (int i = 0; i < collisions.getCount(); i++)
        {
            MapObject mapObject = collisions.get(i);

            if (mapObject instanceof RectangleMapObject)
            {
                RectangleMapObject rectangleObject = (RectangleMapObject) mapObject;
                Rectangle rectangle = rectangleObject.getRectangle();

                BodyDef bodyDef = getBodyDef(rectangle.getX() + rectangle.getWidth() / 2f, rectangle.getY() + rectangle.getHeight() / 2f);

                Body body = world.createBody(bodyDef);
                PolygonShape polygonShape = new PolygonShape();
                polygonShape.setAsBox(rectangle.getWidth() / 2f, rectangle.getHeight() / 2f);
                body.createFixture(polygonShape, 0.0f);
                polygonShape.dispose();
            }
            else if (mapObject instanceof EllipseMapObject)
            {
                EllipseMapObject circleMapObject = (EllipseMapObject) mapObject;
                Ellipse ellipse = circleMapObject.getEllipse();

                BodyDef bodyDef = getBodyDef(ellipse.x, ellipse.y);

                if (ellipse.width != ellipse.height)
                    throw new IllegalArgumentException("Only circles are allowed.");

                Body body = world.createBody(bodyDef);
                CircleShape circleShape = new CircleShape();
                circleShape.setRadius(ellipse.width / 2f);
                body.createFixture(circleShape, 0.0f);
                circleShape.dispose();
            }
            else if (mapObject instanceof PolygonMapObject)
            {
                PolygonMapObject polygonMapObject = (PolygonMapObject) mapObject;
                Polygon polygon = polygonMapObject.getPolygon();

                BodyDef bodyDef = getBodyDef(polygon.getX(), polygon.getY());

                Body body = world.createBody(bodyDef);
                PolygonShape polygonShape = new PolygonShape();
                polygonShape.set(polygon.getVertices());
                body.createFixture(polygonShape, 0.0f);
                polygonShape.dispose();
            }
        }
    }

    private BodyDef getBodyDef(float x, float y)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        return bodyDef;
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.step(Gdx.graphics.getDeltaTime(), 6, 2); // Fixme

        viewport.apply();

        mapRenderer.setView((OrthographicCamera) viewport.getCamera());
        mapRenderer.render();

        worldRenderer.render(world, viewport.getCamera().combined);
    }

    @Override
    public void resize(int width, int height)
    {
        viewport.update(width, height, true);
    }
}