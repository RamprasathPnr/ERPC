package com.omneagate.erbc.Activity.Dialog;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.omneagate.erbc.GifAnimation.GifWebView;
import com.omneagate.erbc.R;

/**
 * Created by user1 on 28/8/15.
 */
public class LoadGifFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_gif_anim,container, false);
        GifWebView gifView = (GifWebView)view.findViewById(R.id.gifAnimations);
        gifView.setBackgroundColor(0);
        gifView.setBackgroundResource(R.color.transparent);
        gifView.loadPath( "file:///android_asset/loader_round.gif");
        return view;
    }
}