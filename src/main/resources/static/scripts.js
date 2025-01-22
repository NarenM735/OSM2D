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


var x_pos = 100;
var y_pos = 100;
const speed = 3;
var bulletEntity=null;
var self = {x: x_pos, y: y_pos, r: 100, g:100, b:255};

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
        stompClient.subscribe('/topic/gameBulletSingle', function (message) {
            bulletEntity=JSON.parse(message.body);
        });
    });

}

var flag=0;
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

// function renderMessage()
// {
// }

function handleMovement()
{
    if (charIsDown("a"))
	x_pos -= speed;
    if (charIsDown("d"))
	x_pos += speed;
    if (charIsDown("w"))
	y_pos -= speed;
    if (charIsDown("s"))
	y_pos += speed;

}


function drawPlayer(player)
{
    fill(player.r, player.g, player.b);
    circle(player.x, player.y, 20);
}

function updateBullet(bullet)
{
    bullet.x += bullet.velx;
    bullet.y += bullet.vely;
}

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
    }
     self = {x: x_pos, y: y_pos, r: 100, g:100, b:255};


    if(bulletEntity!=null){
        bullets.push(bulletEntity);
        bulletEntity=null;
    }
    if (bullets.length!=0)
    {
        for(const bullet of bullets)
        {   
            circle(bullet.x,bullet.y,7);
	        updateBullet(bullet);
            
        }
    }
    console.log("drawing");
    if(players.length!=0){
            for(const player of players)
            {
            if(!player)
                continue;
            
            if (player.name == clientID) // skip yourself
                continue;
            
            drawPlayer(player);
            }
        }
    drawPlayer(self);
    
    // renderMessage();

    
    if (frameCount%60==0 && message_time > 0)  //this is shit ; not working as intended
        {
        fill(100,100,200); 
        textSize(20);
        text(show_msg, 5, 25);
        message_time --;
        }
        if(message_time==0){
        show_msg="shit"; //this is shit ; not working as intended
        }
}

const bullet_speed = 10;

function spawnBullet()
{
    var dir_x = mouseX - x_pos;
    var dir_y = mouseY - y_pos;

    var mag = sqrt(dir_x * dir_x + dir_y * dir_y)

    var vel_x = dir_x / mag * bullet_speed;
    var vel_y = dir_y / mag * bullet_speed;
    stompClient.send('/ws/gameBullets',{},JSON.stringify({x: x_pos,y:y_pos,velx: vel_x, vely:vel_y}));

    bullets.push({x:x_pos,y:y_pos,velx:vel_x,vely:vel_y});
}

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
