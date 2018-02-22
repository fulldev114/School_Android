package com.adapter.parent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cloudstream.cslink.parent.ApplicationData;
import com.cloudstream.cslink.R;
import com.common.utils.GlobalConstrants;
import com.common.view.CircularImageView;

import java.util.ArrayList;

public class
        AbsentListAdapter extends BaseAdapter {
    ArrayList<Childbeans> arrayList;

    ViewHolder holder;

    Context myc;

    public AbsentListAdapter(Context c, ArrayList<Childbeans> messageList) {
        myc = c;
        this.arrayList = messageList;
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
        TextView tachername, subject, txtDate, txtBadge;
        CircularImageView profile_pic;
    }

    @Override
    public View getView(final int pos,  View convertview, ViewGroup arg2) {

        LayoutInflater li = (LayoutInflater) myc.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertview == null) {
            convertview = li.inflate(R.layout.absent_notice_item, null);

            holder = new ViewHolder();
            holder.tachername = (TextView) convertview.findViewById(R.id.textView_teachername);
            holder.subject = (TextView) convertview.findViewById(R.id.textView_subject);
            holder.txtDate = (TextView) convertview.findViewById(R.id.txtDate);
            holder.txtBadge = (TextView) convertview.findViewById(R.id.txtBadge);

            holder.profile_pic = (CircularImageView) convertview.findViewById(R.id.imageView1);
            holder.profile_pic.setBorderWidth(6);
			holder.profile_pic.setBorderColor(myc.getResources().getColor(R.color.yellow));


            convertview.setTag(holder);
        } else {
            holder = (ViewHolder) convertview.getTag();
        }

        holder.tachername.setText(arrayList.get(pos).sendername);
        holder.subject.setText(arrayList.get(pos).subject_name);
        holder.txtDate.setText(ApplicationData.convertFromNorweiDateTime(arrayList.get(pos).created_at, myc));

        holder.txtBadge.setVisibility(View.GONE);

        if (arrayList.get(pos).senderimage == null || arrayList.get(pos).senderimage.length()<4  ) {
            String i = "";
        }

        ApplicationData.setProfileImg(myc, ApplicationData.web_server_url + "uploads/" + arrayList.get(pos).senderimage, holder.profile_pic);

        /*holder.txtBadge.setVisibility(View.VISIBLE);
        if (arrayList.get(pos).badge == 0) {
            holder.txtBadge.setVisibility(View.GONE);
        } else if (arrayList.get(pos).badge < 10){
            holder.txtBadge.setText(arrayList.get(pos).badge + "");
        } else {
            holder.txtBadge.setText("N");
        }*/

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