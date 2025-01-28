package com.websocket.wstutorial;

import java.util.*;

import org.springframework.stereotype.Service;

import com.websocket.wstutorial.dto.Message;

@Service
public class GameService {

    private final Map<String, Player> players;
    private final ArrayList<Bullet> bArrayList = new ArrayList<>();
    private final ArrayList<Player> pArrayList = new ArrayList<>();

    public GameService()
    {
        players = new HashMap<>();
    }

    public ArrayList<Player> getPlayerList(){
        return new ArrayList<>(players.values());
    }

    
    public ArrayList<Bullet> getBulletList(){
        return bArrayList;
    }

    public void addPlayer(Player p)
    {
        players.put(p.getname(), p);
    }

//Optimization is needed here (remove null value from list)
    public void updatePlayer(Player player){
        
        // for (Iterator<Player> iterator = pArrayList.iterator(); iterator.hasNext(); ) {
        //     Player value = iterator.next();
        //     if(value==null){
        //         iterator.remove();
        //     }
        //     else if (value.getname().equals(player.getname())) {
        //         iterator.remove();
        //     }
        // }
            
        players.get(player.getname()).update(player);
    
 }

    public void removePlayer(String playerUsername){

        players.remove(playerUsername);

        // for (Iterator<Player> iterator = pArrayList.iterator(); iterator.hasNext(); ) {
        //     Player value = iterator.next();
        //     if(value==null){
        //         iterator.remove();
        //     }
        //     else if (value.getname().equals(playerUsername)) {
        //         iterator.remove();
        //     }
        // }
    }

 public void addBullet(Bullet bullet){
    // int flag=1;
    bArrayList.add(bullet);
        
            

}

//checks bullet bounds and deletes it 
public void nextBullet(){
    for(Bullet bullet2:bArrayList){
        bullet2.update();
        // bullet2.checkBounds();
    }

}
private final Player dummy = new Player(0,0,"null",100);
//bullet collision with player
public Player bulletReg(){
    
    float xp;
    float yp;
    float xb;
    float yb;
    for(Map.Entry<String,Player> mEle : players.entrySet()){
        
        for (Iterator<Bullet> iterator = bArrayList.iterator(); iterator.hasNext(); ) {
            Bullet bullet = iterator.next();
            // for(Bullet bullet : bArrayList){
                xp=mEle.getValue().getx();
                yp=mEle.getValue().gety();       
                xb=bullet.getx();
                yb=bullet.gety();

                if(13.5 >= Math.hypot(xb-xp, yb-yp) ){
                    mEle.getValue().bulletHit();
                    iterator.remove();
                    return mEle.getValue();           
                }
            }
    }


    return dummy;


}


}
