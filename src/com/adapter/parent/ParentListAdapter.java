package com.adapter.parent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cloudstream.cslink.parent.ApplicationData;
import com.cloudstream.cslink.R;
import com.common.utils.GlobalConstrants;
import com.common.view.CircularImageView;

import java.util.ArrayList;

public class ParentListAdapter extends BaseAdapter {
    private boolean flag=true;
    ArrayList<Childbeans> arrayList;

    ViewHolder holder;

    Context myc;

    public ParentListAdapter(Context c, ArrayList<Childbeans> messageList, boolean flag) {
        myc = c;
        this.arrayList = messageList;
        this.flag=flag;
    }

    public void updateListAdapter(ArrayList<Childbeans> messageList) {
        this.arrayList.clear();
        this.arrayList.addAll(messageList);
        this.notifyDataSetChanged();
    }

    public int getCount() {
        return arrayList.size();
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView teachername, subject;
        CircularImageView profile_pic;
        public ImageView img_right_arrow;
        public LinearLayout lin_parent_mobile;
        public TextView textView_childname;
    }

    @Override
    public View getView(final int pos,  View convertview, ViewGroup arg2) {

        LayoutInflater li = (LayoutInflater) myc.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertview == null) {
            convertview = li.inflate(R.layout.parent_list_item, null);

            holder = new ViewHolder();
            holder.teachername = (TextView) convertview.findViewById(R.id.textView_teachername);
            holder.subject = (TextView) convertview.findViewById(R.id.textView_subject);
            holder.img_right_arrow=(ImageView)convertview.findViewById(R.id.img_right_arrow);
            holder.lin_parent_mobile=(LinearLayout)convertview.findViewById(R.id.lin_parent_mobile);
            holder.profile_pic = (CircularImageView) convertview.findViewById(R.id.imageView1);
            holder.profile_pic.setBorderWidth(6);
			holder.profile_pic.setBorderColor(myc.getResources().getColor(R.color.color_blue_p));
            holder.textView_childname=(TextView)convertview.findViewById(R.id.textView_childname);

            convertview.setTag(holder);
        } else {
            holder = (ViewHolder) convertview.getTag();
        }

        holder.teachername.setText(arrayList.get(pos).sendername);
        holder.subject.setText(arrayList.get(pos).child_moblie);
        holder.textView_childname.setText(arrayList.get(pos).child_name);

        if (arrayList.get(pos).senderimage == null || arrayList.get(pos).senderimage.length()<4  ) {
            String i = "";
        }

        ApplicationData.setProfileImg(myc, ApplicationData.web_server_url + "uploads/" + arrayList.get(pos).senderimage, holder.profile_pic);

        /*holder.lin_parent_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             *//*   ApplicationData.showMessage(myc,myc.getResources().getString(R.string.call),
                        arrayList.get(pos).message_desc,getResources().getString(R.string.str_ok));
                ApplicationData.showMessage();*//*
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + arrayList.get(pos).child_moblie));
                myc.startActivity(intent);
            }
        });
*/
        return convertview;
    }

    private void setProfileImg(final CircularImageView profile_pic, final String url) {
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Bitmap doInBackground(Void... params) {

                if (!GlobalConstrants.isWifiConnected(myc))
                    return null;

                Bitmap bm = null;

                bm = ApplicationData.loadBitmap(url);

                if (bm == null)
                    bm = BitmapFactory.decodeResource(myc.getResources(), R.drawable.cslink_avatar_unknown);

                return bm;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);

                if ( result != null )
                {
                    profile_pic.setImageBitmap(result);
                    profile_pic.setBorderWidth(5);
                    profile_pic.setBorderColor(myc.getResources().getColor(R.color.color_blue));
                } else {
//					 GlobalFunc.showToast(mActivity, R.string.network_error, false);
                }
            }


        }.execute();
    }
}