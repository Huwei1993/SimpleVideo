package com.chengxinping.aiyouvideo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 平瓶平瓶子 on 2016/7/24.
 */
public class VideoItemAdapter extends BaseAdapter {
    private List<VideoItem> data;
    private LayoutInflater mInflater;
    private Context context;
    private CustomListView listView;
    private onDeleteLinstener deleteLinstener;

    public interface onDeleteLinstener {
        public void deleteScript(int position);
    }

    public VideoItemAdapter(CustomListView listView, Context context, List<VideoItem> data) {
        this.listView = listView;
        this.context = context;
        this.data = data;
        this.mInflater = LayoutInflater.from(context);
    }

    public VideoItemAdapter(List<VideoItem> data, Context context, CustomListView listView, onDeleteLinstener deleteLinstener) {
        this.data = data;
        this.context = context;
        this.listView = listView;
        this.deleteLinstener = deleteLinstener;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.video_item, null);
        }
        VideoItem item = (VideoItem) getItem(position);

        TextView tittle = (TextView) convertView.findViewById(R.id.video_title);
        tittle.setText(item.name);

        TextView createdTime = (TextView) convertView.findViewById(R.id.video_date);
        createdTime.setText(item.createdTime);

        ImageView thumb = (ImageView) convertView.findViewById(R.id.video_thumb);
        thumb.setImageBitmap(item.thumb);

        RelativeLayout delete = (RelativeLayout) convertView.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteLinstener != null){
                    deleteLinstener.deleteScript(position);
                    listView.slideBack();
                }
            }
        });


        return convertView;
    }

}
