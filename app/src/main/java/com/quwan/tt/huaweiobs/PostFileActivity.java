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

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.model.AuthTypeEnum;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * This sample demonstrates how to post object under specified bucket from
 * OBS using the OBS SDK for Android.
 * {
 *  "credential": {
 *   "access key": "7OWUGC5KQ4U7OUM5AG9F",
 *   "expires_at": "2021-04-11T14:16:29.148000Z",
 *   "secretkey": "E1kgTUW6JWRSQGhQopoS1QUGW2pHS7AVVGc2HvaU",
 *   "securitytoken": "ggljbi1lYXN0LTNJJ3siYWNjZXNzIjoiN09XVUdDNUtRNFU3T1VNNUFHOUYiLCJtZXRob2RzIjpbInRva2VuIl0sInBvbGljeSI6eyJWZXJzaW9uIjoiMS4xIiwiU3RhdGVtZW50IjpbeyJDb25kaXRpb24iOnsiU3RyaW5nRXF1YWxzIjp7Im9iczpwcmVmaXgiOlsicHVibGljIl19fSwiQWN0aW9uIjpbIm9iczpvYmplY3Q6KiJdLCJSZXNvdXJjZSI6WyJvYnM6KjoqOm9iamVjdDoqIl0sIkVmZmVjdCI6IkFsbG93In1dfSwicm9sZSI6W10sInJvbGV0YWdlcyI6W10sInRpbWVvdXRfYXQiOjE2MTgxNTA1ODkxNDgsInVzZXIiOnsiZG9tYWluIjp7ImlkIjoiNjJhMTdiNGMwODQwNGYyNjk3NmEzZWYwOTNlZmFiNmEiLCJuYW1lIjoiaHdjbG91ZHNfZWJnIn0sImlkIjoiMDhmNTNmZDI0ODAwMGYzZTFmODBjMDFjNjJkYWM5YjMiLCJuYW1lIjoic3d4NTMyOTQwNSIsInBhc3N3b3JkX2V4cGlyZXNfYXQiOiIifX0YaLTgzbu6s7CMrujAano98mOR5RFxFbIRMkMI2Bjv0OFLFWiW4Byg_QoepSWjgzSgqex0FxJkXYP_9tFgpXphCJp-FLHP5RZtj1x5zlIUQNIWLm3-yscUmAtWjiKROhwZaPQVgVzE9eP8-YtW8qOkPNyCszBT7zGPknlt0IC-Kk10MFSjB906Jlf99lnyEKPfZy_Oz086sFRwU0Ddh13_dJZsAgbkXfxQCEzCd_E52ZuoRwtEeO4G0Luv8NKcVkW4FUYwqj4-V7vwHFJ1gLbFwTurwKKs1Fo9wI9QkJ03b1JH4mE3FObznX3yP0M6ifYpuv0SDnAW0YDSPopXRA78"
 *  }
 * }
 */
public class PostFileActivity extends Activity
{
    
    private static final String endPoint = "https://obs.cn-north-4.myhuaweicloud.com";

    private String ak = "7OWUGC5KQ4U7OUM5AG9F";

    private String sk = "E1kgTUW6JWRSQGhQopoS1QUGW2pHS7AVVGc2HvaU";
    
    private static String bucketName = "quwan-mpc";

    private static String securityToken = "ggljbi1lYXN0LTNJJ3siYWNjZXNzIjoiN09XVUdDNUtRNFU3T1VNNUFHOUYiLCJtZXRob2RzIjpbInRva2VuIl0sInBvbGljeSI6eyJWZXJzaW9uIjoiMS4xIiwiU3RhdGVtZW50IjpbeyJDb25kaXRpb24iOnsiU3RyaW5nRXF1YWxzIjp7Im9iczpwcmVmaXgiOlsicHVibGljIl19fSwiQWN0aW9uIjpbIm9iczpvYmplY3Q6KiJdLCJSZXNvdXJjZSI6WyJvYnM6KjoqOm9iamVjdDoqIl0sIkVmZmVjdCI6IkFsbG93In1dfSwicm9sZSI6W10sInJvbGV0YWdlcyI6W10sInRpbWVvdXRfYXQiOjE2MTgxNTA1ODkxNDgsInVzZXIiOnsiZG9tYWluIjp7ImlkIjoiNjJhMTdiNGMwODQwNGYyNjk3NmEzZWYwOTNlZmFiNmEiLCJuYW1lIjoiaHdjbG91ZHNfZWJnIn0sImlkIjoiMDhmNTNmZDI0ODAwMGYzZTFmODBjMDFjNjJkYWM5YjMiLCJuYW1lIjoic3d4NTMyOTQwNSIsInBhc3N3b3JkX2V4cGlyZXNfYXQiOiIifX0YaLTgzbu6s7CMrujAano98mOR5RFxFbIRMkMI2Bjv0OFLFWiW4Byg_QoepSWjgzSgqex0FxJkXYP_9tFgpXphCJp-FLHP5RZtj1x5zlIUQNIWLm3-yscUmAtWjiKROhwZaPQVgVzE9eP8-YtW8qOkPNyCszBT7zGPknlt0IC-Kk10MFSjB906Jlf99lnyEKPfZy_Oz086sFRwU0Ddh13_dJZsAgbkXfxQCEzCd_E52ZuoRwtEeO4G0Luv8NKcVkW4FUYwqj4-V7vwHFJ1gLbFwTurwKKs1Fo9wI9QkJ03b1JH4mE3FObznX3yP0M6ifYpuv0SDnAW0YDSPopXRA78";

    private static ObsClient obsClient;
    
    private static StringBuffer sb;
    
    private static AuthTypeEnum authType = AuthTypeEnum.OBS;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_for_demo);
        
        sb = new StringBuffer();
        
//        ObsConfiguration config = new ObsConfiguration();
//        config.setEndPoint(endPoint);
//        config.setAuthType(authType);

        /*
        * Constructs a obs client instance with your account for accessing OBS
        */
        final TextView tv = (TextView)findViewById(R.id.tv);
        final EditText AccessKey = (EditText)findViewById(R.id.AccessKey);
        final EditText SecretKey = (EditText)findViewById(R.id.SecretKey);

        tv.setText("Click to start test");
        
        tv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ak = AccessKey.getText().toString().trim();
                sk = SecretKey.getText().toString().trim();
                obsClient = new ObsClient(ak, sk, securityToken, endPoint);
//                obsClient = new ObsClient(ak, sk, endPoint);
                tv.setClickable(false);
                AsyncTask<Void, Void, String> task = new PostObjectTask();
                task.execute();
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
                File sampleFile = createSampleFile();
                
                obsClient.putObject(bucketName, sampleFile.getName(), sampleFile); // localfile为待上传的本地文件路径，需要指定到具体的文件名
                sb.append("上传文件成功");
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
            TextView tv = (TextView)findViewById(R.id.tv);
            tv.setText(result);
            tv.setOnClickListener(null);
            tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        }
        
        private File createSampleFile()
            throws IOException
        {
//            File file = new File(getCacheDir()+"/obs-android-sdk-"+System.currentTimeMillis()+".txt");
            File file = File.createTempFile("obs-android-sdk-", ".txt");
            file.deleteOnExit();
            Writer writer = new OutputStreamWriter(new FileOutputStream(file));
            writer.write("abcdefghijklmnopqrstuvwxyz\n");
            writer.write("0123456789011234567890\n");
            writer.close();
            Log.i("hql","name = "+file.getName()+" filePath = "+file.getPath());
            return file;
        }
        
    }
    
}
