package com.qa.appstudent.network;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qa.appstudent.data.Question;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class HttpService extends Thread {

    public static String webUrl;
    private static  String request;
    private static Question data;

    public HttpService(String webUrl, String request, Question question) {
        this.webUrl = webUrl;
        this.request = request;
        this.data = question;
    }

    public void run() {

            try {

                        URL url = new URL( webUrl);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        //  urlConnection.setRequestMethod(request);
                        if (request == "POST") {
                            urlConnection.setDoOutput(true);
                        }
                        urlConnection.setDoInput(true);
                        urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        urlConnection.setRequestProperty("Accept", "application/json");
                        // JSONObject credential = new JSONObject();
                        //  credential.put("username","test");
                        //  credential.put("password", "test");
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        String jsonData = gson.toJson(data);
                        BufferedWriter out =
                                new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
                        out.write(jsonData);
                        out.close();
                        urlConnection.connect();
                        int status = urlConnection.getResponseCode();
                        urlConnection.disconnect();

                        if (status == 200 || status == 201) {
                            Log.d ("result: ", "Successfully uploaded!");
                        } else {
                            Log.d ("result: ", "Error in uploading!");
                        }

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        Log.d("InputStream", e.getLocalizedMessage());
                    } finally {
            }
            }

    }


