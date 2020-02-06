package com.example.sensorok;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    public Ball ball = null;
    public Handler handler = new Handler();
    public Timer timer = null;
    public TimerTask timerTask = null;
    public int keperyoSzelesseg, kepernyoMagassag;
    public android.graphics.PointF ballPos,ballSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //teljesképernyő , title bar eltünik
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //eltüntetés
        getWindow().setFlags(0xFFFFFFFF, WindowManager.LayoutParams.FLAG_FULLSCREEN);   //teljesképernyő

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FrameLayout frameLayout = findViewById(R.id.frameLayout);

        //képernyő méret lekérése

        Display display = getWindowManager().getDefaultDisplay();
        keperyoSzelesseg = display.getWidth();
        kepernyoMagassag = display.getHeight();
        ballPos = new android.graphics.PointF();
        ballSpeed = new android.graphics.PointF();

        //labda pozíció és sebesség beállítása

        ballPos.x = keperyoSzelesseg/2;
        ballPos.y = kepernyoMagassag/2;
        ballSpeed.x = 0;
        ballSpeed.y = 0;

        //labda elkészítése

        ball = new Ball(MainActivity.this,ballPos.x,ballPos.y, 25);

        frameLayout.addView(ball);      //labda hozzáadása a képernyőhöz
        frameLayout.invalidate();       // meghívja az onDraw metódust a Ball osztályban

        //sebességmérő esemény létrehozása

        ((SensorManager) getSystemService(Context.SENSOR_SERVICE)).registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                // golyó sebesség változtatás
                ballSpeed.x = -sensorEvent.values[0];
                ballSpeed.y = sensorEvent.values[1];
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
                //semmit
            }
        },((SensorManager) getSystemService(Context.SENSOR_SERVICE)).getSensorList(Sensor.TYPE_ACCELEROMETER).get(0),SensorManager.SENSOR_DELAY_NORMAL);


        //onTouchListener

        frameLayout.setOnTouchListener(new android.view.View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ballPos.x = motionEvent.getX();
                ballPos.y = motionEvent.getY();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                ballPos.x += ballSpeed.x;
                ballPos.y += ballSpeed.y;

                //kiesik oldalról akkor a másik oldalán jön vissza

                if (ballPos.x >keperyoSzelesseg) ballPos.x=0;
                if (ballPos.y >kepernyoMagassag) ballPos.y=0;

                if (ballPos.x <0 ) ballPos.x=keperyoSzelesseg;
                if (ballPos.y <0 ) ballPos.y=kepernyoMagassag;

                ball.x = ballPos.x;
                ball.y = ballPos.y;

                ball.invalidate();
            }
        };
        timer.schedule(timerTask,10,10);                //timer indítása
        super.onResume();
    }
}
