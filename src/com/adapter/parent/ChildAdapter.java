package com.adapter.parent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cloudstream.cslink.parent.ApplicationData;
import com.cloudstream.cslink.parent.MainActivity;
import com.cloudstream.cslink.R;
import com.common.Bean.BadgeBean;
import com.common.dialog.MainProgress;
import com.common.view.CircularImageView;
import com.xmpp.parent.Constant;
import com.xmpp.parent.XMPPMethod;

import java.util.ArrayList;

public class ChildAdapter extends PagerAdapter {

    private Activity context;
    private ArrayList<Childbeans> list;
    private int pos;
    private String selectedchild_id;
    private ArrayList<BadgeBean> bglist = new ArrayList<BadgeBean>();
    private int selected;


    public ChildAdapter(Activity context, ArrayList<Childbeans> list, int pos, ArrayList<BadgeBean> bglist) {
        this.context = context;
        this.list = list;
        this.pos = pos;
        SharedPreferences preference = context.getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
        selectedchild_id = preference.getString("childid", "");
        this.bglist = bglist;
    }

    public void updatelist(Activity context, ArrayList<BadgeBean> bglist, ArrayList<Childbeans> list,int pos) {
        this.context = context;
        this.bglist = bglist;
        this.list = list;
        this.pos = pos;
        SharedPreferences preference = context.getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
        selectedchild_id = preference.getString("childid", "");
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }


    @Override
    public int getItemPosition(Object object) {
        // TODO Auto-generated method stub
        return super.getItemPosition(object);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder = null;
        View view = null;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.item_viewpager_child, container, false);
            holder.imageView1 = (CircularImageView) view.findViewById(R.id.imageView1);
            holder.txt_childnm = (TextView) view.findViewById(R.id.txt_childnm);
            holder.line = (View) view.findViewById(R.id.line);
            holder.bg_circle = (CircularImageView) view.findViewById(R.id.bg_circle);
            holder.lin_badge = (LinearLayout) view.findViewById(R.id.lin_badge);
            holder.txtBadge = (TextView) view.findViewById(R.id.txtBadge);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Childbeans data = list.get(position);

        if (data.child_name != null && data.child_name.length() > 0)
            holder.txt_childnm.setText(data.child_name);

        if (data.child_image != null && data.child_image.length() > 0) {
            String url = ApplicationData.web_server_url + ApplicationData.child_image_path + data.child_image;
            ApplicationData.setProfileImg(context, url, holder.imageView1);
        }
        if (selectedchild_id.equalsIgnoreCase(data.user_id)) {
            holder.bg_circle.setImageResource(R.drawable.circle_yello_full);
            holder.txt_childnm.setTextColor(context.getResources().getColor(R.color.yellow_home));
        } else {
            holder.bg_circle.setImageResource(R.drawable.circle_white_empty);
            holder.txt_childnm.setTextColor(context.getResources().getColor(R.color.white_light));
        }
        if (position == list.size() - 1) {
            holder.line.setVisibility(View.GONE);
        }

        String badgeval = getval(data.user_id, data);

        if (badgeval != null && !badgeval.equalsIgnoreCase("0")) {
            holder.lin_badge.setVisibility(View.VISIBLE);
            holder.txtBadge.setText(badgeval);
        } else
            holder.lin_badge.setVisibility(View.GONE);

        // Add viewpager_item.xml to ViewPager
        ((ViewPager) container).addView(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (list.get(position).name != null && list.get(position).name.length() == 0) {
                    try {
                        ApplicationData.showMessage(context, context.getResources().getString(R.string.str_alert),
                                context.getResources().getString(R.string.msg_not_active_child),
                                context.getResources().getString(R.string.str_ok));
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (!selectedchild_id.equalsIgnoreCase(list.get(position).user_id)) {

                    SharedPreferences myPrefs = context.getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = myPrefs.edit();
                    editor.putString("childname", list.get(position).child_name);
                    editor.putString("childid", list.get(position).user_id);
                    editor.putString("childArray", myPrefs.getString("child_array", ""));
                    editor.putString("school_id", list.get(position).school_id);
                    editor.putString("school_class_id", list.get(position).school_class_id);
                    editor.putString("image", list.get(position).child_image);
                    editor.putString("schoolname",list.get(position).school_name);
                    editor.commit();

                   /* selectedchild_id=list.get(position).user_id;
                    try {
                        Intent data = new Intent();
                        String action = Constant.SELECT_CHILD;
                        data.putExtra("selposition",position);
                        data.setAction(action);
                        context.sendBroadcast(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/

                    new chatclass(list.get(position).jid, list.get(position).jid_pwd).execute();

                }
            }
        });

        return view;
    }

    private String getval(String user_id, Childbeans data) {
        int badge_value = 0;
        for (int i = 0; i < bglist.size(); i++) {
            if (user_id.equalsIgnoreCase(bglist.get(i).kidid)) {
                badge_value = badge_value + Integer.parseInt(bglist.get(i).badge);
            }
        }
        if (badge_value > 99) {
            return "N";
        } else
            return String.valueOf(badge_value);
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {

        return view.equals(object);
    }


    @Override
    public float getPageWidth(int position) {
        return (0.6f);
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }


    class ViewHolder {

        public CircularImageView imageView1;
        public TextView txt_childnm, txtBadge;
        public View line;
        public CircularImageView bg_circle;
        public LinearLayout lin_badge;
    }

    private class chatclass extends AsyncTask<Void, Void, Void> {
        private final String jid, jid_pwd;
        int position;
        SharedPreferences myPrefs = context.getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        private MainProgress pDialog;

        public chatclass(String jid, String jid_pwd) {
            this.jid = jid;
            this.jid_pwd = jid_pwd;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           /* if (pDialog == null)
                pDialog = new MainProgress(context);
            pDialog.setCancelable(false);
            pDialog.setMessage(context.getResources().getString(R.string.str_wait));
            pDialog.show();*/
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (myPrefs.getString("jid", "") != null && myPrefs.getString("jid", "").length() > 0) {
                try {
                    XMPPMethod.disconnection(context, myPrefs.getString("jid", ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
           /* if(pDialog!=null && pDialog.isShowing())
            {
                pDialog.dismiss();
            }*/
            editor.putString("jid", jid);
            editor.putString("jid_pwd", jid_pwd);
            editor.commit();

            try {
                XMPPMethod.connect(context, jid, "5222", jid_pwd);//, myPrefs.getString("jid", ""), "5222", myPrefs.getString("jid_pwd", ""));
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent in = new Intent(context, MainActivity.class);
            in.putExtra("fragment", 0);
            context.startActivity(in);
            context.finish();

        }
    }
}