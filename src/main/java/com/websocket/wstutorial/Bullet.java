package com.websocket.wstutorial;

import java.awt.Point;

public class Bullet {
    private float x;
    private float y;
    private float velx;
    private float vely;
    
    Bullet(){}

    Bullet(float x,float y,float velx,float vely){
        this.x = x;
        this.y = y;
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
}
