package com.qdi.rajapay.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.qdi.rajapay.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeBannerAdapter extends PagerAdapter {
    public ArrayList<JSONObject> arr;
    Context context;

    public HomeBannerAdapter(ArrayList<JSONObject> arr,Context context) {
        this.arr=arr;
        this.context = context;
    }

    @Override
    public int getCount() {
        return arr.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.banner_item,null);

        /**
         * @author Liao Mei
         * @note add click event for each pager
         */
        // <code>
        view.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //this will log the page number that was click
                Log.i("TAG", "This page was clicked: " + position);
                if(arr.get(position).has("url")){
                    Intent intent = null;
                    try {
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(arr.get(position).getString("url")));
                        context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        // </code>

        ImageView image = view.findViewById(R.id.image);
        try {
            if(!arr.get(position).isNull("image"))
                Picasso.get()
                        .load(arr.get(position).getString("image"))
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .fit().centerInside()
                        .into(image);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view,0);

        return view;
    }
}
