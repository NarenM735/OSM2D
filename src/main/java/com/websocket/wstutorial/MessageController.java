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
import java.util.Random;
import java.awt.Point;

@Controller
public class MessageController {

    Point spawnPoint1=new Point(1500, 30);
    Point spawnPoint2=new Point(2900, 100);
    Point spawnPoint3=new Point(100, 100);
    Point spawnPoint4=new Point(30, 1500);
    Point spawnPoint5=new Point(2970, 1500);
    Point spawnPoint6=new Point(100, 2900);
    Point spawnPoint7=new Point(2900, 2900);
    Point spawnPoint8=new Point(1500, 2970);
    


    @Autowired
    private NotificationService notificationService;
    private SimpMessagingTemplate simpMessagingTemplate;
    private final Logger LOG = LoggerFactory.getLogger(MessageController.class);


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
        Thread.sleep(20);
        // notificationService.sendGlobalNotification();

        List<Point> defaultSpawn = Arrays.asList(spawnPoint1,spawnPoint2,spawnPoint3,spawnPoint4,spawnPoint5,spawnPoint6,spawnPoint7,spawnPoint8);
        Random rand = new Random();
        Point assignedSpawPoint=defaultSpawn.get(rand.nextInt(defaultSpawn.size()));
        Player p = new Player();
        p.setx((float)assignedSpawPoint.getX());
        p.sety((float)assignedSpawPoint.getY());
        p.setname(principal.getName());

        gameService.addPlayer(p);





        
        return p;
        //Add a List view of all the Players joins but a simple text

    }
    // Player postion streaming
    @MessageMapping("/gameState")
    // @SendTo("/topic/gameState")
    public void getLocation(final Player player1,final Principal principal) throws InterruptedException {
            // player1.setname(principal.getName());
           
            
           gameService.addPlayer(player1);
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
    public void sendPlayerList() throws InterruptedException {
    simpMessagingTemplate.convertAndSend("/topic/gameState", gameService.getPlayerList()); 
        if(LocalDateTime.now().isAfter(gameService.tenSecTest)){
            gameService.nukePlayers();
        }      
        for (Player p: gameService.getPlayerList()){
            if (p == null)
                continue;   
            
            if (p.getHp() == 0){
                gameService.removePlayer(p.getname());
            }
        }

}

    @Scheduled(fixedRate = 16)
    public void sendPlayerLocations() throws InterruptedException{

        List<Player> damagedPlayers = gameService.checkPlayerBulletCollisions();
        for (Player p : damagedPlayers)
	{
	    ResponseMessage rm = new ResponseMessage(HtmlUtils.htmlEscape(p.getHp()+""));
	    simpMessagingTemplate.convertAndSendToUser(p.getname(), "/queue/bullet",rm);
        }

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
