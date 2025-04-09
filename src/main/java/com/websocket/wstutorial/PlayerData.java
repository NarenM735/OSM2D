
package com.websocket.wstutorial;


public class PlayerData
{

    private float x,y;
    private float velX, velY;

    private float gunAngle;

    private String uuid;

    public PlayerData(
	String uuid,
	float x,
	float y,
	float velX,
	float velY,
	float gunAngle)
    {
	this.uuid = uuid;
	this.x = x;
	this.y = y;
	this.velX = velX;
	this.velY = velY;
	this.gunAngle = gunAngle;
    }

    public void setuuid(String uuid)
    {
	this.uuid = uuid;
    }

    public void setx(float x)
    {
	this.x = x;
    }

    public void sety(float y)
    {
	this.y = y;
    }

    public void setvelX(float velX)
    {
	this.velX = velX;
    }

    public void setvelY(float velY)
    {
	this.velY = velY;
    }
    
    public void setgunAngle(float gunAngle)
    {
	this.gunAngle = gunAngle;
    }

    public String getuuid()
    {
	return uuid;
    }
    
    public float getx()
    {
	return x;
    }

    public float gety()
    {
	return y;
    }

    public float getvelX()
    {
	return velX;
    }

    public float getvelY()
    {
	return velY;
    }

    public float getgunAngle()
    {
	return gunAngle;
    }
    
}
