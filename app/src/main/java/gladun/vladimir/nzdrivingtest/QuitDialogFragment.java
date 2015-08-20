package gladun.vladimir.nzdrivingtest;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Dialog to quit test mode
 *
 * @author vvgladoun@gmail.com
 */
public class QuitDialogFragment extends DialogFragment implements View.OnClickListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // do not close dialog
        this.setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.fragment_exit, container, false);

        //find buttons
        Button btnOk = (Button)dialog.findViewById(R.id.exit_ok_btn);
        Button btnCancel = (Button)dialog.findViewById(R.id.exit_cancel_btn);
        //set listeners
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        return dialog;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        //remove the default title
        TextView title = (TextView)dialog.findViewById(android.R.id.title);
        title.setVisibility(View.GONE);

        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exit_ok_btn:
                getActivity().finish();
                break;
            case R.id.exit_cancel_btn:
                dismiss();
                break;
        }
    }
}
