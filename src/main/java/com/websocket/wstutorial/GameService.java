package com.websocket.wstutorial;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import java.util.*;
import java.awt.Point;
import java.awt.Rectangle;
import java.time.LocalDateTime;

import org.apache.catalina.webresources.WarResourceSet;
import org.springframework.stereotype.Service;

//import com.sun.org.slf4j.internal.LoggerFactory;
import com.websocket.wstutorial.dto.Message;

@Service
public class GameService{

    private final Rectangle worldBounds = new Rectangle(23,23,2976,2976);
    public final Map<String, Player> players;
    private final List<Bullet> bullets;
    private List<Wall> walls;
	//private final Logger LOG = LoggerFactory.getLogger(GameService.class);

	private LocalDateTime endTime;
	LocalDateTime tenSecTest = LocalDateTime.now().plusSeconds(50);

    public GameService()
    {
	players = new HashMap<>();
	bullets = new ArrayList<>();
	walls = new ArrayList<>();
		// int x1 = 200;
		// int y1 = 200;
		// int x2 = 300;
		// int y2 = 300;
	walls.add(new Wall(208, 7, 287, 140));
	walls.add(new Wall(208, 151, 287, 250));
	walls.add(new Wall(273, 458, 412, 586));
	walls.add(new Wall(149, 938, 288, 1017));
	walls.add(new Wall(330, 940, 724, 1018));
	walls.add(new Wall(645, 998, 724, 1228));
	walls.add(new Wall(886, 134, 1179,216));
	walls.add(new Wall(887, 206, 968, 527));
	walls.add(new Wall(1045, 323, 1124, 401));
	walls.add(new Wall(922, 757, 1364, 835));
	walls.add(new Wall(922, 815, 1001, 1360));
	walls.add(new Wall(371, 1077, 450, 1155));
	
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
		// Rectangle wall1 = new Rectangle(208, 7, 287, 140);
	for (Iterator<Bullet> iter = bullets.iterator(); iter.hasNext();)
	{
	    Bullet bullet = iter.next();

	    bullet.update();
	    if (!worldBounds.contains(bullet.getPos()))
	    {
		iter.remove();
	    }
		// else if(!wall1.contains(bullet.getPos())){
		// 	iter.remove();

		// }

		for (Wall w : walls)
		{
			if (w.collideBullet(bullet))
			{
				iter.remove();
			}
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
	for(Map.Entry<String,Player> mEle : new HashSet<Map.Entry<String,Player>>(players.entrySet())){

	    Player player = mEle.getValue();
	    
	    for (Iterator<Bullet> iterator = bullets.iterator(); iterator.hasNext(); ) {

		Bullet bullet = iterator.next();

		if(player.collide(bullet.getPos())){
		    player.bulletHit();
			float current_playerHp = player.getHp();
			if (current_playerHp == 0.0f){
				String play_name = player.getname();
				System.out.println(play_name+" was killed by "+bullet.getbulletID());
				players.get(bullet.getbulletID()).incScore();
				players.remove(play_name);

			}
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


	//removes all player from the game (GAME OVER condition)
	public void nukePlayers(){
		for(Map.Entry<String,Player>mEle : players.entrySet()){
			mEle.getValue().setHp(-1);
		}

		players.clear();
	}

	public boolean shouldStartTimer() {
		if (!players.isEmpty() && endTime == null) {
			endTime = LocalDateTime.now().plusSeconds(50);
			tenSecTest = endTime;
		}
		return endTime != null;
	}
	
	public LocalDateTime getEndTime() {
		return endTime != null ? endTime : LocalDateTime.now();
	}
	

}
