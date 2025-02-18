package com.websocket.wstutorial;

import java.util.ArrayList;
import java.util.List;
import java.awt.Point;
import java.awt.Rectangle;

public class World
{

    Rectangle worldBounds;
    
    List<Player> players;
    List<Bullet> bullets;
    List<Rectangle> walls;

    public World(int x1, int y1, int x2, int y2)
    {
	players = new ArrayList<>();
	bullets = new ArrayList<>();
	walls = new ArrayList<>();

	worldBounds = new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }

    public void update(float dt)
    {
	checkBulletsOutOfBounds();

	checkBulletsCollisions();

	checkPlayersOutOfBounds(dt);
    }

    private void checkPlayersOutOfBounds(float dt)
    {
	for (Player p : players)
	{
	    Point playerPos = new Point((int)p.getx(), (int)p.gety());
	    if (p.getx() < worldBounds.x || p.getx() > (worldBounds.x + worldBounds.width))
	    {
		int clamped_x = p.getx() < worldBounds.x ? worldBounds.x : (worldBounds.x + worldBounds.width);
		
		// bring the player back into the world bounds
		p.setx(clamped_x);
	    }

	    if (p.gety() < worldBounds.y || p.gety() > (worldBounds.y + worldBounds.height))
	    {
		int clamped_y = p.gety() < worldBounds.y ? worldBounds.y : (worldBounds.y + worldBounds.height);

		// bring player back into world bounds
		p.sety(clamped_y);
	    }
	}
    }

    private void checkBulletsOutOfBounds()
    {
	List<Integer> bulletsToBeRemoved = new ArrayList<>();
	for (int i = 0; i < bullets.size(); i++)
	{
	    Bullet b = bullets.get(i);
	    if (!worldBounds.contains(b.getPos()))
	    {
		bulletsToBeRemoved.add(i);
	    }
	}

	for (int index : bulletsToBeRemoved)
	{
	    bullets.remove(index);
	}
    }
    
    private void checkBulletsCollisions()
    {
	List<Bullet> bulletsToBeRemoved = new ArrayList<>();
	for (Player p : players)
	{
	    for (Bullet b : bullets)
	    {
		if (p.collide(b.getPos()))
		{
		    p.bulletHit();
		    bulletsToBeRemoved.add(b);
		}
	    }
	}

	for (Bullet b : bulletsToBeRemoved)
	{
	    bullets.remove(b);
	}
    }
    
}
