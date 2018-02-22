package com.adapter.parent;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudstream.cslink.parent.ApplicationData;
import com.cloudstream.cslink.R;
import com.common.view.CircularImageView;

import java.util.ArrayList;

/**
 * Created by etech on 24/6/16.
 */
public class GroupMessageAdapter extends BaseAdapter {
    private final Activity context;
    private final ArrayList<Childbeans> recmsg;
    boolean[] name;
    String temarray="";
    public GroupMessageAdapter(Activity context, ArrayList<Childbeans> recmsg, int i) {

        this.context=context;
        this.recmsg=recmsg;
        name = new boolean[recmsg.size()];
        int counter=0;
        while(counter<name.length)
        {
            if(!recmsg.get(counter).fromname.equals(temarray)) {
                name[counter] = true;
                temarray = recmsg.get(counter).fromname;
            }
            else
                name[counter]=false;

            counter++;
        }
    }

    @Override
    public int getCount() {
        return recmsg.size();
    }

    @Override
    public Object getItem(int position) {
        return recmsg.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
       LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        if(convertView==null)
        {
            convertView = inflater.inflate(R.layout.group_chat_list,parent,false);
            holder= new ViewHolder();
            holder.receive_msg = (TextView) convertView.findViewById(R.id.showingreceive_msg);
            holder.date_receive = (TextView) convertView.findViewById(R.id.showingrecive_msg_date);
            holder.receive_avatar = (CircularImageView) convertView.findViewById(R.id.from_img_profile);
            holder.receive_time=(TextView) convertView.findViewById(R.id.showingrecive_msg_time);
            holder.layout_receive_msg = (RelativeLayout) convertView.findViewById(R.id.show_recivemsg);
            holder.txt_sendernm=(TextView)convertView.findViewById(R.id.txt_sendernm);
            holder.rel_img=(RelativeLayout)convertView.findViewById(R.id.rel_img);

            convertView.setTag(holder);
        }
        else
        {
            holder= (ViewHolder) convertView.getTag();
        }

        Childbeans data =recmsg.get(position);

        if(name[position]==true)
        {
            holder.txt_sendernm.setVisibility(View.VISIBLE);
            holder.rel_img.setVisibility(View.VISIBLE);
            holder.txt_sendernm.setText(data.fromname);
            if(data.image!=null && data.image.length()>0)
                ApplicationData.setProfileImg(context, ApplicationData.web_server_url + "uploads/" + data.image, holder.receive_avatar);
            else
                holder.receive_avatar.setImageResource(R.drawable.cslink_avatar_unknown);
        }
        else
        {
            holder.txt_sendernm.setVisibility(View.GONE);
            holder.rel_img.setVisibility(View.INVISIBLE);
        }

        holder.date_receive.setText(ApplicationData.convertFromNorweiDateTimewithdash(data.created_at, context));

        holder.receive_msg.setText(data.message_desc.trim());

        return convertView;
    }

    class ViewHolder
    {
        public TextView  receive_msg,  date_receive,  txtFromSeen,receive_time;
        public CircularImageView  receive_avatar;
        public RelativeLayout layout_receive_msg, layout_send_msg;
        public ImageView imgToMobile;
        public TextView txt_sendernm;
        public RelativeLayout rel_img;
    }
}
