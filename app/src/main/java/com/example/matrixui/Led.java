package com.example.matrixui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.core.graphics.ColorUtils;

public class Led {


    float width;
    float height;
    boolean state; // means led is off
    int onColor; // color when on
    int offColor; //color when off
    Paint fillColor = new Paint();
    float x;
    float y;
    boolean onLed = false;

    public Led(float _x, float _y,float _width, float _height){

            this.width = _width;
            this.height = _height;
            this.x = _x;
            this.y = _y;
            this.onColor = 0xFFFFFFFF;
            this.offColor = 0xFFd6d6d6;
            this.state = false;
            fillColor.setStyle(Paint.Style.FILL);
            fillColor.setColor(onColor);
    }


    public void draw(Canvas canvas) {
        canvas.drawRect((float) x, (float) y, (float) width, (float) height, fillColor);

    }

    public void update(){

        if(state){
            fillColor.setColor(onColor);

        }else {
            fillColor.setColor(offColor);

        }

        }

    public void setState(boolean _state){
        this.state = _state;
    }

    public boolean getState(){
        return this.state;
    }

    public void changeState(){
        this.state = !state;
    }


    public void setOnLed(boolean _onLed){

        this.onLed = _onLed;
    }

    //get methods
    public boolean getLedOn(){
        return this.onLed;
    }

    public float getX(){
        return this.x;
    }

    public float getY(){
        return this.y;
    }

    public float getWidth(){
        return this.width;
    }

    public float getHeight(){
        return this.height;
    }

}
