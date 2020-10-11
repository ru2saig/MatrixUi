package com.example.matrixui;

import android.graphics.Bitmap;
import android.graphics.Canvas;


public class LedSprite {

    private Bitmap ledON;
    private Bitmap ledOFF;
    private Bitmap ledCurrent;
    private int posX;
    private int posY;
    private int width;
    private int height;

    //this variable is for scaling the image for a certain
    //side length depending on canvas size( canvas.getWidth();)
    private float scalefactor;


    public LedSprite(Bitmap temp_ledON,Bitmap temp_ledOFF, int temp_posX, int temp_posY){
        this.ledON = temp_ledON;
        this.ledOFF = temp_ledOFF;
        this.posX = temp_posX;
        this.posY = temp_posY;
        this.scalefactor = 5.0f;
        this.ledCurrent = this.ledOFF;

    }

    public void draw(Canvas canvas){


        this.width = (int)this.scalefactor*this.ledCurrent.getWidth();
        this.height = (int)this.scalefactor*this.ledCurrent.getHeight();

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(ledCurrent,width,height,false);
        canvas.drawBitmap(scaledBitmap,this.posX,this.posY,null);

    }


    //change the image bitmap to button one
    public void switchOn(){

    }

    //change the image bitmap to button 0
    public void switchOff(){

    }



}
