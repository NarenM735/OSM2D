package com.websocket.wstutorial;

import com.websocket.wstutorial.dto.Message;
import com.websocket.wstutorial.dto.ResponseMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.awt.Point;
import java.time.Duration;

@Controller
public class MessageController {


    @Autowired
    private NotificationService notificationService;
    private SimpMessagingTemplate simpMessagingTemplate;

    private GameService gameService;

    public MessageController(GameService gameService,SimpMessagingTemplate simpMessagingTemplate){
        this.gameService=gameService;
        this.simpMessagingTemplate=simpMessagingTemplate;
    }

    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public ResponseMessage getMessage(final Message message,final Principal principal) throws InterruptedException {
        Thread.sleep(1000);
        // notificationService.sendGlobalNotification();
        return new ResponseMessage(HtmlUtils.htmlEscape("Broadcast msg by "+principal.getName()+" : "+message.getMessageContent()));
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
	StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
	Principal user = headers.getUser();
	gameService.removePlayer(user.getName());

    }

    
    @MessageMapping("/playerJoin")
    @SendTo("/topic/playerJoin")
    public Player getName(final Message message,final Principal principal) throws InterruptedException {

	Point assignedSpawPoint = gameService.getRandomSpawnPoint();
	
	Player p = new Player();
       
        p.setname(principal.getName());
        p.setdpName(message.getMessageContent());

	p.setHp(100);
        p.setx((float)assignedSpawPoint.getX());
        p.sety((float)assignedSpawPoint.getY());


        gameService.addPlayer(p);

	System.out.println("NEW PLAYER!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

	return p;

    }
    // Player postion streaming
    @MessageMapping("/gameState")
    // @SendTo("/topic/gameState")
    public void getLocation(final PlayerData player1,final Principal principal) throws InterruptedException {
	// player1.setname(principal.getName());
           
            
	gameService.updatePlayer(player1);
	//    Thread.sleep(20);
        
        //function
        
        // return gameService.getPlayerList();
    }

    //Bullet streaming
    @MessageMapping("/gameBullets")
    // @SendTo("/topic/gameState")
    public void getBullet(Bullet bullet1) throws InterruptedException {
	// player1.setname(principal.getName());
	gameService.addBullet(bullet1);
	simpMessagingTemplate.convertAndSend("/topic/gameBulletSingle", bullet1);
	// LOG.info("bullet received",bullet1);
        //    Thread.sleep(20);
        
        //function
        
        // return gameService.getPlayerList();
    }
    
    @Scheduled(fixedRate = 16)
    @MessageMapping("/timerFunction")
    @SendTo("/topic/timerFunction")
    public ResponseMessage getRemainingTime() {
        if (gameService.shouldStartTimer()) {
            long remainingTime = Duration.between(LocalDateTime.now(), gameService.getEndTime()).getSeconds();
            remainingTime = Math.max(remainingTime, 0);

            simpMessagingTemplate.convertAndSend("/topic/timerFunction", remainingTime);
            return new ResponseMessage(HtmlUtils.htmlEscape(""+remainingTime));
        }
        return new ResponseMessage(HtmlUtils.htmlEscape("50"));
    }


    
    @Scheduled(fixedRate =16 )
    public void sendPlayerList() throws InterruptedException {
	simpMessagingTemplate.convertAndSend("/topic/gameState", gameService.getPlayerList()); 
        if(LocalDateTime.now().isAfter(gameService.tenSecTest)){
            simpMessagingTemplate.convertAndSend("/topic/getLeader", gameService.computeLeader()); 
            System.out.print(gameService.computeLeader().getdpName());
            gameService.nukePlayers();
        }      

        if(!gameService.killFeed.isEmpty()){
            Map.Entry<String,String> entry = gameService.killFeed.entrySet().iterator().next();
            String key = entry.getKey();
            String value = entry.getValue();
            killFeedObj KFO=new killFeedObj(key,value,5);

            simpMessagingTemplate.convertAndSend("/topic/gameKillFeedback", KFO ); 
            gameService.killFeed.remove(key);

        }
    }

    @Scheduled(fixedRate = 16)
    public void sendPlayerLocations() throws InterruptedException{

        List<Player> damagedPlayers = gameService.checkPlayerBulletCollisions();
        //below code is useless
        // for (Player p : damagedPlayers)
	// {
	// ResponseMessage rm = new ResponseMessage(HtmlUtils.htmlEscape(p.getHp()+""));
	// simpMessagingTemplate.convertAndSendToUser(p.getname(), "/queue/bullet",rm);
        // }


	if (!damagedPlayers.isEmpty())
	    simpMessagingTemplate.convertAndSend("/topic/gameBullets", gameService.getBulletList());

	
        gameService.updateBullets();
        gameService.predictPos();

	List<Player> playersToBeStopped = gameService.checkPlayerOutOfBounds();
	gameService.checkPlayerWallCollisions();
	
	if (!playersToBeStopped.isEmpty())
	{
	    simpMessagingTemplate.convertAndSend("/topic/gameState", gameService.getPlayerList());
	}
	
    }

    
    
    @Scheduled(fixedRate = 100)
    public void sendBulletList() throws InterruptedException{
        simpMessagingTemplate.convertAndSend("/topic/gameBullets", gameService.getBulletList());
    }



    @MessageMapping("/private-message")
    @SendToUser("/topic/private-messages")
    public ResponseMessage getPrivateMessage(final Message message,
                                             final Principal principal) throws InterruptedException {
        Thread.sleep(1000);
        notificationService.sendPrivateNotification(principal.getName());
        return new ResponseMessage(HtmlUtils.htmlEscape(
				       "Sending private message to user " + principal.getName() + ": "
				       + message.getMessageContent())
	    );
    }
}
