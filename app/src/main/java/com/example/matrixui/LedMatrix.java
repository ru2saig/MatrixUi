package com.example.matrixui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class LedMatrix {

    int[] dimensions;
    int ledWidth;
    int ledHeight;
    int[] pos;
    Paint bgColor = new Paint();
    Paint strokePaint = new Paint();
    ArrayList<Led> Leds = new ArrayList<>();


    public LedMatrix(int[] _pos,int[] _dimensions) {

        this.dimensions = _dimensions;
        this.pos = _pos;
        bgColor.setStyle(Paint.Style.FILL);
        bgColor.setColor(0xFFd6d6d6);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(0xFFcfcfcf);
        strokePaint.setStrokeWidth(10);

        int _ledWidth = (dimensions[0]-pos[0])/ 3 ;
        int _ledHeight = (dimensions[1]-pos[1])/ 3 ;


        for(int i=0;i<=3;i++){
           for(int k = 0; k<3;k++) {
               Leds.add(new Led(pos[0] + _ledWidth * i - 5, pos[1] + _ledHeight * k, _ledWidth * (i + 1) + pos[0] - 5, pos[1] + _ledHeight * (k + 1)));
           }

        }

    }


    public void draw(Canvas canvas){

        canvas.drawRect(pos[0],pos[1],dimensions[0],dimensions[1],bgColor);
        for(int l = 0; l<9;l++) {
            Leds.get(l).draw(canvas);
        }
        canvas.drawRect(pos[0],pos[1] ,dimensions[0] ,dimensions[1],strokePaint);

    }

    public void updateLeds(){
        for (Led _led:Leds) {

            _led.update();

        }
    }

    public ArrayList<Led> getLeds(){

        return Leds;

    }



}



