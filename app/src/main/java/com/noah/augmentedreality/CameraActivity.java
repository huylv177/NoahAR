package com.noah.augmentedreality;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.vr.sdk.base.sensors.HeadTracker;

import java.util.List;

/**
 * Created by HuyLV-CT on 7/21/2016.
 */
public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener, View.OnTouchListener {
    private static final float ALPHA = 0.25f;
    public int i = 0;
    public TextView orientationText;
    public HeadTracker headTracker;
    private FrameLayout frameLayout;
    private ImageView mLoaderGraphic;
    private boolean checkOpenGLVersion = true;
    private GLSurfaceView mSurfaceView;
    private RajawaliTransparentSurfaceRenderer mRenderer;
    private SurfaceView cameraView;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private SensorManager sensorManager;
    private Sensor magnetSensor;
    private Sensor orientationSensor;
    private float[] valuesAccelerometer;
    private float[] valuesMagneticField;
    private float[] matrixR;
    private float[] matrixI;
    private float[] matrixValues;
    private float[] results;
    private Button btLeft;
    private Button btRight;
    private Button btUp;
    private Button btDown;
    private Button btForward;
    private Button btBackward;
    private rajawali.Camera mCamera;
    private float azimuth;
    private float pitch;
    private float roll;
    private float[] Rot;
    private List<Sensor> sensors;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        frameLayout = new FrameLayout(this);
        setContentView(frameLayout);

        initLoaderView();

        cameraView = new SurfaceView(this);
        surfaceHolder = cameraView.getHolder();
        surfaceHolder.addCallback(this);
        cameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.autoFocus(null);
            }
        });


        mSurfaceView = new GLSurfaceView(this);
        mSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mSurfaceView.setZOrderOnTop(true);
        mSurfaceView.setEGLContextClientVersion(2);
        mRenderer = new RajawaliTransparentSurfaceRenderer(this);
        mRenderer.setSurfaceView(mSurfaceView);
        mSurfaceView.setRenderer(mRenderer);


        frameLayout.addView(mSurfaceView);
        frameLayout.addView(cameraView);
        cameraView.setOnTouchListener(this);
        addButton();


        matrixR = new float[9];
        matrixI = new float[9];
        matrixValues = new float[3];
        valuesAccelerometer = new float[3];
        valuesMagneticField = new float[3];
        mCamera = mRenderer.getCamera();

        results = new float[3];
        Rot = new float[9];
    }

    private void addButton() {
        orientationText = new TextView(this);
        orientationText.setText("SSSSSSSSSS");
        orientationText.setTextColor(Color.WHITE);
        ViewGroup.LayoutParams params1 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        orientationText.setLayoutParams(params1);
        frameLayout.addView(orientationText);

        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.direction_layout, null);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(300, 300);
        params.gravity = Gravity.BOTTOM;
        v.setLayoutParams(params);

        btLeft = (Button) v.findViewById(R.id.btyp);
        btRight = (Button) v.findViewById(R.id.btym);
        btUp = (Button) v.findViewById(R.id.btzp);
        btDown = (Button) v.findViewById(R.id.btzm);
        btForward = (Button) v.findViewById(R.id.btxp);
        btBackward = (Button) v.findViewById(R.id.btxm);

        btLeft.setOnTouchListener(new RepeatListener(400, 10, this));
        btRight.setOnTouchListener(new RepeatListener(400, 10, this));
        btUp.setOnTouchListener(new RepeatListener(400, 10, this));
        btDown.setOnTouchListener(new RepeatListener(400, 10, this));
        btForward.setOnTouchListener(new RepeatListener(400, 10, this));
        btBackward.setOnTouchListener(new RepeatListener(400, 10, this));

        frameLayout.addView(v);
        headTracker = HeadTracker.createFromContext(this);


    }

//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        switch (event.sensor.getType()) {
//            case Sensor.TYPE_ACCELEROMETER:
////                for (int i = 0; i < 3; i++) {
////                    valuesAccelerometer[i] = event.values[i];
////                }
//                valuesAccelerometer = applyLowPassFilter(event.values,valuesAccelerometer);
//                break;
//            case Sensor.TYPE_MAGNETIC_FIELD:
////                for (int i = 0; i < 3; i++) {
////                    valuesMagneticField[i] = event.values[i];
////                }
//                valuesMagneticField = applyLowPassFilter(event.values,valuesMagneticField);
//                break;
//        }
//
//        boolean success = SensorManager.getRotationMatrix(
//                matrixR,
//                matrixI,
//                valuesAccelerometer,
//                valuesMagneticField);
//
//        if (success) {
//            SensorManager.getOrientation(matrixR, matrixValues);
//
//            float azimuth = (float) Math.toDegrees(matrixValues[0]);
//            float pitch = (float) Math.toDegrees(matrixValues[1]);
//            float roll = (float) Math.toDegrees(matrixValues[2]);
//
//            mRenderer.getCamera().setRotation(roll,mRenderer.getCamera().getRotY(),mRenderer.getCamera().getRotZ());
//        }


