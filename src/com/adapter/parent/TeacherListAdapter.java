package com.adapter.parent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TeacherListAdapter extends BaseAdapter {
    private ArrayList<Childbeans> arrchildbadge = new ArrayList<Childbeans>();
    private boolean flag = true;
    ArrayList<Childbeans> arrayList = new ArrayList<Childbeans>();

    ViewHolder holder;

    Context myc;

    public TeacherListAdapter(Context c, ArrayList<Childbeans> messageList, boolean flag, ArrayList<Childbeans> arrchildbadge) {
        myc = c;
        this.arrayList = messageList;
        this.flag = flag;
        this.arrchildbadge = arrchildbadge;
    }

    public void updateListAdapter(ArrayList<Childbeans> messageList, ArrayList<Childbeans> arrchildbadge) {
        this.arrayList.clear();
        this.arrayList.addAll(messageList);

        this.arrchildbadge.clear();
        this.arrchildbadge.addAll(arrchildbadge);

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
        TextView tachername, subject, txtDate;
        CircularImageView profile_pic;
        public ImageView img_right_arrow;
        public LinearLayout inc_gp_bdg;
        public TextView txtBadge_gp;
    }

    @Override
    public View getView(final int pos, View convertview, ViewGroup arg2) {

        LayoutInflater li = (LayoutInflater) myc.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertview == null) {
            convertview = li.inflate(R.layout.teacher_list_item, null);

            holder = new ViewHolder();
            holder.tachername = (TextView) convertview.findViewById(R.id.textView_teachername);
            holder.subject = (TextView) convertview.findViewById(R.id.textView_subject);
            holder.txtDate = (TextView) convertview.findViewById(R.id.txtDate);
            holder.img_right_arrow = (ImageView) convertview.findViewById(R.id.img_right_arrow);
            holder.profile_pic = (CircularImageView) convertview.findViewById(R.id.imageView1);
            holder.profile_pic.setBorderWidth(6);
            holder.profile_pic.setBorderColor(myc.getResources().getColor(R.color.color_blue_p));
            holder.inc_gp_bdg = (LinearLayout) convertview.findViewById(R.id.inc_gp_bdg);
            holder.txtBadge_gp = (TextView) convertview.findViewById(R.id.txtBadge_gp);

            convertview.setTag(holder);
        } else {
            holder = (ViewHolder) convertview.getTag();
        }

        Childbeans data = arrayList.get(pos);
        holder.tachername.setText(data.sendername);
        holder.subject.setText(data.subject_name);
        holder.txtDate.setText(ApplicationData.convertToNorweiDate(data.created_at, myc));

        if (flag)
            holder.img_right_arrow.setVisibility(View.VISIBLE);
        else
            holder.img_right_arrow.setVisibility(View.GONE);

        if(data.senderimage == null || data.senderimage.length() < 4) {
            String i = "";
        }

        ApplicationData.setProfileImg(myc, ApplicationData.web_server_url + "uploads/" + arrayList.get(pos).senderimage, holder.profile_pic);

        //set badge
        if (data.badge == 0) {
            holder.inc_gp_bdg.setVisibility(View.GONE);
        } else if (data.badge < 100)
        {
            holder.inc_gp_bdg.setVisibility(View.VISIBLE);
            holder.txtBadge_gp.setText(String.valueOf(data.badge));
        } else {
            holder.txtBadge_gp.setText("N");
        }

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

                if (result != null) {
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