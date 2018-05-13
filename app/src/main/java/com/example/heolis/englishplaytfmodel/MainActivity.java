package com.example.heolis.englishplaytfmodel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.tensorflow.Session;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button predictButton;
    private TextView predictTextView;
    private Classifier classifier;
    private Handler handler;
    private HandlerThread handlerThread;
    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 117;
    private static final float IMAGE_STD = 1;
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "output";
    private static final String MODEL_FILE = "file:///android_asset/toAndroid.pb";
    private static final String LABEL_FILE =
            "file:///android_asset/labels.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        predictButton = findViewById(R.id.bt_get_predict);
        predictTextView = findViewById(R.id.tv_predict);
        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputStream inputStream = null;
                try {
                    inputStream = getAssets().open("dog.jpg");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bitmap input = BitmapFactory.decodeStream(inputStream);
                input = zoomImage(input, 224, 224);
                toPredict(input);
            }
        });
    }

    private void toPredict(final Bitmap input){
        classifier = TensorFlowImageClassifier.create(
                        getAssets(),
                        MODEL_FILE,
                        LABEL_FILE,
                        INPUT_SIZE,
                        IMAGE_MEAN,
                        IMAGE_STD,
                        INPUT_NAME,
                        OUTPUT_NAME);
        List<Classifier.Recognition> results = classifier.recognizeImage(input);

        predictTextView.setText("");
        // results是列表
        for (Classifier.Recognition r : results) {
            Log.e("results", "名字:" + r.getTitle() + "    确信度:" + r.getConfidence());
            predictTextView.append("名字:" + r.getTitle() + "    确信度:" + r.getConfidence() + "\n");
        }

    }

    public Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

}
