package com.gigamole.neon.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.gigamole.neon.R;
import com.gigamole.neon.utils.Constants;
import com.gigamole.neon.visualizers.BarVisualizerView;
import com.gigamole.neon.visualizers.GravityDefiedVisualizerView;
import com.gigamole.neon.visualizers.PulseVisualizerView;
import com.gigamole.neon.visualizers.SeaBreathVisualizerView;
import com.gigamole.slideimageview.lib.SlideImageView;

/**
 * Created by GIGAMOLE on 17.05.2015.
 */
public class PlayerPagerAdapter extends PagerAdapter {

    private final Context context;

    public PlayerPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return Constants.PAGE_COUNT;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view;

        switch (position) {
            case Constants.COVER_PAGE:
            default:
                view = new SlideImageView(this.context);
                view.setTag(Constants.COVER_PAGE_TAG);
                ((SlideImageView) view).setAxis(SlideImageView.Axis.VERTICAL);
                ((SlideImageView) view).setSource(R.drawable.stub);
                break;
            case Constants.BAR_PAGE:
                view = new BarVisualizerView(this.context);
                view.setTag(Constants.BAR_PAGE_TAG);
                break;
            case Constants.GD_PAGE:
                view = new GravityDefiedVisualizerView(this.context);
                view.setTag(Constants.GD_PAGE_TAG);
                break;
            case Constants.SEA_PAGE:
                view = new SeaBreathVisualizerView(this.context);
                view.setTag(Constants.SEA_PAGE_TAG);
                break;
            case Constants.PULSE_PAGE:
                view = new PulseVisualizerView(this.context);
                view.setTag(Constants.PULSE_PAGE_TAG);
                break;
        }

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
