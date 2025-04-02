package com.websocket.wstutorial;

import java.awt.Point;

public class Bullet {
    private float x;
    private float y;
    private float velx;
    private float vely;
    private String bulletID;
    
    // Bullet(){}

    Bullet(float x,float y,float velx,float vely,String bulletID){
        this.x = x;
        this.y = y;
        this.bulletID=bulletID;
        this.velx = velx;
        this.vely = vely;
    }

    
    public void update(){
        x += velx;
        y += vely;
    }
    public float getx(){
        return x;
    }

    
    public float gety(){
        return y;
    }

    public Point getPos()
    {
	return new Point((int)x,(int)y);
    }
    
    public float getvelx(){
	return velx;
    }
    
    public float getvely(){
        return vely;
    }

    public void setx(float x){
        this.x = x;
    }
    
    public void sety(float y){
        this.y = y;
    }
    
    public void setvelx(float velx){
        this.velx = velx;
    }
    
    public void setvely(float vely){
        this.vely = vely;
    }

    public void setbulletID(String bulletID){
        this.bulletID=bulletID;
    }

    public String getbulletID(){
        return bulletID;
    }
}
