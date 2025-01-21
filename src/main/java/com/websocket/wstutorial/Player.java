package com.websocket.wstutorial;

import java.util.Random;

// import java.io.Serializable;



public class Player {

    static Random random = new Random();

    private int x,y;
    private float r,g,b;
    // private int velx,vely;
    private String name;
    Player(){

    }

    Player(int x,int y,String name){
        this.x=x;
        this.y=y;
        this.name=name;

        random.setSeed(name.hashCode());
        this.r = random.nextInt();
        this.g = random.nextInt() / 2f;
        this.b = random.nextInt()/2f;
    }

    public void update(Player p)
    {
        x = p.getx();
        y = p.gety();
    }
    
    public int getx(){
        return x;
    }

    public String getname(){
        return name;
    }

    
    public int gety(){
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

    public void setx(int x){
        
        this.x=x;

    }


    public void setname(String name){
        
        this.name=name;

    }

    public void sety(int y){
        
        this.y=y;

    }

}
