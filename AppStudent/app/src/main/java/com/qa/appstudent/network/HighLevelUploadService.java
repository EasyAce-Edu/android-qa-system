package com.qa.appstudent.network;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.qa.appstudent.data.MessageDTO;
import com.qa.appstudent.data.Question;

import java.io.File;

/**
 * Created by maoningguo on 2015-09-27.
 */

public class HighLevelUploadService {

    final Context context;
    final File file;
    final String hashId;

    public HighLevelUploadService(Context context, File file, String hashId) {
        this.context = context;
        this.file = file;
        this.hashId = hashId;
    }


    public TransferObserver processS3Service() {


        AmazonS3 s3 = new AmazonS3Client(
                new BasicAWSCredentials("123", "456"));

        TransferUtility transferManager = new TransferUtility(s3, context);
        // Starts a download
        String name = file.getName();

        final TransferObserver observer = transferManager.upload("testmaoninguo", this.hashId, file);

        observer.setTransferListener(new TransferListener() {


            @Override
            public void onStateChanged(int id, TransferState state) {
                        Log.d("State:", "Id: " + hashId + ", " + "state: " + state.toString());
                String s3Location = "https://s3.amazonaws.com/testmaoninguo/" + hashId;
                if (state == TransferState.COMPLETED) {
                    MessageDTO messageDTO = new MessageDTO("testText", s3Location);
                    Question question = new Question("math", "testPerson", messageDTO);
                    HttpService post = new HttpService("https://easyace-api-staging.herokuapp.com/questions", "POST", question);
                    post.start();
                }

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int percentage = (int) (bytesCurrent / bytesTotal * 100);
                Log.d("Id: ", +id + ", " + "percentage: " + percentage);
                //Display percentage transfered to user
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.d("hehe", "error: ", ex);
            }

        });
        return observer;
    }


}
