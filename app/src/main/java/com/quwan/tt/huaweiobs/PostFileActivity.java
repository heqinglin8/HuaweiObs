/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.quwan.tt.huaweiobs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.AuthTypeEnum;
import com.obs.services.model.PutObjectResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This sample demonstrates how to post object under specified bucket from
 * OBS using the OBS SDK for Android.
 {
 "credential": {
 "access": "2ZDJC3S0OZSSBPQGSNOY",
 "expires_at": "2021-04-13T04:02:05.148000Z",
 "secret": "CazDMA6KgJSyc6abJ8fEezVBFmJeM8qU1kKeSEaf",
 "securitytoken": "ggpjbi1ub3J0aC00SSl7ImFjY2VzcyI6IjJaREpDM1MwT1pTU0JQUUdTTk9ZIiwibWV0aG9kcyI6WyJ0b2tlbiJdLCJwb2xpY3kiOnsiVmVyc2lvbiI6IjEuMSIsIlN0YXRlbWVudCI6W3siQ29uZGl0aW9uIjp7IlN0cmluZ0VxdWFscyI6eyJvYnM6cHJlZml4IjpbInB1YmxpYyJdfX0sIkFjdGlvbiI6WyJvYnM6b2JqZWN0OioiXSwiUmVzb3VyY2UiOlsib2JzOio6KjpvYmplY3Q6KiJdLCJFZmZlY3QiOiJBbGxvdyJ9XX0sInJvbGUiOltdLCJyb2xldGFnZXMiOltdLCJ0aW1lb3V0X2F0IjoxNjE4Mjg2NTI1MTQ4LCJ1c2VyIjp7ImRvbWFpbiI6eyJpZCI6IjYyYTE3YjRjMDg0MDRmMjY5NzZhM2VmMDkzZWZhYjZhIiwibmFtZSI6Imh3Y2xvdWRzX2ViZyJ9LCJpZCI6IjA4ZjUzZmQyNDgwMDBmM2UxZjgwYzAxYzYyZGFjOWIzIiwibmFtZSI6InN3eDUzMjk0MDUiLCJwYXNzd29yZF9leHBpcmVzX2F0IjoiIn19n1uoiznqqPHd7wX1MQKJA8Z_df6AbLgjSxRdxl64Vup5O7DIWjNrI2aJZfB6BHJh9i9J_L6w8TAVL8wzNkgsJ8Aw6eEcCLyG9tgQ1syzYJKaTgL_6rxZcT7XzmC0JrPa434QEG8lgVCDI4SjGCIFmF35y8-hCqXnSWKQSyK0n1EMhLzBi5KTDnbrIwk3LXqqksswOPhd8FJZLqi0cDPnLJlGIi9VgmKu_ngjznEK_4xJ-alDgCdrdqOcjbD_fv8i9IlScSl7b7K47GRHD34T3SIatbV7uvREtmcjrvskea1J3Yqj_Mc9lvZ1XNVnG3ntDDJ4qdM5Xz_NUNudcD_N2Q=="
 }
 }
 */
public class PostFileActivity extends Activity
{
    
    private static final String TAG = "PostFileActivity";
    private static final String endPoint = "https://obs.cn-north-4.myhuaweicloud.com";
    private String securityToken ;
    private static String bucketName = "huwwww";

    private static ObsClient obsClient;
    
    private static StringBuffer sb;
    
