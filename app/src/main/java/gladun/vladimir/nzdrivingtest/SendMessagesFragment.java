package gladun.vladimir.nzdrivingtest;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Send sms or e-mail to contact the author
 *
 * @author Vladimir Gladun vvgladoun@gmail.com
 */
public class SendMessagesFragment extends Fragment implements View.OnClickListener {

    private static final String SEND_MENU_TITLE = "Send report";
    private static final String TAG = "SEND MESSAGE";

    private EditText mMessageText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // get send message fragment layout
        View fragment_view = inflater.inflate(R.layout.fragment_send_menu, container, false);

        getActivity().setTitle(SEND_MENU_TITLE);
        // get buttons from activity's layout
        Button btnSMS = (Button) fragment_view.findViewById(R.id.send_menu_sms_btn);
        Button btnEMAIL = (Button) fragment_view.findViewById(R.id.send_menu_email_btn);

        // set implemented method onClick as the onClickListener
        btnSMS.setOnClickListener(this);
        btnEMAIL.setOnClickListener(this);
        // get edit text widget
        mMessageText = (EditText) fragment_view.findViewById(R.id.send_edit_text);
        // instance should be retained
        setRetainInstance(true);
        return fragment_view;
    }

    @Override
    public void onClick(View v) {
        // action depends on the view's id
        switch (v.getId()) {
            case R.id.send_menu_sms_btn:
                // open send sms fragment
                sendSMS();
                break;
            case R.id.send_menu_email_btn:
                // open send email fragment
                sendEmail();
                break;
        }
    }

    /**
     * Replace current fragment with the next one
     *
     * @param fragment - next fragment
     */
    private void startFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null); //add to stack for proper back navigation
        transaction.commit();
    }

    /**
     * Send email to the author
     */
    private void sendEmail() {
        String message = mMessageText.getText().toString();
        if (message.isEmpty()) {
            Toast.makeText(getActivity(), "Enter your message first!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent emailIntent = new Intent(Intent.ACTION_SEND);//.ACTION_SEND
        String[] TO = {getString(R.string.send_email_address)};

        emailIntent.setData(Uri.parse("mailto:")); // to slect only from messengers
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.send_email_subject));
        emailIntent.putExtra(Intent.EXTRA_TEXT, mMessageText.getText().toString());

        try {
            startActivityForResult(Intent.createChooser(emailIntent, "Choose application to send email:"), 0);
            //return to main menu
            getFragmentManager().popBackStack();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "There is no email client on this device!", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "No apps to send message");
        }
    }

    /**
     * Send sms to the author
     */
    private void sendSMS() {
        //get message
        String message = mMessageText.getText().toString();
        if (message.isEmpty()) {
            //if empty - show warning
            Toast.makeText(getActivity(), "Enter your message first!", Toast.LENGTH_SHORT).show();
            return;
        }
        String contactPhoneNumber = getString(R.string.send_sms_phone);
        int smsLength = message.length();

        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> smsArray = smsManager.divideMessage(message);

        try {
            if (smsArray.size() > 1) {
                // send simple sms
                smsManager.sendTextMessage(contactPhoneNumber, null, message, null, null);
            } else {
                // send multiple sms
                smsManager.sendMultipartTextMessage(contactPhoneNumber, null, smsArray, null, null);
            }
            //return to main menu
            getFragmentManager().popBackStack();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "SMS faild, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            Log.e(TAG, "SMS was not sent!");
        }
    }

}
