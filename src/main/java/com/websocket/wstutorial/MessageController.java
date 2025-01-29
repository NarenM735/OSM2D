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
import java.util.ArrayList;

@Controller
public class MessageController {
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
    public ResponseMessage getName(final Message message,final Principal principal) throws InterruptedException {
        Thread.sleep(25);
        // notificationService.sendGlobalNotification();
        return new ResponseMessage(HtmlUtils.htmlEscape(principal.getName()+" Joined"));
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

    


    
    @Scheduled(fixedRate = 50)
    public void sendPlayer() throws InterruptedException{
        
    simpMessagingTemplate.
    convertAndSend("/topic/gameState", gameService.getPlayerList());
    }

    @Scheduled(fixedRate = 16)
    public void sendLocation() throws InterruptedException{
        
        
        Player p =gameService.bulletReg();
        if(p.getname()!="null"){
            ResponseMessage rm = new ResponseMessage(HtmlUtils.htmlEscape(p.getHp()+""));
        simpMessagingTemplate.convertAndSendToUser(p.getname(), "/queue/bullet",rm);
        simpMessagingTemplate.convertAndSend("/topic/gameBullets", gameService.getBulletList());
        
        }
        gameService.nextBullet();
        gameService.predictPos();
    }

    
    
    @Scheduled(fixedRate = 1000)
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