    private static AuthTypeEnum authType = AuthTypeEnum.OBS;
    private TextView filePathTv;
    private TextView uploadTv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_for_demo);
        
        sb = new StringBuffer();
        uploadTv = findViewById(R.id.tv);
        final EditText AccessKey = findViewById(R.id.AccessKey);
        final EditText SecretKey = findViewById(R.id.SecretKey);
        final EditText securityTokenEd = findViewById(R.id.securityToken);
        Button fileSelectBtn = findViewById(R.id.fileSelectBtn);
        filePathTv = findViewById(R.id.filePath);

        uploadTv.setText("Click to start upload file");
        
        uploadTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(filePathTv.getText().toString().isEmpty()){
                    Toast.makeText(PostFileActivity.this,"请选择文件", Toast.LENGTH_SHORT).show();
                    return;
                }

                uploadTv.setText("uploading file...");
                String ak = AccessKey.getText().toString().trim();
                String sk = SecretKey.getText().toString().trim();
                securityToken = securityTokenEd.getText().toString().trim();
                if(TextUtils.isEmpty(securityToken)){
                    obsClient = new ObsClient(ak, sk, endPoint);
                }else{
                    obsClient = new ObsClient(ak, sk, securityToken, endPoint);
                }

                uploadTv.setClickable(false);
                AsyncTask<Void, Void, String> task = new PostObjectTask();
                task.execute();
            }
        });

        fileSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //intent.setType(“image/*”);//选择图片
                //intent.setType(“audio/*”); //选择音频
                //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
                //intent.setType(“video/*;image/*”);//同时选择视频和图片
                intent.setType("*/*");//无类型限制
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
            }
        });
    }
    
    class PostObjectTask extends AsyncTask<Void, Void, String>
    {
        
        @Override
        protected String doInBackground(Void... params)
        {
            try
            {
//                File sampleFile = createSampleFile();
                String filePath = filePathTv.getText().toString().trim();
//                filePath = toConvertPath(filePath);
                File sampleFile = new File(filePath);
                Log.i(TAG,"name = "+sampleFile.getName()+" filePath = "+sampleFile.getPath());
                Long timeStart = System.currentTimeMillis();
                PutObjectResult result = obsClient.putObject(bucketName, sampleFile.getName(), sampleFile); // localfile为待上传的本地文件路径，需要指定到具体的文件名
                if(result == null){
                    sb.append("上传的文件失败");
                    return sb.toString();
                }
                Long timeEnd = System.currentTimeMillis();
                long delayTime = timeEnd - timeStart;
                sb.append("成功，上传的文件是：\n"+ sampleFile.getName() + "\nrequestId = "+result.getRequestId()+"\n当前时间戳是：" + timeEnd +"\n 当前时间是："+ getTimeString(timeEnd)+"\n timeStart："+timeStart+"\n delayTime："+delayTime);
                Log.i(TAG,"fileName："+ sampleFile.getName() + "\nrequestId = "+result.getRequestId() + "\ncurrentTimeMillis：" + timeEnd + "\n 当前时间是："+ getTimeString(timeEnd)+"\n timeStart："+timeStart+"\n delayTime："+delayTime);
                return sb.toString();
            }
            catch (ObsException e)
            {
                sb.append("\n\n");
                sb.append("Response Code:" + e.getResponseCode())
                    .append("\n\n")
                    .append("Error Message:" + e.getErrorMessage())
                    .append("\n\n")
                    .append("Error Code:" + e.getErrorCode())
                    .append("\n\n")
                    .append("Request ID:" + e.getErrorRequestId())
                    .append("\n\n")
                    .append("Host ID:" + e.getErrorHostId());
                return sb.toString();
            }
            catch (Exception e)
            {
                sb.append("\n\n");
                sb.append(e.getMessage());
                return sb.toString();
            }
            finally
            {
                if (obsClient != null)
                {
                    try
                    {
                        /*
                         * Close obs client
                         */
                        obsClient.close();
                    }
                    catch (IOException e)
                    {
                    }
                }
            }
            
        }
        
        @Override
        protected void onPostExecute(String result)
        {
            uploadTv.setText(result);
            uploadTv.setMovementMethod(ScrollingMovementMethod.getInstance());
            uploadTv.setClickable(true);
        }

        @Override
        protected void onPreExecute() {
            sb.setLength(0);

        }

        private File createSampleFile()
            throws IOException
        {
            File file = File.createTempFile("obs-android-sdk-", ".txt");
            file.deleteOnExit();
            Writer writer = new OutputStreamWriter(new FileOutputStream(file));
            writer.write("abcdefghijklmnopqrstuvwxyz\n");
            writer.write("0123456789011234567890\n");
            writer.close();
            return file;
        }
        
    }

    String path;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if ("file".equalsIgnoreCase(uri.getScheme())){//使用第三方应用打开
                path = uri.getPath();
                filePathTv.setText(path);
                Toast.makeText(this,path+"11111",Toast.LENGTH_SHORT).show();
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                path = getPath(this, uri);
                filePathTv.setText(path);
                uploadTv.setText("Click to start upload file");
                Toast.makeText(this,path,Toast.LENGTH_SHORT).show();
            } else {//4.4以下下系统调用方法
                path = getRealPathFromURI(uri);
                filePathTv.setText(path);
                Toast.makeText(PostFileActivity.this, path+"222222", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(null!=cursor&&cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private String toConvertPath(String filePath){
        String[] dataStr = filePath.split("/");
        String fileTruePath = "/sdcard";
        for(int i=4;i<dataStr.length;i++){
            fileTruePath = fileTruePath+"/"+dataStr[i];
        }
        return fileTruePath;
    }

    private String getTimeString(long time){
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA);
        Date date1 = new Date(time);
        return format.format(date1);
    }
    
}
