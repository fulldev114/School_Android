package com.cloudstream.cslink.parent;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cloudstream.cslink.R;

public class AdapterHomeScreen extends BaseAdapter {
    String[] tag;
    Integer[] image;
    Integer[] badge;
    ViewHolder holder;

    Context myc;

    public AdapterHomeScreen(Context c, String[] date, Integer[] drawerlist_image, Integer[] drawerlist_badge) {
        tag = date;
        image = drawerlist_image;
        badge = drawerlist_badge;
        myc = c;
    }


    public int getCount() {
        return tag.length;
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public void updatedata(Context c, String[] date) {
        tag = date;
        myc = c;
    }

    static class ViewHolder {
        TextView date;
        ImageView img;
        TextView txtBadge;
        public LinearLayout lin_std;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int pos, View convertview, ViewGroup arg2) {

        LayoutInflater li = (LayoutInflater) myc.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertview == null) {
            convertview = li.inflate(R.layout.homescreenlisttile, null);

            holder = new ViewHolder();
            holder.date = (TextView) convertview.findViewById(R.id.textView1);
            holder.img = (ImageView) convertview.findViewById(R.id.imageView1);
            holder.txtBadge = (TextView) convertview.findViewById(R.id.txtBadge);
            holder.lin_std = (LinearLayout) convertview.findViewById(R.id.lin_std);
            convertview.setTag(holder);
            holder.date.setTextColor(myc.getResources().getColorStateList(R.color.btn_cmd_color));

        } else
            holder = (ViewHolder) convertview.getTag();

        holder.date.setText(tag[pos]);


        int sdk = android.os.Build.VERSION.SDK_INT;

        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            holder.img.setBackgroundDrawable(myc.getResources()
                    .getDrawable(image[pos]));
        } else {
            holder.img.setBackground(myc.getResources().getDrawable(
                    image[pos]));
        }

        holder.lin_std.setVisibility(View.VISIBLE);
        if (badge[pos] == 0) {
            holder.lin_std.setVisibility(View.GONE);
        } else if (badge[pos] < 100) {
            holder.txtBadge.setText(badge[pos] + "");
        } else {
            holder.txtBadge.setText("N");
        }

        return convertview;


    }


}