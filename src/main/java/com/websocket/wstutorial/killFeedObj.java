package com.websocket.wstutorial;

public class killFeedObj {
    public String killer;
    public String killed;
    public int time;
    
   public killFeedObj(String killer,String killed,int time){
    this.killed=killed;
    this.killer=killer;
    this.time=time;
   } 

   public void setkiller(String killer){
    this.killer=killer;
   }
   
   public void setkilled(String killed){
    this.killed=killed;
   }
   
   public void settime(int time){
    this.time=time;
   }

   public String getkiller(){
    return killer;
   }
   
   public String getkilled(){
    return killed;
   }

   
   public int gettime(){
    return time;
   }
}
