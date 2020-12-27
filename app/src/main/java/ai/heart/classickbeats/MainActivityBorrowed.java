package ai.heart.classickbeats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;

public class MainActivityBorrowed extends AppCompatActivity {

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private static final String TAG = "MS TEST RGB";
    private static final int REQUEST_CAMERA_PERMISSION = 1640;
    private static final int CAMERA_FACING = CameraCharacteristics.LENS_FACING_BACK;
    private CameraDevice camera;
    private CameraCaptureSession session;
    private ImageReader imageReader;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private String cameraID;
    int count = 0;
    ImageView imageView;
    private Size imageDimension;
    private CameraManager cameraManager;
    private Button processBtn;
    private int recordTimeInMilliSeconds = 33 * 1000; // second * milli

    //     Check For FPS
//    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSSSSSS");

    private final CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            MainActivityBorrowed.this.camera = camera;
            Log.e(TAG, "Camera Open Called");
            try {
                camera.createCaptureSession(Arrays.asList(imageReader.getSurface()), stateSessionCallback, mBackgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
                Log.e(TAG, "Failed Camera Session");
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {

        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {

        }
    };
    private final CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
        }

        @Override
        public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, int sequenceId, long frameNumber) {
            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
//            Log.e(TAG,"Frame number sequence start "+frameNumber);
        }

        @Override
        public void onCaptureSequenceAborted(@NonNull CameraCaptureSession session, int sequenceId) {
            super.onCaptureSequenceAborted(session, sequenceId);
        }

