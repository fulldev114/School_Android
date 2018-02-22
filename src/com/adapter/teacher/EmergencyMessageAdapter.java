package com.adapter.teacher;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.cloudstream.cslink.teacher.ApplicationData;
import com.cloudstream.cslink.R;

import java.util.ArrayList;

/**
 * Created by etech on 15/7/16.
 */
public class EmergencyMessageAdapter extends BaseAdapter {
    private final Context context;
    private final String[] text_title;
    private ArrayList<Childbeans> listmessage= new ArrayList<Childbeans>();
    private Integer selectedPosition=-1;
    Integer[] img_draw={R.drawable.fire,R.drawable.alert,R.drawable.water_flood};

    public EmergencyMessageAdapter(Context context, ArrayList<Childbeans> listmessage)
    {
        this.context=context;
        this.listmessage=listmessage;
        text_title = context.getResources().getStringArray(R.array.emergency_message);
    }

    @Override
    public int getCount() {
        return listmessage.size();
    }

    @Override
    public Object getItem(int position) {
        return listmessage.get(position);
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
            holder.txt_time=(TextView)convertView.findViewById(R.id.txt_time);
            holder.rd_btn.setVisibility(View.GONE);
            holder.txt_time.setVisibility(View.VISIBLE);

            holder.view_bottom.setVisibility(View.GONE);

            convertView.setTag(holder);
        }
        else
        {
            holder= (ViewHolder) convertView.getTag();
        }

        Childbeans childbean= listmessage.get(position);

       /* if(position==listmessage.size()-1)
        {
            holder.view_bottom.setVisibility(View.GONE);
        }
        else
            holder.view_bottom.setVisibility(View.VISIBLE);*/


        holder.txt_msg.setText(childbean.message_desc);

        if(childbean.created_at!=null && childbean.created_at.length()>0)
            holder.txt_time.setText(ApplicationData.convertToNorweiDate(childbean.created_at,context));

        for(int i=0;i<text_title.length;i++)
        {
            if(text_title[i].equalsIgnoreCase(childbean.message_desc))
            {
                holder.img_emrgency.setImageResource(img_draw[i]);
            }
        }

        return convertView;
    }


    class ViewHolder
    {

        public RadioButton rd_btn;
        public ImageView img_emrgency;
        public TextView txt_msg;
        public View view_bottom;
        public TextView txt_time;
    }
}