//        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            valuesAccelerometer = lowPass(event.values.clone(), valuesAccelerometer);
//        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
//            valuesMagneticField = lowPass(event.values.clone(), valuesMagneticField);
//        }
//        if (valuesAccelerometer != null && valuesMagneticField != null) {
//            SensorManager.getRotationMatrix(matrixR, matrixI, valuesAccelerometer, valuesMagneticField);
//            SensorManager.remapCoordinateSystem(matrixR, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Z, Rot);
//            SensorManager.getOrientation(matrixR, results);
//            azimuth = (float) (((results[0] * 180) / Math.PI) + 180);
//            pitch = (float) (((results[1] * 180 / Math.PI)) + 90);
//            roll = (float) (((results[2] * 180 / Math.PI)) + 180);
//            mCamera.setRotation(mCamera.getRotX(), azimuth - 360, mCamera.getRotZ());
//            orientationText.setText(azimuth + "\n" + pitch + "\n" + roll + "\n" + mRenderer.getCamera().getRotX() + "\n" + mRenderer.getCamera().getRotY() + "\n" + mRenderer.getCamera().getRotZ());
//        }
//    }


    private void initLoaderView() {
        mLoaderGraphic = new ImageView(CameraActivity.this);
        mLoaderGraphic.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mLoaderGraphic.setScaleType(ImageView.ScaleType.CENTER);
        mLoaderGraphic.setImageResource(R.mipmap.loader);
        frameLayout.addView(mLoaderGraphic);
        AnimationSet animSet = new AnimationSet(false);
        RotateAnimation anim1 = new RotateAnimation(360, 0,
                Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF,
                .5f);
        anim1.setRepeatCount(Animation.INFINITE);
        anim1.setDuration(2000);
        animSet.addAnimation(anim1);
        mLoaderGraphic.setAnimation(animSet);


        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (checkOpenGLVersion) {
            ConfigurationInfo info = am.getDeviceConfigurationInfo();
            if (info.reqGlEsVersion < 0x20000)
                throw new Error("OpenGL ES 2.0 is not supported by this device");
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
        camera.setDisplayOrientation(0);
        Camera.Parameters parameters = camera.getParameters();
        for (Camera.Size s : parameters.getSupportedPreviewSizes()) {
            Log.e("cxz", "ccc" + s.width + " " + s.height);
        }

        Utils.ASE = getRealVerticalViewAngle(parameters.getVerticalViewAngle());
//        Log.e("cxz","ccxc"+parameters.getHorizontalViewAngle()+ " "+parameters.getVerticalViewAngle()+ " "+Utils.ASE);

        parameters.setPreviewSize(1920, 1080);
        camera.setParameters(parameters);
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public void hideLoader() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoaderGraphic.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mRenderer.getObjectAt(event.getX(), event.getY());
                break;
            }
            case MotionEvent.ACTION_UP: {
                mRenderer.selectedObject = null;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                mRenderer.moveObject(event.getX(), event.getY());
                Log.e("cxz", "move:" + event.getX() + " " + event.getY());
            }
        }

        return true;
    }


    @Override
    public void onClick(View v) {
//        float x = mCamera.getPosition().x;
//        float y = mCamera.getPosition().y;
//        float z = mCamera.getPosition().z;
        float x = mRenderer.m2.getPosition().x;
        float y = mRenderer.m2.getPosition().y;
        float z = mRenderer.m2.getPosition().z;
//        Vec worldPos = new Vec(x,y,z);
//        worldPos.deviceToWorld();

        Vec worldPos = mRenderer.m2.getWPosition();

        switch (v.getId()) {
            case R.id.btxm:
//                mCamera.setPosition(x, y, z + 1);
                worldPos.x -= 1;
                break;
            case R.id.btxp:
                worldPos.x += 1;
//                mCamera.setPosition(x, y, z - 1);
                break;
            case R.id.btyp:
//                mCamera.setPosition(x + 1, y, z);
                worldPos.y += 1;
                break;
            case R.id.btym:
                worldPos.y -= 1;
//                mCamera.setPosition(x - 1, y, z);
                break;
            case R.id.btzm:
                worldPos.z -= 1;
//                mCamera.setPosition(x, y - 1, z);
                break;
            case R.id.btzp:
                worldPos.z += 1;
//                mCamera.setPosition(x, y + 1, z);
                break;
        }
        mRenderer.m2.setPositionByW(worldPos);
        orientationText.setText("camera:" + x + " " + y + " " + z);
    }


    @Override
    protected void onPause() {
        super.onPause();
//        sensorManager.unregisterListener(this, orientationSensor);
//        sensorManager.unregisterListener(this, magnetSensor);
        sensorManager = null;
        headTracker.stopTracking();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensors = sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
        if (sensors.size() > 0) magnetSensor = sensors.get(0);
        sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) orientationSensor = sensors.get(0);
//        sensorManager.registerListener(this, magnetSensor, SensorManager.SENSOR_DELAY_FASTEST);
//        sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_FASTEST);
        headTracker.startTracking();
    }

    float getRealVerticalViewAngle(float originalAngle) {
        return 2 * (float) Math.toDegrees(Math.atan(0.75f * Math.tan(Math.toRadians(originalAngle / 2))));
    }
}
