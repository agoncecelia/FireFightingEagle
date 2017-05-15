package com.fluskat.firefightingeagle;

import android.os.AsyncTask;

import java.net.HttpURLConnection;

/**
 * Created by Erenis on 08-Jul-16. TODO: add docs
 */
public class CheckConnectionTask extends AsyncTask<String, Void, Integer>
{
    private ConnectionListener mListener;

    public CheckConnectionTask(ConnectionListener listener)
    {
        mListener = listener;
    }

    @Override
    protected Integer doInBackground(String... strings)
    {
        try
        {
            HttpURLConnection connection = Utils.checkInternet();
            return connection.getResponseCode();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected void onPostExecute(Integer integer)
    {
        if (integer == 200)
        {
            mListener.isConnected();
        }
        else
        {
            mListener.notConnected();
        }
    }

    public interface ConnectionListener
    {
        void isConnected();

        void notConnected(/*String errorMessage*/);
    }
}