        @Override
        public void onCaptureBufferLost(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull Surface target, long frameNumber) {
            super.onCaptureBufferLost(session, request, target, frameNumber);
        }
    };
    private final CameraCaptureSession.StateCallback stateSessionCallback = new CameraCaptureSession.StateCallback() {

        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            MainActivityBorrowed.this.session = session;
            Log.e(TAG, "Session Start");
            try {
                MainActivityBorrowed.this.session.setRepeatingRequest(createCaptureRequest(), captureCallback, mBackgroundHandler);
                Date date = new Date();
//                Log.e(TAG, formatter.format(date));
                mBackgroundHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopTakingImage();
                    }
                }, recordTimeInMilliSeconds);
            } catch (CameraAccessException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

        }
    };

    private final ImageReader.OnImageAvailableListener onImageAvailableListener = reader -> {
        Image img = reader.acquireLatestImage();
        count++;
        float[] means;
        // System.out.println("Image number: " + count);
        means = processImage(img);
        System.out.println(means[0] + "\t" + means[1] + "\t" + means[2]);
        img.close();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        processBtn = findViewById(R.id.process_btn);

        processBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProcess();
                processBtn.setText("PROCESS STARTED");
            }
        });

    }

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    void startProcess() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            getCameraPermission();
            return;
        }
        if (MainActivityBorrowed.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Log.e(TAG, "Has Flash Light");
            startTakingImages();
        } else {
            Log.e(TAG, "Mobile Don't Have Flash");
        }

    }

    void getCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivityBorrowed.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
        }
    }

    //  @SuppressLint("MissingPermission")
    void startTakingImages() {
        cameraManager = (CameraManager) getSystemService(MainActivity.CAMERA_SERVICE);
        try {
            cameraID = getCamera(cameraManager);
            cameraManager.setTorchMode(cameraID, true);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            cameraManager.openCamera(cameraID, cameraStateCallback, null);
            imageReader = ImageReader.newInstance(320, 240, ImageFormat.YUV_420_888, 30); //For fps * 30 sec ( SET THIS IMAGE SIZE )
            imageReader.setOnImageAvailableListener(onImageAvailableListener, mBackgroundHandler);
        } catch (CameraAccessException e){
            Log.e(TAG, e.getMessage());
        }
    }

    void stopTakingImage(){
        processBtn.setText("START PROCESS");
//      Date date = new Date();
//      Log.e(TAG,formatter.format(date));
        Log.e(TAG,"Camera Stop");
//      Toast.makeText(MainActivity.this, "Process End", Toast.LENGTH_SHORT).show();
        try {
            session.abortCaptures();
            session.close();
            camera.close();
//            stopBackgroundThread();
        } catch (CameraAccessException e){
            Log.e(TAG, e.getMessage());
        }
    }

    public String getCamera(CameraManager manager){
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                Log.e("Orientation ",characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)+"" );
                int facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing == CAMERA_FACING) {
                    return cameraId;
                }
            }
        } catch (CameraAccessException e){
            e.printStackTrace();
        }
        return null;
    }
    protected CaptureRequest createCaptureRequest() {
        try {
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraID);
            // cameraManager.setTorchMode(getCamera(cameraManager),true);
            CaptureRequest.Builder builder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
            builder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
            int orientation = getResources().getConfiguration().orientation;
            builder.set(CaptureRequest.JPEG_ORIENTATION, getJpegOrientation(characteristics,orientation) );
            builder.addTarget(imageReader.getSurface());

            // Added by Vipul
            builder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_OFF);
            builder.set(CaptureRequest.CONTROL_AWB_LOCK, Boolean.TRUE);

            return builder.build();
        } catch (CameraAccessException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }
    private int getJpegOrientation(CameraCharacteristics c, int deviceOrientation) {
        if (deviceOrientation == android.view.OrientationEventListener.ORIENTATION_UNKNOWN) return 0;
        int sensorOrientation = c.get(CameraCharacteristics.SENSOR_ORIENTATION);

        // Round device orientation to a multiple of 90
        deviceOrientation = (deviceOrientation + 45) / 90 * 90;

        // Reverse device orientation for front-facing cameras
        boolean facingFront = c.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT;
        if (facingFront) deviceOrientation = -deviceOrientation;

        // Calculate desired JPEG orientation relative to camera orientation to make
        // the image upright relative to the device orientation
        int jpegOrientation = (sensorOrientation + deviceOrientation + 360) % 360;

        return jpegOrientation;
    }

    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    float[] processImage(Image image){

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        // sRGB array needed by Bitmap static factory method I use below.
        int[] argbArray = new int[imageWidth * imageHeight];
        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
        yBuffer.position(0);

        // This is specific to YUV420SP format where U & V planes are interleaved
        // so you can access them directly from one ByteBuffer. The data is saved as
        // UVUVUVUVU... for NV12 format and VUVUVUVUV... for NV21 format.
        //
        // The alternative way to handle this would be refer U & V as separate
        // `ByteBuffer`s and then use PixelStride and RowStride to find the right
        // index of the U or V value per pixel.
        ByteBuffer uvBuffer = image.getPlanes()[1].getBuffer();
        uvBuffer.position(0);
        int r, g, b;
        int yValue, uValue, vValue;

        int r_sum = 0;
        int g_sum = 0;
        int b_sum = 0;
        int count = 0;
        for (int y = 0; y < imageHeight - 2; y++) {
            for (int x = 0; x < imageWidth - 2; x++) {
                int yIndex = y * imageWidth + x;
                // Y plane should have positive values belonging to [0...255]
                yValue = (yBuffer.get(yIndex) & 0xff);

                int uvx = x / 2;
                int uvy = y / 2;
                // Remember UV values are common for four pixel values.
                // So the actual formula if U & V were in separate plane would be:
                // `pos (for u or v) = (y / 2) * (width / 2) + (x / 2)`
                // But since they are in single plane interleaved the position becomes:
                // `u = 2 * pos`
                // `v = 2 * pos + 1`, if the image is in NV12 format, else reverse.
                int uIndex = uvy * imageWidth + 2 * uvx;
                // ^ Note that here `uvy = y / 2` and `uvx = x / 2`
                int vIndex = uIndex + 1;

                uValue = (uvBuffer.get(uIndex) & 0xff) - 128;
                vValue = (uvBuffer.get(vIndex) & 0xff) - 128;
                r = (int) (yValue + 1.370705f * vValue);
                g = (int) (yValue - (0.698001f * vValue) - (0.337633f * uValue));
                b = (int) (yValue + 1.732446f * uValue);
                r = (int) clamp(r, 0, 255);
                g = (int) clamp(g, 0, 255);
                b = (int) clamp(b, 0, 255);
                // Use 255 for alpha value, no transparency. ARGB values are
                // positioned in each byte of a single 4 byte integer
                // [AAAAAAAARRRRRRRRGGGGGGGGBBBBBBBB]
                argbArray[yIndex] = (255 << 24) | (r & 255) << 16 | (g & 255) << 8 | (b & 255);
                r_sum += r;
                g_sum += g;
                b_sum += b;
                count++;
                // float pixel_mean = (r + g + b)/3;
                // System.out.print(pixel_mean + " ");
            }
            // System.out.println("");
        }
//        Bitmap bitmap = Bitmap.createBitmap(argbArray, imageWidth, imageHeight, Config.ARGB_8888);
//        printRGB(bitmap);
        float r_mean = (float)r_sum/ (float)count;
        float g_mean = (float)g_sum/ (float)count;
        float b_mean = (float)b_sum/ (float)count;

//        String r_mean_s = "Mean: " + r_sum + "     Count: " + count;
//        Log.d("",r_mean_s);
        float[] means = new float[3];
        means[0] = r_mean;
        means[1] = g_mean;
        means[2] = b_mean;

        return means;
    }

    void printRGB(Bitmap bitmap){
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] data = new int[w * h];
        bitmap.getPixels(data, 0, w, 0, 0, w, h);

        String imageJson;

        for(int pix: data) {
            int red = Color.red(pix);
            int blue = Color.blue(pix);
            int green = Color.green(pix);
            int alpha = Color.alpha(pix);

            imageJson = "{\"red\":"+red+",\"blue\":"+blue+",\"green\":"+green+",\"alpha\":"+alpha+"},";
//            imageJson = String.valueOf(red);

//                    imagePixel.put("red", red);
//                    imagePixel.put("blue", blue);
//                    imagePixel.put("green", green);
//                    imagePixel.put("alpha", alpha);
//                    image.put(imagePixel);
//                    imagePixel = null;
//                    String RGB_STRING = "("+red+","+blue+","+green+","+alpha+")";
//                    Log.e("RGBA",RGB_STRING);
//            Log.e("RGBA","String called");

            Log.d("",imageJson);

        }

        /// Free Space
        data = null;
    }

    void closeCamera(){
        try {
            session.abortCaptures();
            session.close();
//            stopBackgroundThread();
        } catch (CameraAccessException e){
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        startBackgroundThread();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if(session != null){
                session.abortCaptures();
                camera.close();
            }
            stopBackgroundThread();
        } catch (CameraAccessException e){
            Log.e(TAG, e.getMessage());
        }
    }

}