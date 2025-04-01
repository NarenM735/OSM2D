var dir_x=0;
var dir_y=0;

var angleGun=0;
var last_xspeed=0;
var last_yspeed=0;
const bullet_speed = 10;
const frame_time = 16.666666666666666666666666666667;
var playerHp =100;
var stompClient = null;
var flag1 = 0;

document.addEventListener('DOMContentLoaded', function () {
    console.log("Page Ready");
    connect();
});

var players=[];
var bullets=[];
let clientID="";

// var x_pos = 100;
// var y_pos = 100;
var x_speed = 0;
var y_speed = 0;
const speed = 3;

var bulletEntity=null;


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


var x_pos;
var y_pos;
function onConnected() {
    flag=1;
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/playerJoin', function (message) {
        // showMessage(JSON.parse(message.body).content);
        tempPlayer=JSON.parse(message.body);
        x_pos=tempPlayer.x;
        y_pos=tempPlayer.y;
         showMessage(tempPlayer.name);
    });
    // Tell your username to the server
    stompClient.send('/ws/playerJoin',
                     {},
                     JSON.stringify({messageContent: "hello"})
                    )

    // stompClient.send('/ws/gameState',
    //                  {},
    //                  JSON.stringify({x: x_pos,y:y_pos,name:clientID, r:red, g:green, b:blue,hp:playerHp,ang:angleGun})
    //                 )

    // connectingElement.classList.add('hidden');
}
var self = {x: x_pos, y: y_pos, r: 100, g:100, b:255,ang:angleGun};


var message_time = 5    ;
var show_msg = "CONNECTING...";

function showMessage(msg)
{
    message_time = 3;
    show_msg = msg;
    
}

var red=null;
var green=null;
var blue=null;
let backGroundImg;

var screen_width = 1280;
var screen_height = 720;
var gunImg;
function setup() {
    createCanvas(screen_width, screen_height);
    backGroundImg = loadImage('/mapTest1.jpg');
    gunImg=loadImage('/gun.png');
    image(backGroundImg,0,0);
    
    
    // background(220);
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
    circle(player.x - x_pos + width/2, player.y - y_pos + height/2, 20);
    push()
    // angleMode(DEGREES);
    translate(player.x - x_pos + width/2, player.y - y_pos + height/2);
    
    rotate(player.ang);

    image(gunImg,0, 0,25,20);
    
    pop()
    console.log(player.x - x_pos, player.y - y_pos)
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
    // background(255,51,54);
    background(255,87,90);
    image(backGroundImg, screen_width/2 - self.x, screen_height/2 - self.y);

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
    
    //why x:old_x_pos and y:old_y_pos
    //&& (last_xspeed!=x_speed || last_yspeed!=y_speed 
    if(flag==1 ){
        if (Math.abs(old_x_pos-x_pos) <= 3 || Math.abs(old_y_pos-y_pos) <= 3){
            if (playerHp>0){
                stompClient.send('/ws/gameState',
                    {},
                    JSON.stringify({x: x_pos,y:y_pos,velx:x_speed, vely:y_speed, name:clientID, r:red, g:green, b:blue,hp:playerHp,ang:angleGun})
                   )
            }
        }
    }

     self = {x: x_pos, y: y_pos, r: 100, g:100, b:255};
    last_xspeed=x_speed;
    last_yspeed=y_speed;
    self = {x: x_pos, y: y_pos, r: red, g:green, b:blue,hp:playerHp,ang:angleGun};


    if(bulletEntity!=null){
        bullets.push(bulletEntity);
        bulletEntity=null;
    }
    
    if (bullets.length!=0)
    {
        for(const bullet of bullets)
        {   
            circle(bullet.x - x_pos + screen_width/2, bullet.y - y_pos + screen_height/2 ,7);
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
                flag1 = 1;
                self = {x: player.x, y: player.y, r: player.r, g: player.g, b: player.b ,hp: player.hp,ang:angleGun};
		x_pos = player.x;
		y_pos = player.y;
        playerHp = player.hp;
            }
            
            let elapsed = millis() - last_time;
            // player.x += player.velx * (elapsed / frame_time);
            // player.y += player.vely * (elapsed / frame_time);
            if (player.hp>0){
                drawPlayer(player);
            }
        }
    }
    
    fill(100,100,200);
    textSize(20);
    text(show_msg, 5, 25);

    if (frameCount%60==0 && message_time > 0)  
    {
        message_time --;
    }
    if(message_time==0 && flag1){
        show_msg=self.hp; 
        flag1=0;
    }
    else if (message_time==0 && !flag1){
        playerHp = 0;
        show_msg=0;
        flag1=0;
    }


    last_time = millis();

    //angle of the gun
    dir_x = mouseX - screen_width/2;
    dir_y = mouseY - screen_height/2;

   

    angleGun = atan2(mouseY-(height/2),mouseX-(width/2));




}


function spawnBullet()

{

    var mag = sqrt(dir_x * dir_x + dir_y * dir_y);

    var vel_x = dir_x / mag * bullet_speed;
    var vel_y = dir_y / mag * bullet_speed;
    if (playerHp>0){
        stompClient.send('/ws/gameBullets',{},JSON.stringify({x:x_pos + ( (dir_x/mag)*30 ),y:y_pos+ ( (dir_y/mag)*30 ),velx:vel_x,vely:vel_y,ang:angleGun}));
    }
    

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

