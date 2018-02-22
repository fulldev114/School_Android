package com.adapter.teacher;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.cloudstream.cslink.R;

/**
 * Created by etech on 15/7/16.
 */
public class EmergencyAdapter extends BaseAdapter {
    private Context context;
    private final Integer[] img_draw;
    private final String[] text_title;
    private Integer selectedPosition=-1;

    public EmergencyAdapter(Context context, Integer[] img_draw, String[] text_title)
    {
        this.context=context;
        this.img_draw=img_draw;
        this.text_title=text_title;
    }

    public void updatedata(Context context) {
        this.context=context;
        selectedPosition=-1;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return text_title.length;
    }

    @Override
    public Object getItem(int position) {
        return text_title[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder = null;
        if(convertView==null)
        {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.adres_item_system_layout,parent,false);
            holder.rd_btn=(RadioButton)convertView.findViewById(R.id.rd_btn);
            holder.img_emrgency=(ImageView)convertView.findViewById(R.id.img_emrgency);
            holder.txt_msg=(TextView)convertView.findViewById(R.id.txt_msg);
            holder.view_bottom=(View)convertView.findViewById(R.id.view_bottom);

            convertView.setTag(holder);
        }
        else
        {
            holder= (ViewHolder) convertView.getTag();
        }

       /* if(position==text_title.length-1)
        {
            holder.view_bottom.setVisibility(View.GONE);
        }
        else
            holder.view_bottom.setVisibility(View.VISIBLE);*/

        holder.img_emrgency.setImageResource(img_draw[position]);
        holder.txt_msg.setText(text_title[position]);

        if(selectedPosition==-1)
        {
            holder.rd_btn.setChecked(false);
        }
        holder.rd_btn.setChecked(position == selectedPosition);
        holder.rd_btn.setTag(position);
        holder.rd_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPosition = (Integer) view.getTag();
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    public String sendmessage(Activity activity) {
        String message = "";
        if(selectedPosition!=-1)
        {
            message=text_title[selectedPosition];
        }
        return message;
    }


    class ViewHolder
    {

        public RadioButton rd_btn;
        public ImageView img_emrgency;
        public TextView txt_msg;
        public View view_bottom;
    }
}
