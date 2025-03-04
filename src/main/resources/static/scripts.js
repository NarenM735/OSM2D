var last_xspeed=0;
var last_yspeed=0;
const bullet_speed = 10;
const frame_time = 16.666666666666666666666666666667;

var stompClient = null;

document.addEventListener('DOMContentLoaded', function () {
    console.log("Page Ready");
    connect();
});
var players=[];
var bullets=[];
let clientID="";


var x_pos = 100;
var y_pos = 100;
var x_speed = 0;
var y_speed = 0;
const speed = 3;

var bulletEntity=null;
var playerHp=100;
var self = {x: x_pos, y: y_pos, r: 100, g:100, b:255,hp:playerHp};


function connect() {
    var socket = new SockJS('/our-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        clientID = frame.headers["user-name"];
        console.log('Connected: ' + frame);
        onConnected();
        stompClient.subscribe('/topic/gameState', function (message) {
            players=JSON.parse(message.body);
        });
        stompClient.subscribe('/topic/gameBullets', function (message) {
            bullets=JSON.parse(message.body);
        });
        stompClient.subscribe('/topic/gameBulletSingle', function (message) {
            bulletEntity=JSON.parse(message.body);
        });
        stompClient.subscribe('/user/queue/bullet', function (message) {
            playerHp=JSON.parse(message.body).content;
            showMessage(JSON.parse(message.body).content);
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

    stompClient.send('/ws/gameState',
                     {},
                     JSON.stringify({x: x_pos,y:y_pos,name:clientID, r:red, g:green, b:blue,hp:playerHp})
                    )

    // connectingElement.classList.add('hidden');
}

var message_time = 5    ;
var show_msg = "CONNECTING...";

function showMessage(msg)
{
    
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


var last_time = millis();

function drawPlayer(player)
{
    fill(player.r, player.g, player.b);
    circle(player.x, player.y, 20);
    console.log(player.x,player.y)
}

function updateBullet(bullet)
{

    let elapsed = millis() - last_time;

    bullet.x += bullet.velx * (elapsed / frame_time);
    bullet.y += bullet.vely * (elapsed / frame_time);


}



function draw()
{
    background(220);

    x_speed = 0;
    y_speed = 0;

    old_x_pos = x_pos;
    old_y_pos = y_pos;

    let elapsed = millis() - last_time;
    let multiplier = elapsed / frame_time;
    if (charIsDown("a")) {
        x_pos -= speed * multiplier; 
        x_speed += -speed;
    }if (charIsDown("d")) {
        x_pos += speed * multiplier;
        x_speed += speed;
    }
    if (charIsDown("w")) {
        y_pos -= speed * multiplier;
        y_speed += -speed;
    }
    if (charIsDown("s")) {
        y_pos += speed * multiplier;
        y_speed += speed;
    }
    
    fill(100,100,255);
    
    if(flag==1 && (last_xspeed!=x_speed || last_yspeed!=y_speed )){
        if (Math.abs(old_x_pos-x_pos) < 5 || Math.abs(old_y_pos-y_pos) < 5){
            stompClient.send('/ws/gameState',
                {},
                JSON.stringify({x: old_x_pos,y:old_y_pos,velx:x_speed, vely:y_speed, name:clientID, r:red, g:green, b:blue,hp:playerHp})
               )
        }
    }
    last_xspeed=x_speed;
    last_yspeed=y_speed;
    self = {x: x_pos, y: y_pos, r: red, g:green, b:blue,hp:playerHp};


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

    
    if(players.length!=0){
        for(const player of players)
        {
            if(!player)
                continue;
            
            
            if (player.name == clientID)
            {
                self = {x: player.x, y: player.y, r: player.r, g: player.g, b: player.b ,hp: player.hp};
		x_pos = player.x;
		y_pos = player.y;
            }
            
            let elapsed = millis() - last_time;
            // player.x += player.velx * (elapsed / frame_time);
            // player.y += player.vely * (elapsed / frame_time);
            
            drawPlayer(player);
        }
    }
    
    
    fill(100,100,200); 
    textSize(20);
    text(show_msg, 5, 25);

    if (frameCount%60==0 && message_time > 0)  
    {
        message_time --;
    }
    if(message_time==0 ){
        show_msg=self.hp; 
    }


    last_time = millis();


}


function spawnBullet()

{
    var dir_x = mouseX - x_pos;
    var dir_y = mouseY - y_pos;

    var mag = sqrt(dir_x * dir_x + dir_y * dir_y)

    var vel_x = dir_x / mag * bullet_speed;
    var vel_y = dir_y / mag * bullet_speed;
    stompClient.send('/ws/gameBullets',{},JSON.stringify({x:x_pos + ( (dir_x/mag)*30 ),y:y_pos+ ( (dir_y/mag)*30 ),velx:vel_x,vely:vel_y}));

    // bullets.push({x:x_pos + ( (dir_x/mag)*30 ),y:y_pos+ ( (dir_y/mag)*30 ),velx:vel_x,vely:vel_y});
}

// function mouseClicked()

// {
//     var dir_x = mouseX - x_pos;
//     var dir_y = mouseY - y_pos;

//     var mag = sqrt(dir_x * dir_x + dir_y * dir_y)

//     var vel_x = dir_x / mag * bullet_speed;
//     var vel_y = dir_y / mag * bullet_speed;
//     stompClient.send('/ws/gameBullets',{},JSON.stringify({x: x_pos,y:y_pos,velx: vel_x, vely:vel_y}));

//     bullets.push(new Bullet(x_pos, y_pos, vel_x, vel_y));
// }

function mouseClicked()
{
    if (mouseButton == LEFT)
    {
        spawnBullet();
        
        // bullets.push(new Bullet(x_pos, y_pos, vel_x, vel_y))
        // stompClient.send('/ws/gameBullets',{},JSON.stringify({x: x_pos,y:y_pos,velx: vel_x, vely:vel_y}));
    }
}
