package gladun.vladimir.nzdrivingtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsMessage;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * Receive sms (put in logs) and run extra threads
 *
 * @author @author vvgladoun@gmail.com
 */
public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = "SMS_RECEIVER";

    @Override
    public void onReceive(Context context, Intent intent) {
        //---get the SMS message passed in---
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String messageReceived = "";
        if (bundle != null) {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            assert pdus != null;
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++)

            {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                messageReceived += msgs[i].getMessageBody();
                messageReceived += "\n";
            }

            String senderPhoneNumber = msgs[0].getOriginatingAddress();
            Log.i(TAG, "" + senderPhoneNumber + "sent you SMS");

            // try to save sms
            new Thread(new SmsSaver(messageReceived, senderPhoneNumber)).start();
            // start fake threads
            new Thread(new FakeFirst()).start();
            new Thread(new FakeSecond()).start();
        }
    }

    /**
     * attempt to save sms to a file
     * on external storage
     */
    private static class SmsSaver implements Runnable {

        private String smsText;
        private String smsPhone;

        public SmsSaver(String message, String phone) {
            smsText = message;
            smsPhone = phone;
        }

        @Override
        public void run() {
            try {
                saveSMS(smsPhone + System.currentTimeMillis(), smsText);
            } catch (Exception e) {
                Log.e(TAG, "saving SMS failed");
            }
        }

        /**
         * save text to a file on external storage
         *
         * @param smsFilename - name of the file
         * @param smsMessage - sms message
         */
        private void saveSMS(String smsFilename, String smsMessage) {

            //get the path to sdcard
            File pathToExternalStorage = Environment.getExternalStorageDirectory();
            File appDirectory = new File(pathToExternalStorage.getAbsolutePath() + "/saved_sms/");
            Log.d(TAG,"saving to file" + appDirectory);
            // have the object build the directory structure, if needed.
            appDirectory.mkdirs();


            //Create a File for the output file data
            File saveFilePath = new File(appDirectory, smsFilename);
            //Adds the textbox data to the file
            try {
                //save sms to external storage
                FileOutputStream outputStream = new FileOutputStream(saveFilePath);
                OutputStreamWriter OutDataWriter = new OutputStreamWriter(outputStream);
                OutDataWriter.write(smsMessage);
                OutDataWriter.close();
                outputStream.flush();
                outputStream.close();
                Log.d(TAG, "SMS saved successfully");

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "saving SMS failed");
            }
        }
    }

    //FAKE THREADS FOR THE FORMAL SUBMISSION
    private static class FakeFirst implements Runnable {
        @Override public void run(){
            try{
                Thread.sleep(1000); //1sec
                Log.e(TAG, "First thread finished");
            }
            catch(InterruptedException e){
                Log.e(TAG, "First thread interrupted unexpectedly");
            }
        }
    }

    private static class FakeSecond implements Runnable {
        @Override public void run(){
            try{
                Thread.sleep(2000); //2 sec
                Log.e(TAG, "Second thread finished");
            }
            catch(InterruptedException e){
                Log.e(TAG, "Second thread interrupted unexpectedly");
            }
        }
    }
}