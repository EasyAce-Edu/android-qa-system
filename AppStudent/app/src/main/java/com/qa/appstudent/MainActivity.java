package com.qa.appstudent;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
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
import android.widget.ArrayAdapter;
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


public class MainActivity extends AppCompatActivity {

    private static  String logtag = "CameraApp";
    private static int TakePicture = 1;
    private static int pickPicture = 2;
    private static int DisplayImage = 3;
    private Uri imageUri;
    private HashMap URIs=new HashMap();
    private int view_id=0;
    private String selected_subject="undefined";
    String[] subjects = new String[] {"Calculus", "Stat", "Computer Science","Linear Algebra"};
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView add_icon=(ImageView)findViewById(R.id.add_icon);
        add_icon.setImageResource(R.mipmap.ic_camera);
        add_icon.setOnClickListener(upload_photo);
        Button subject_btn=(Button)findViewById(R.id.select_subject);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, subjects);
        subject_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                subject_dialog();
            }
        });
    }
    private void subject_dialog(){
        new AlertDialog.Builder(this)
                .setTitle("Select A Subject")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected_subject=subjects[which];
                        Button subject_btn=(Button)findViewById(R.id.select_subject);
                        subject_btn.setText(selected_subject);
                        dialog.dismiss();
                    }
                }).create().show();
    }


    protected void onSaveInstanceState(Bundle extra) {
        super.onSaveInstanceState(extra);
        extra.putSerializable("HashMap", URIs);
        extra.putString("string", URIs.toString());
    }
    protected void onRestoreInstanceState(Bundle extra){
        //TextView uri_text= (TextView)findViewById(R.id.uri_text);
        URIs= (HashMap) extra.getSerializable("HashMap");
        //uri_text.setText(extra.getString("string"));//DEBUG#####
    }


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


    private OnClickListener upload_photo = new OnClickListener() {
        public void onClick(View v) {
            startDialog();
        }
    };

    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, pickPicture);
                    }
                });

        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        String picname = "picture.jpg";
                        File photo = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES), picname);
                        imageUri = Uri.fromFile(photo);
                        Log.e(logtag, imageUri.toString());
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, TakePicture);
                    }
                });
        myAlertDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        LinearLayout ll = (LinearLayout)findViewById(R.id.image_views);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 0, 20, 0);
        switch (requestCode) {
            case 1://take picture
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageUri;
                    getContentResolver().notifyChange(selectedImage, null);
                    //ImageView imageView = (ImageView) findViewById(R.id.image_camera);
                    ImageView imageView= new ImageView(this);
                    imageView.setAdjustViewBounds(true);
                    ContentResolver cr = getContentResolver();
                    Bitmap bitmap;
                    Bitmap bitmap_large;
                    imageView.setOnClickListener(imageClickerListener);
                    try {
                        bitmap_large = MediaStore.Images.Media.getBitmap(cr, selectedImage);
                        bitmap = Bitmap.createScaledBitmap(bitmap_large,450,450,true);
                        imageView.setImageBitmap(bitmap);
                        imageView.setLayoutParams(lp);
                        /*LinearLayout layout = new LinearLayout(this);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                        TextView titleView = new TextView(this);
                        LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                        titleView.setLayoutParams(lparams);
                        titleView.setText("Delete ^");
                        titleView.setOnClickListener(DeleteImg);
                        titleView.setId(view_id);*/
                        imageView.setId(view_id);
                        URIs.put(view_id, selectedImage);
                        view_id++;
                        /*layout.addView(imageView);
                        layout.addView(titleView);*/
                        ll.addView(imageView);
                        Toast.makeText(MainActivity.this, selectedImage.toString(), Toast.LENGTH_LONG).show();
                        //TextView uri_text= (TextView)findViewById(R.id.uri_text);//DEBUG#####
                        //uri_text.setText(URIs.toString());//DEBUG#####
                    } catch (Exception e) {
                        Log.e(logtag, e.toString());
                    }
                }
                break;
            case 2://pick from gellary
                if(resultCode == RESULT_OK){
                    //HorizontalScrollView ll=(HorizontalScrollView)findViewById(R.id.horizontalScrollView);
                    Uri selectedImage = intent.getData();
                    Bitmap bitmap;
                    Bitmap bitmap_large;
                    //ImageView imageView = (ImageView) findViewById(R.id.image_camera);
                    try {
                        bitmap_large = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                        bitmap = Bitmap.createScaledBitmap(bitmap_large, 400, 400, true);
                        ImageView imageView= new ImageView(this);
                        imageView.setAdjustViewBounds(true);
                        //imageView.setImageURI(selectedImage);
                        imageView.setImageBitmap(bitmap);
                        imageView.setLayoutParams(lp);
                        imageView.setOnClickListener(imageClickerListener);
                        /*LinearLayout layout = new LinearLayout(this);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                        TextView titleView = new TextView(this);
                        LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                        titleView.setLayoutParams(lparams);
                        titleView.setText("Delete ^");
                        titleView.setOnClickListener(DeleteImg);
                        titleView.setId(view_id);*/
                        imageView.setId(view_id);
                        URIs.put(view_id, selectedImage);
                        view_id++;
                        /*layout.addView(imageView);
                        layout.addView(titleView);*/
                        ll.addView(imageView);
                        //TextView uri_text= (TextView)findViewById(R.id.uri_text);//DEBUG#####
                        //uri_text.setText(URIs.toString());//DEBUG#####

                    } catch (Exception e) {
                        Log.e(logtag, e.toString());
                    }
                }
                break;
            case 3://display image
                try {
                    int image_id = intent.getIntExtra("delete_id", 0);
                    ImageView imageview = (ImageView) findViewById(image_id);
                    imageview.setVisibility(View.GONE);
                    URIs.remove(image_id);
                    break;
                }catch(Exception e) {
                }
        }
        check_image_limit();
    }

    private OnClickListener imageClickerListener=new OnClickListener(){
        public void onClick(View v) {
            Intent display = new Intent(MainActivity.this, ImageDisplay.class);
            display.putExtra("uri",URIs.get(v.getId()).toString());
            display.putExtra("id",v.getId());
            MainActivity.this.startActivityForResult(display, DisplayImage);

        }
    };

    private void check_image_limit(){
        ImageView add=(ImageView)findViewById(R.id.add_icon);
        if(URIs.size()>=3){
            add.setVisibility(View.INVISIBLE);
        }else{
            add.setVisibility(View.VISIBLE);
        }

    }
}
