package com.websocket.wstutorial;

public class Bullet {
    private int x;
    private int y;
    // private int velx;
    // private int vely;

    
    Bullet(){}

    Bullet(int x,int y,int velx,int vely){
        this.x=x;
        this.y=y;
        // this.velx=velx;
        // this.vely=vely;
    }


    public int getx(){
        return x;
    }

    
    public int gety(){
        return y;
    }
    
    // public int getvelx(){
    //     return velx;
    // }
    
    // public int getvely(){
    //     return vely;
    // }

    public void setx(int x){
        this.x=x;
    }
    
    public void sety(int y){
        this.y=y;
    }
    
    // public void setvelx(int velx){
    //     this.velx=velx;
    // }
    
    // public void setvely(int vely){
    //     this.vely=vely;
    // }
}
