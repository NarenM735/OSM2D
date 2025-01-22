// var stompClient = null;
// var notificationCount = 0;

// $(document).ready(function() {
//     console.log("Index page is ready");
//     connect();

//     $("#send").click(function() {
//         sendMessage();
//     });

//     $("#send-private").click(function() {
//         sendPrivateMessage();
//     });

//     $("#notifications").click(function() {
//         resetNotificationCount();
//     });
// });

// function connect() {
//     var socket = new SockJS('/our-websocket');
//     stompClient = Stomp.over(socket);
//     stompClient.connect({}, function (frame) {
//         console.log('Connected: ' + frame);
//         updateNotificationDisplay();
//         stompClient.subscribe('/topic/messages', function (message) {
//             showMessage(JSON.parse(message.body).content);
//         });

//         stompClient.subscribe('/user/topic/private-messages', function (message) {
//             showMessage(JSON.parse(message.body).content);
//         });

//         stompClient.subscribe('/topic/global-notifications', function (message) {
//             notificationCount = notificationCount + 1;
//             updateNotificationDisplay();
//         });

//         stompClient.subscribe('/user/topic/private-notifications', function (message) {
//             notificationCount = notificationCount + 1;
//             updateNotificationDisplay();
//         });
//     });
// }

// function showMessage(message) {
//     $("#messages").append("<tr><td>" + message + "</td></tr>");
// }

// function sendMessage() {
//     console.log("sending message");
//     stompClient.send("/ws/message", {}, JSON.stringify({'messageContent': $("#message").val()}));
// }

// function sendPrivateMessage() {
//     console.log("sending private message");
//     stompClient.send("/ws/private-message", {}, JSON.stringify({'messageContent': $("#private-message").val()}));
// }

// function updateNotificationDisplay() {
//     if (notificationCount == 0) {
//         $('#notifications').hide();
//     } else {
//         $('#notifications').show();
//         $('#notifications').text(notificationCount);
//     }
// }

// function resetNotificationCount() {
//     notificationCount = 0;
//     updateNotificationDisplay();
// }

var stompClient = null;

document.addEventListener('DOMContentLoaded', function () {
    console.log("Page Ready");
    connect();
});
var Playerlist=null;
var Bulletlist=null;
let clientID="";

function connect() {
    var socket = new SockJS('/our-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        clientID = frame.headers["user-name"];
        console.log('Connected: ' + frame);
        onConnected();
        // stompClient.subscribe('/topic/playerJoin', function (message) {
        //     showMessage(JSON.parse(message.body).content);
        // });

        // stompClient.subscribe('/user/topic/private-messages', function (message) {
        //     showMessage(JSON.parse(message.body).content);
        // });
        stompClient.subscribe('/topic/gameState', function (message) {
            Playerlist=JSON.parse(message.body);
        });
        stompClient.subscribe('/topic/gameBullets', function (message) {
            Bulletlist=JSON.parse(message.body);
        });
    });

}
let flag=0;
function onConnected() {
    flag=1;
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/playerJoin', function (message) {
        showMessage(JSON.parse(message.body).content);
    });
    // Tell your username to the server
    stompClient.send('/ws/playerJoin',
        {},
        JSON.stringify({messageContent: "hello"})
    )


    // connectingElement.classList.add('hidden');
}

let message_time = 5    ;
var show_msg = "CONNECTING...";

function showMessage(msg)
{
    message_time = 3;
    show_msg = msg;
    
}

var red=null;
var green=null;
var blue=null;

function setup() {
    createCanvas(1280, 650);
    background(220);
    frameRate(60);

    
    red = Math.floor(Math.random() * 255);
    green = Math.floor(Math.random() * 255);
    blue = Math.floor(Math.random() * 255);
}

function charIsDown(charachter)
{
    return keyIsDown(charachter.toUpperCase().charCodeAt(0))
}

var x_pos = 100;
var y_pos = 100;
const speed = 3;



class Bullet {
    constructor(x, y, velx, vely)
    {
	this.x = x;
	this.y = y;
	this.velx = velx;
	this.vely = vely;
    }

    getX(){
        return this.x;
    }

    getY(){
        return this.y;
    }
    getVX(){
        return this.velx;
    }
    getVY(){
        return this.vely;
    }
    
    update()
    {
	this.x += this.velx;
	this.y += this.vely;

	
    }



    render()
    {
	fill(255, 100, 100);
	circle(this.x, this.y, 7);
    }
}

function renderMessage(){}
// {
//     if (frameCount%60==0 && message_time > 0)
//     {
// 	fill(100,100,200);
// 	textSize(20);
// 	text(show_msg, 5, 25);
// 	message_time --;
//     }
//     if(message_time==0){
//     show_msg="";}
// }


function draw()
{
    background(220);

    let i=0;

    if (charIsDown("a"))
	x_pos -= speed;
    if (charIsDown("d"))
	x_pos += speed;
    if (charIsDown("w"))
	y_pos -= speed;
    if (charIsDown("s"))
	y_pos += speed;
    
    fill(100,100,255);
    
    if(flag==1){
    stompClient.send('/ws/gameState',
        {},
        JSON.stringify({x: x_pos,y:y_pos,name:clientID, r:red, g:green, b:blue})
    )
    console.log(JSON.stringify({x: x_pos,y:y_pos,name:clientID,r:red,g:green,b:blue}));
    
}
    
    if(Playerlist==null){
        circle(x_pos, y_pos, 20); 
    }else{
    // circle(x_pos, y_pos, 20);
    while(i<Playerlist.length)
    {
        var player = null;
        if(Playerlist[i]!=null){
            player = Playerlist[i];
            fill(player.r, player.g, player.b);
            circle(player.x, player.y, 20);
        }
        i++;
    }
}

    // if(Bulletlist!=null){
    // for (let b of Bulletlist)
    // {
	// // b.update();
    // // stompClient.send('/ws/gameState',{},JSON.stringify({x: b.getX(),y:b.getY(),velx: b.getVX(), vely:b.getVY()}));
    // // stompClient.send('/ws/gameState',{},JSON.stringify({x: b.getX(),y:b.getY()}));
    // // if()
	// Bulletlist[b].render();
    // }

    let j=0;
    var bullet=null
    if(Bulletlist!=null){
        while(j<Bulletlist.length){
            bullet=Bulletlist[j];
            circle(bullet.x,bullet.y,7);
            j++;
        }
    }


    fill(100,100,200);
    
    textSize(20);
    text(show_msg, 5, 25);
    if (frameCount%60==0 && message_time > 0)
        {
        
        
        message_time --;
        }
        if(message_time==0){
        show_msg="";}
}

const bullet_speed = 10;

function mouseClicked()
{
    if (mouseButton == LEFT)
    {
	var dir_x = mouseX - x_pos;
	var dir_y = mouseY - y_pos;

	var mag = sqrt(dir_x * dir_x + dir_y * dir_y)

	var vel_x = dir_x / mag * bullet_speed;
	var vel_y = dir_y / mag * bullet_speed;
	
	// bullets.push(new Bullet(x_pos, y_pos, vel_x, vel_y))
    stompClient.send('/ws/gameBullets',{},JSON.stringify({x: x_pos,y:y_pos,velx: vel_x, vely:vel_y}));
    }
}
