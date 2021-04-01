package dev.lyze.libGDXTiledBox2DExample;

import com.badlogic.gdx.Game;

public class Main extends Game
{
    @Override
    public void create()
    {
        setScreen(new ObjectLayer());
        //setScreen(new TileCollisions());
    }
}