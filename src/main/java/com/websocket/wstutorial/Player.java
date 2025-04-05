package com.websocket.wstutorial;

import java.awt.Point;
import java.util.Random;

public class Player {

    static Random random = new Random();

    private float x,y;
    private float r,g,b;
    private float velx,vely;
    private String name;
    private float hp;
    private float ang;
    private int score;
    
    
    Player() {
        this.hp = 100;
    }

    Player(float x, float y, String name, float hp, float velx, float vely,float ang,int score){
        this.x = x;
        this.y = y;
        this.name = name;
        this.hp = hp;
        this.velx = velx;
        this.vely = vely;
        this.ang=ang;
        this.score=score;

        random.setSeed(name.hashCode());
        this.r = random.nextInt();
        this.g = random.nextInt()/2f;
        this.b = random.nextInt()/2f;
    }

    public void update()
    {
        x += velx;
        y += vely;
    }

    public float bulletHit(){
        hp-=10;
        return hp;
    }

    public float getx(){
        return x;
    }

    public float getvelx(){
        return velx;
    }
    
    public float getvely(){
        return vely;
    }
    
    public float getHp(){
        return hp;
    }

    public void setHp(float Hp){
        if (hp<=0){
            hp = 0;
            return;
        }
        hp=Hp;
    }

    public String getname(){
        return name;
    }

    
    public float gety(){
        return y;
    }

    public float getr()
    {
        return r;
    }

    public float getg()
    {
        return g;
    }

    public float getb()
    {
        return b;
    }

    public void setr(float r)
    {
        this.r = r;
    }

    public void setg(float g)
    {
        this.g = g;
    }

    public void setb(float b)
    {
        this.b = b;
    }

    public void setx(float x){
        
        this.x=x;

    }

    public void setang(float ang){
        this.ang=ang;
    }

    public float getang(){
        return ang;
    }


    public Point getPos()
    {
	return new Point((int)x, (int)y);
    }
    
    public boolean collide(Point p)
    {
	return (13.5 >= Math.hypot(x - p.x, y - p.y));
    }
    
    public void setvely(float vely){
        
	this.vely=vely;

    }
    
    public void setvelx(float velx){
        
        this.velx=velx;

    }

    public void setname(String name){
        
        this.name=name;

    }

    public void sety(float y){
        
        this.y=y;

    }

    public void setscore(int score){
        this.score=score;
    }

    public int getscore(){
        return score;
    }

    public void incScore(){
        score+=10;
    }
    
}
