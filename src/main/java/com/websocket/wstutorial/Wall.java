package com.websocket.wstutorial;

import java.awt.Point;
import java.awt.Rectangle;

public class Wall
{
    private Rectangle bounds;

    public Wall(int x1, int y1, int x2, int y2)
    {
	bounds = new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }

    public void collidePlayer(Player p)
    {
	Point playerPos = p.getPos();
	if (bounds.contains(playerPos))
	{
	    double d1 = playerPos.x - bounds.getMinX();
	    double d2 = bounds.getMaxX() - playerPos.x;

	    double d3 = playerPos.y - bounds.getMinY();
	    double d4 = bounds.getMaxY() - playerPos.y;

	    double min_val = Math.min(d1, Math.min(d2, Math.min(d3, d4)));

	    if (min_val == d1)
	    {
		p.setx((int)bounds.getMinX());
		//p.setvelx(0);
	    }
	    else if (min_val == d2)
	    {
		p.setx((int)bounds.getMaxX());
		//p.setvelx(0);
	    }
	    else if (min_val == d3)
	    {
		p.sety((int)bounds.getMinY());
		//p.setvely(0);
	    }
	    else
	    {
		p.sety((int)bounds.getMaxY());
		//p.setvely(0);
	    }
	}
    }
}
