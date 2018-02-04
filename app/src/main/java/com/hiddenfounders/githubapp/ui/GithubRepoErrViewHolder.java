package com.hiddenfounders.githubapp.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hiddenfounders.githubapp.R;

public class GithubRepoErrViewHolder
        extends RecyclerView.ViewHolder
        implements View.OnClickListener{
    private ProgressBar mProgressBar;
    private ImageButton mRetryBtn;

    private TextView mErrorTxt;
    private LinearLayout mErrorLayout;

    public GithubRepoErrViewHolder(View v) {
        super(v);

        mProgressBar = v.findViewById(R.id.loadmore_progress);
     //   mRetryBtn = v.findViewById(R.id.loadmore_retry);
        mErrorTxt = v.findViewById(R.id.loadmore_errortxt);
        mErrorLayout = v.findViewById(R.id.loadmore_errorlayout);

       // mRetryBtn.setOnClickListener(this);
//        mErrorLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        /*switch (view.getId()) {
            case R.id.loadmore_retry:
            case R.id.loadmore_errorlayout:

                showRetry(false, null);
                mCallback.retryPageLoad();

                break;
        }*/
    }

    public void setPbVisibility(int visibility) {
        //mProgressBar.setVisibility(visibility);
    }

    public void setErrLayoutVisibility(int visibility) {
        //mErrorLayout.setVisibility(visibility);
    }

    public void updateErrMessage(String errMessage) {
        //this.mErrorTxt.setText(errMessage);
    }
}
