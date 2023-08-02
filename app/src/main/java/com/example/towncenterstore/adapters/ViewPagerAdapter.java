package com.example.towncenterstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.towncenterstore.listeners.ClickBtnSkip;
import com.example.towncenterstore.R;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;


public class ViewPagerAdapter extends PagerAdapter {
    Context context;
    String[] textView1;
    String[] textView2;
    int[] images;
    int[] flag;
    WormDotsIndicator dotsIndicator;


    LayoutInflater inflater;
    ClickBtnSkip clickBtnSkip;

    public ViewPagerAdapter(Context context, String[] textView1, String[] textView2, int[] images, ClickBtnSkip btnSkip) {
        this.context = context;
        this.textView1 = textView1;
        this.textView2 = textView2;
        this.clickBtnSkip = btnSkip;
        this.images = images;


    }

    public WormDotsIndicator getDotsIndicator() {
        return dotsIndicator;
    }

    public void setDotsIndicator(WormDotsIndicator dotsIndicator) {
        this.dotsIndicator = dotsIndicator;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int[] getImages() {
        return images;
    }

    public void setImages(int[] images) {
        this.images = images;
    }

    public String[] getTextView1() {
        return textView1;
    }

    public void setTextView1(String[] textView1) {
        this.textView1 = textView1;
    }

    public String[] getTextView2() {
        return textView2;
    }

    public void setTextView2(String[] textView2) {
        this.textView2 = textView2;
    }

    public int[] getFlag() {
        return flag;
    }

    public void setFlag(int[] flag) {
        this.flag = flag;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public void setInflater(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public ClickBtnSkip getClickBtnSkip() {
        return clickBtnSkip;
    }

    public void setClickBtnSkip(ClickBtnSkip clickBtnSkip) {
        this.clickBtnSkip = clickBtnSkip;
    }

    @Override
    public int getCount() {
        return textView1.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {




        // Declare Variables
        TextView textView1;
        TextView textView2;
        ImageView imageView;
        Button btnNext;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.item_slide_images_welcome, container,
                false);

        textView1 = itemView.findViewById(R.id.tvTitle);
        textView2 = itemView.findViewById(R.id.tvSubTitle);
        imageView = itemView.findViewById(R.id.imgSliderPhoto);
        btnNext = itemView.findViewById(R.id.btnNext);
        dotsIndicator = itemView.findViewById(R.id.dotsId);
        dotsIndicator.setViewPager((ViewPager) container);


        textView1.setText(this.textView1[position]);
        textView2.setText(this.textView2[position]);
        imageView.setImageResource(this.images[position]);
        if (position==2){
            btnNext.setText("Finish");
        }else {
            btnNext.setText("Next");
        }


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickBtnSkip.clickBtnSkip(position);
            }
        });


        ((ViewPager) container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((ConstraintLayout) object);

    }

}