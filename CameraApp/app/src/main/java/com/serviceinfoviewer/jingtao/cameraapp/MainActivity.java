package com.serviceinfoviewer.jingtao.cameraapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity  {

    private static  String logtag = "CameraApp";
    private static int TakePicture = 1;
    private static int pickPicture = 2;
    private Uri imageUri;
    private HashMap URIs=new HashMap();
    private int view_id=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button CameraButton=(Button)findViewById(R.id.camera);
        CameraButton.setOnClickListener(CameraListener);
        Button GalleryButton=(Button)findViewById(R.id.gallery);
        GalleryButton.setOnClickListener(GalleryListener);
    }

    private OnClickListener GalleryListener = new OnClickListener() {
        public void onClick(View v) {
            pickPhoto(v);
        }
    };


    private OnClickListener CameraListener = new OnClickListener (){
        public void onClick(View v){
            takePhoto(v);
        }
    };


    private void pickPhoto(View v){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, pickPicture);
    }

    private void takePhoto(View v){
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        String picname="picture.jpg";
        File photo = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), picname);
        if (!photo.mkdirs()) {
            Log.e(logtag, "Directory not created");
        }
        imageUri = Uri.fromFile(photo);
        Log.e(logtag,imageUri.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent,TakePicture);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        LinearLayout ll = (LinearLayout)findViewById(R.id.image_views);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 10, 0);
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageUri;
                    getContentResolver().notifyChange(selectedImage, null);
                    //ImageView imageView = (ImageView) findViewById(R.id.image_camera);
                    ImageView imageView= new ImageView(this);
                    imageView.setAdjustViewBounds(true);
                    ContentResolver cr = getContentResolver();
                    Bitmap bitmap;
                    Bitmap bitmap_large;
                    imageView.setOnTouchListener(imageTouchListener);
                    imageView.setOnClickListener(imageClickerListener);
                    try {
                        bitmap_large = MediaStore.Images.Media.getBitmap(cr, selectedImage);
                        bitmap = Bitmap.createScaledBitmap(bitmap_large,400,300,true);
                        imageView.setImageBitmap(bitmap);
                        imageView.setLayoutParams(lp);
                        LinearLayout layout = new LinearLayout(this);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                        TextView titleView = new TextView(this);
                        LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                        titleView.setLayoutParams(lparams);
                        titleView.setText("Delete ^");
                        titleView.setOnClickListener(DeleteImg);
                        titleView.setId(view_id);
                        imageView.setId(view_id);
                        URIs.put(view_id, selectedImage);
                        view_id++;
                        layout.addView(imageView);
                        layout.addView(titleView);
                        ll.addView(layout);
                        Toast.makeText(MainActivity.this, selectedImage.toString(), Toast.LENGTH_LONG).show();
                        TextView uri_text= (TextView)findViewById(R.id.uri_text);//DEBUG#####
                        uri_text.setText(URIs.toString());//DEBUG#####
                    } catch (Exception e) {
                        Log.e(logtag, e.toString());
                    }
                }
                break;
            case 2:
                if(resultCode == RESULT_OK){
                    //HorizontalScrollView ll=(HorizontalScrollView)findViewById(R.id.horizontalScrollView);
                    Uri selectedImage = intent.getData();
                    Bitmap bitmap;
                    Bitmap bitmap_large;
                    //ImageView imageView = (ImageView) findViewById(R.id.image_camera);
                    try {
                        bitmap_large = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                        bitmap = Bitmap.createScaledBitmap(bitmap_large, 400, 300, true);
                        ImageView imageView= new ImageView(this);
                        imageView.setAdjustViewBounds(true);
                        //imageView.setImageURI(selectedImage);
                        imageView.setImageBitmap(bitmap);
                        imageView.setLayoutParams(lp);
                        imageView.setOnTouchListener(imageTouchListener);
                        imageView.setOnClickListener(imageClickerListener);
                        LinearLayout layout = new LinearLayout(this);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                        TextView titleView = new TextView(this);
                        LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                        titleView.setLayoutParams(lparams);
                        titleView.setText("Delete ^");
                        titleView.setOnClickListener(DeleteImg);
                        titleView.setId(view_id);
                        imageView.setId(view_id);
                        URIs.put(view_id, selectedImage);
                        view_id++;
                        layout.addView(imageView);
                        layout.addView(titleView);
                        ll.addView(layout);
                        TextView uri_text= (TextView)findViewById(R.id.uri_text);//DEBUG#####
                        uri_text.setText(URIs.toString());//DEBUG#####

                    } catch (Exception e) {
                        Log.e(logtag, e.toString());
                    }
                }
                break;
        }
    }

    private OnClickListener DeleteImg = new OnClickListener(){
        public void onClick(View v){
            URIs.remove(v.getId());
            ViewGroup parent = (ViewGroup) v.getParent();
            parent.removeAllViews();
            TextView uri_text= (TextView)findViewById(R.id.uri_text);//DEBUG#####
            uri_text.setText(v.getId()+" removed \n"+URIs.toString());//DEBUG#####
            //Log.e(logtag, "Delete clicked");
            //Intent intent = new IntenDisplay_Imageay_Image.class);

        }
    };
    private OnClickListener imageClickerListener=new OnClickListener(){
        public void onClick(View v) {
            //Log.e(logtag, "image clicked");
            //Intent intent = new IntenDisplay_Imageay_Image.class);
            //TODO: new activity;
            Intent display = new Intent(MainActivity.this, ImageDisplay.class);
            display.putExtra("uri",URIs.get(v.getId()).toString());
            MainActivity.this.startActivity(display);

        }
    };

    private OnTouchListener imageTouchListener= new OnTouchListener(){
        public boolean onTouch(View view, MotionEvent event) {
            //Log.e(logtag, "image touched");
            //TODO: Touch Listener
            switch (event.getAction())
            {
            }
            return false;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}