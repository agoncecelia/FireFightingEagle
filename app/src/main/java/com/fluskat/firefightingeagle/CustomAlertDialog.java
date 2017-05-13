package com.fluskat.firefightingeagle;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;

/**
 * Created by Erenis on 08-Feb-16. TODO: more
 */
public class CustomAlertDialog extends AlertDialog
{
    private Dialog dialog;

    private Builder builder;

    private CustomQuestionListener mQuestionListener;

    public CustomAlertDialog(Context context)
    {
        super(context);
        init(context);
    }

    private void init(Context context)
    {
        builder = new Builder(context);
        builder.setCancelable(false);
    }

    public void setCancelable(boolean cancelable)
    {
        builder.setCancelable(cancelable);
    }

    public void initInformationDialog(String text, String confirm)
    {
//        mQuestionListener = listener;
        View view = getLayoutInflater().inflate(R.layout.dialog_information, null);
        TextView tv = (TextView) view.findViewById(R.id.widget_text_view_information);
        Button goToProfile = (Button) view.findViewById(R.id.widget_button_go_to_profile_dialog);

        goToProfile.setText(confirm);
        tv.setText(text);
        goToProfile.setOnClickListener(mOnClickListener);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    public void initQuestionDialog(String msg, String confirmButtonText, String declineButtonText, boolean isCancelable,
                                   CustomQuestionListener listener)
    {
        mQuestionListener = listener;

        View view = getLayoutInflater().inflate(R.layout.dialog_question, null);

        TextView textView = (TextView) view.findViewById(R.id.widget_text_view_information_question_dialog);
        textView.setText(msg);

        Button buttonOk = (Button) view.findViewById(R.id.widget_button_ok_question_dialog);
        Button buttonDismiss = (Button) view.findViewById(R.id.widget_button_dismiss_question_dialog);

        buttonOk.setText(confirmButtonText);
        buttonDismiss.setText(declineButtonText);

        buttonDismiss.setOnClickListener(mOnClickListener);
        buttonOk.setOnClickListener(mOnClickListener);

        builder.setView(view);
        builder.setCancelable(isCancelable);

        dialog = builder.create();
        dialog.show();
    }

    public interface CustomQuestionListener
    {
        void onOkClicked() throws Exception;

        void onCancel() throws JSONException;
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int id = v.getId();

            switch (id)
            {
                case R.id.widget_button_ok_question_dialog:
                    try
                    {
                        mQuestionListener.onOkClicked();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    break;
                case R.id.widget_button_dismiss_question_dialog:
                    try
                    {
                        mQuestionListener.onCancel();
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    break;
            }
            dialog.dismiss();
        }
    };
}