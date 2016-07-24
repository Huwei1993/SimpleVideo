package com.chengxinping.aiyouvideo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 平瓶平瓶子 on 2016/7/23.
 */
public class VideoItemAdapter extends ArrayAdapter<VideoItem> {
    private final LayoutInflater mInflater;
    private final int mResource;

    public VideoItemAdapter(Context context, int resource, List<VideoItem> objects) {
        super(context, resource, objects);
        mInflater = LayoutInflater.from(context);
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(mResource, parent, false);
        }
        VideoItem item = getItem(position);

        TextView tittle = (TextView) convertView.findViewById(R.id.video_title);
        tittle.setText(item.name);

        TextView createdTime = (TextView) convertView.findViewById(R.id.video_date);
        createdTime.setText(item.createdTime);

        ImageView thumb = (ImageView) convertView.findViewById(R.id.video_thumb);
        thumb.setImageBitmap(item.thumb);

        return convertView;
    }
}
