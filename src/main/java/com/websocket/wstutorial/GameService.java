package com.websocket.wstutorial;

import java.util.*;
import java.awt.Point;
import java.awt.Rectangle;

import org.apache.catalina.webresources.WarResourceSet;
import org.springframework.stereotype.Service;

import com.websocket.wstutorial.dto.Message;

@Service
public class GameService {

    private final Rectangle worldBounds = new Rectangle(0,0,500,500);
    private final Map<String, Player> players;
    private final List<Bullet> bullets;
    private List<Wall> walls;

    public GameService()
    {
	players = new HashMap<>();
	bullets = new ArrayList<>();
	walls = new ArrayList<>();

	walls.add(new Wall(200, 200, 300, 300));
    }

    public ArrayList<Player> getPlayerList()
    {
        return new ArrayList<>(players.values());
    }
    
    public List<Bullet> getBulletList(){
        return bullets;
    }

    public void addPlayer(Player p)
    {
	players.put(p.getname(), p);
    }

    public void removePlayer(String playerUsername)
    {
        players.remove(playerUsername);
    }

    public void addBullet(Bullet bullet){
	bullets.add(bullet);
    }

    public void updateBullets()
    {
	for (Iterator<Bullet> iter = bullets.iterator(); iter.hasNext();)
	{
	    Bullet bullet = iter.next();

	    bullet.update();
	    if (!worldBounds.contains(bullet.getPos()))
	    {
		iter.remove();
	    }
	}
    }

    public void checkPlayerWallCollisions()
    {
	for (Map.Entry<String, Player> entry : players.entrySet())
	{
	    for (Wall w : walls)
	    {
		w.collidePlayer(entry.getValue());
	    }
	}
    }

    public List<Player> checkPlayerOutOfBounds()
    {
	List<Player> playerToBeNotified = new ArrayList<>();
	
	for (Map.Entry<String, Player> entry : players.entrySet())
	{

	    Point playerPos = entry.getValue().getPos();
	    if (!worldBounds.contains(playerPos))
	    {
		if (playerPos.x > worldBounds.getMaxX())
		{
		    entry.getValue().setx((int)worldBounds.getMaxX());
		    entry.getValue().setvelx(0);
		}
		else if (playerPos.x < worldBounds.getMinX())
		{
		    entry.getValue().setx((int)worldBounds.getMinX());
		    entry.getValue().setvelx(0);
		}

		if (playerPos.y > worldBounds.getMaxY())
		{
		    entry.getValue().sety((int)worldBounds.getMaxY());
		    entry.getValue().setvely(0);
		}
		else if (playerPos.y < worldBounds.getMinY())
		{
		    entry.getValue().sety((int)worldBounds.getMinY());
		    entry.getValue().setvely(0);
		}
	    }

	    playerToBeNotified.add(entry.getValue());
	}

	return playerToBeNotified;
    }
    
    // check bullet collision with player
    public List<Player> checkPlayerBulletCollisions(){

	List<Player> result = new ArrayList<>();
	
	for(Map.Entry<String,Player> mEle : players.entrySet()){

	    Player player = mEle.getValue();
	    
	    for (Iterator<Bullet> iterator = bullets.iterator(); iterator.hasNext(); ) {

		Bullet bullet = iterator.next();

		if(player.collide(bullet.getPos())){
		    player.bulletHit();
		    iterator.remove();
		    result.add(player);          
		}
	    }
	}

	return result;

    }

    public void predictPos(){
	for(Map.Entry<String,Player> mEle : players.entrySet()) {
	    mEle.getValue().update();
	}
    }

}
