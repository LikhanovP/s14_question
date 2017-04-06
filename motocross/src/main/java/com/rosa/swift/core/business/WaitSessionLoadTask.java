package com.rosa.swift.core.business;

import android.os.AsyncTask;

import com.rosa.swift.core.data.DataRepository;

/**
 * Created by inurlikaev on 08.06.2016.
 */
public class WaitSessionLoadTask extends AsyncTask<IWaitSessionLoadParam, Void, Boolean> {
    private IWaitSessionLoadParam param;
    private Exception exception;

    @Override
    protected Boolean doInBackground(IWaitSessionLoadParam... params) {
        try {
            param = params[0];
            int i = 0;
            do {
                if (DataRepository.getInstance().getSessionId() != null) break;
                Thread.sleep(200);
                i++;
            } while (i < 50);

            return DataRepository.getInstance().getSessionId() != null;
        } catch (InterruptedException unhandled) {
            //empty
        } catch (Exception e) {
            exception = e;
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean loggedIn) {
        if (param != null) {
            if (loggedIn) {
                param.execute();
            } else {
                param.onException(exception);
            }
        }
    }

    @Override
    protected void onCancelled() {
        if (param != null) {
            param.onCancelled();
        }
    }
}

