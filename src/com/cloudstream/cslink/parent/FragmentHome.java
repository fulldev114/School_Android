package com.cloudstream.cslink.parent;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adapter.parent.ChildAdapter;
import com.adapter.parent.Childbeans;
import com.cloudstream.cslink.R;
import com.common.Bean.BadgeBean;
import com.common.dialog.MainProgress;
import com.common.utils.PagerContainer;
import com.db.parent.DatabaseHelper;
import com.xmpp.parent.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class FragmentHome extends Fragment implements View.OnClickListener {

    private MainProgress pDialog;
    private LinearLayout lin_message, lin_absent, lin_gpmsg;
    private ViewPager viewpager;
    private int selPos = 0;
    private ImageView img_backar, img_nextar;
    private PagerContainer mContainer;
    private String child_array, parent_id, parent_name, childid;
    ArrayList<Childbeans> arrayList = new ArrayList<Childbeans>();
    ArrayList<BadgeBean> bglist = new ArrayList<BadgeBean>();
    String[] videoNames;
    private ChildAdapter adapter;
    private LinearLayout inc_msg_bg, inc_gp_bdg, inc_abn_bg;
    private TextView txtBadge, txtBadge_gp, txtBadge_abn;
    //static String abibadge = "0";
    String abibadge = "0", abnbadge = "0", chat_badge = "0";
    private SharedPreferences myPrefs;
    private Activity mActivity;
    private int offabn = 0, offabi = 0, offchb = 0;
    private UpdateChildBroadCast updatechildbroadcast;

    public FragmentHome() {

    }

    String url, _parent_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.homescreen, container, false);
        mActivity = getActivity();

        lin_message = (LinearLayout) rootView.findViewById(R.id.lin_msg);
        lin_gpmsg = (LinearLayout) rootView.findViewById(R.id.lin_gp);
        lin_absent = (LinearLayout) rootView.findViewById(R.id.lin_abs);
        viewpager = (ViewPager) rootView.findViewById(R.id.viewPager_home);
        img_backar = (ImageView) rootView.findViewById(R.id.img_backar);
        img_nextar = (ImageView) rootView.findViewById(R.id.img_nextar);
        inc_msg_bg = (LinearLayout) rootView.findViewById(R.id.inc_msg_bg);
        inc_gp_bdg = (LinearLayout) rootView.findViewById(R.id.inc_gp_bdg);
        inc_abn_bg = (LinearLayout) rootView.findViewById(R.id.inc_abn_bg);
        txtBadge = (TextView) rootView.findViewById(R.id.txtBadge);
        txtBadge_gp = (TextView) rootView.findViewById(R.id.txtBadge_gp);
        txtBadge_abn = (TextView) rootView.findViewById(R.id.txtBadge_abn);
        //   photogallery=(Gallery)rootView.findViewById(R.id.photogallery);

        img_nextar.setOnClickListener(this);
        img_backar.setOnClickListener(this);
        lin_message.setOnClickListener(this);
        lin_gpmsg.setOnClickListener(this);
        lin_absent.setOnClickListener(this);

        ApplicationData.ignorbadge = true;
        myPrefs = getActivity().getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
        child_array = myPrefs.getString("child_array", "");
        parent_id = myPrefs.getString("parent_id", "");
        parent_name = myPrefs.getString("parent_name", "");
        childid = myPrefs.getString("childid", "");


        //to check push notification is received or not in app..
//        boolean isDebuggable = 0 != (getActivity().getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE);
//        boolean isBeingDebugged = android.os.Debug.isDebuggerConnected();
//
//        if (isDebuggable || isBeingDebugged) {
//            Button btnLog = (Button) rootView.findViewById(R.id.btnLog);
//            btnLog.setVisibility(View.VISIBLE);
//            btnLog.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent i = new Intent(getActivity(), ActivityLog.class);
//                    startActivity(i);
//                }
//            });
//        }
        // viewpager.setPageMargin( getResources().getDimensionPixelOffset(R.dimen.forty_phone_size));

        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                selPos = arg0;
                if (arrayList != null) {
                    if (selPos > 0 && selPos < arrayList.size() - 2) {
                        img_nextar.setImageResource(R.drawable.next_blue);
                        img_nextar.setRotation(0);
                        img_backar.setImageResource(R.drawable.next_blue);
                        img_backar.setRotation(180);
                        viewpager.setCurrentItem(selPos);
                    } else if (selPos == 0) {
                        img_backar.setImageResource(R.drawable.back_white_left_arrow);
                        img_backar.setRotation(0);
                        img_nextar.setImageResource(R.drawable.next_blue);
                        img_nextar.setRotation(0);
                        viewpager.setCurrentItem(selPos);
                    } else if (selPos == arrayList.size() - 1) {
                        img_nextar.setImageResource(R.drawable.back_white_left_arrow);
                        img_nextar.setRotation(180);
                        img_backar.setImageResource(R.drawable.next_blue);
                        img_backar.setRotation(180);
                        viewpager.setCurrentItem(selPos - 1);
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //viewpager.setCurrentItem(selPos);
            }
        });


        ((MainActivity) getActivity()).lin_emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), EmergencyAlertActivity.class);
                startActivity(i);
            }
        });

        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                ApplicationData.setMainActivity(mActivity);
            }
        }, 2000);

        // ApplicationData.showAppBadgeDec(getActivity(), 0);
        //get children list from json array
        loadChildrenList();

        /*IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.SELECT_CHILD);
        updatechildbroadcast = new UpdateChildBroadCast();
        getActivity().registerReceiver(updatechildbroadcast, filter);*/
        return rootView;
    }

    public void setbadge() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (bglist != null) {
                        bglist.clear();
                    }
                    DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(getActivity());
                    String query = "select * from badge_table";
                    ArrayList<HashMap<String, String>> messagelist = db.selectRecordsFromDBList(query, null);
                    if (messagelist != null) {

                        for (HashMap<String, String> badgemap : messagelist) {
                            BadgeBean bean = new BadgeBean();
                            if (badgemap.get("KidId").equalsIgnoreCase(childid)) {
                                abibadge = badgemap.get("Abi");
                                abnbadge = badgemap.get("Abn");
                                chat_badge = badgemap.get("Chb");
                            }

                            int abibg = 0, abnbg = 0, chbg = 0;
                            if (badgemap.get("Abi") == null || badgemap.get("Abi").equalsIgnoreCase("") || badgemap.get("Abi").length() == 0)
                                abibg = 0;
                            else
                                abibg = Integer.parseInt(badgemap.get("Abi"));

                            if (badgemap.get("Abn") == null || badgemap.get("Abn").equalsIgnoreCase("") || badgemap.get("Abn").length() == 0)
                                abnbg = 0;
                            else
                                abnbg = Integer.parseInt(badgemap.get("Abn"));

                            if (badgemap.get("Chb") == null || badgemap.get("Chb").equalsIgnoreCase("") || badgemap.get("Chb").length() == 0)
                                chbg = 0;
                            else
                                chbg = Integer.parseInt(badgemap.get("Chb"));

                            bean.badge = String.valueOf(abibg + abnbg + chbg);//)-Integer.parseInt(badgemap.get("Chb")));
                            bean.kidid = badgemap.get("KidId");

                            Log.d("FragmentHome", bean.badge + "......" + bean.kidid);

                            bean.type = badgemap.get("MessgeType");

                            bglist.add(bean);
                        }

                        if (!abibadge.equalsIgnoreCase("0")) {
                            inc_gp_bdg.setVisibility(View.VISIBLE);
                            if (Integer.parseInt(abibadge) > 99)
                                txtBadge_gp.setText("N");
                            else
                                txtBadge_gp.setText(abibadge);
                        } else {
                            inc_gp_bdg.setVisibility(View.GONE);
                        }

                    }

                    setmessagebadge();
                }
            });
        }
    }

    private void loadChildrenList() {
        if (arrayList != null)
            arrayList.clear();
        arrayList = new ArrayList<Childbeans>();
        int noti_position = 0, totbadge = 0, pos = 0;
        if (child_array.equalsIgnoreCase("")) {

        } else {
            try {
                DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(getActivity());
                JSONArray jsonObj = new JSONArray(child_array);
                int adtuallength = jsonObj.length();
                videoNames = new String[adtuallength];
                for (int i = 0; i < adtuallength; i++) {

                    Childbeans childbeans = new Childbeans();
                    JSONObject c = jsonObj.getJSONObject(i);

                    if (c.getString("parentname").length() == 0)
                        continue;
                    else {
                        childbeans.child_image = c.getString("child_image");
                        childbeans.child_name = c.getString("child_name");
                        videoNames[i] = new String(c.getString("child_name"));
                        childbeans.school_name = c.getString("school_name");
                        childbeans.child_moblie = c.getString("child_moblie");
                        childbeans.school_class_id = c.getString("school_class_id");
                        childbeans.user_id = c.getString("user_id");
                   /* if (String.valueOf(noti_kidid).equals(childbeans.user_id))
                        noti_position = i;*/
                        childbeans.child_gender = c.getString("child_gender");
                        childbeans.child_age = c.getString("child_age");
                        childbeans.school_id = c.getString("school_id");
                        childbeans.name = parent_name;
                        if (c.has("jid"))
                            childbeans.jid = c.getString("jid");
                        if (c.has("key"))
                            childbeans.jid_pwd = c.getString("key");

                        if (c.has("abi_badge") && c.has("abn_badge"))
                            childbeans.badge = c.getInt("abi_badge") + c.getInt("abn_badge");

                        if (c.has("abi_badge")) {
                            if (c.getInt("abi_badge") != 0) {
                                totbadge = c.getInt("abi_badge");
                                txtBadge_gp.setVisibility(View.VISIBLE);
                                if (c.getInt("abi_badge") > 99)
                                    txtBadge_gp.setText("N");
                                else
                                    txtBadge_gp.setText(String.valueOf(c.getInt("abi_badge")));

                            }
                        }
                        if (c.has("abn_badge")) {
                            if (c.getInt("abn_badge") != 0) {
                                totbadge = totbadge + c.getInt("abn_badge");
                                txtBadge_abn.setVisibility(View.VISIBLE);
                                if (c.getInt("abn_badge") > 99)
                                    txtBadge_abn.setText("N");
                                else
                                    txtBadge_abn.setText(String.valueOf(c.getInt("abn_badge")));

                            }
                        }

                        if (c.has("abi_badge") && c.has("abn_badge")) {
                            db.insertbadgeifnot("", "", "abi", childbeans.user_id, "", childbeans.child_name, c.getInt("abi_badge"), c.getInt("abn_badge"), totbadge + "");
                            childbeans.abi_badge = 0;
                            childbeans.abn_badge = 0;
                            c.put("abi_badge", 0);
                            c.put("abn_badge", 0);

                            jsonObj.put(i, c);
                        }
                        SharedPreferences.Editor edit = myPrefs.edit();
                        edit.putString("child_array", jsonObj.toString());
                        edit.commit();
                        arrayList.add(childbeans);

                        if (childid.equalsIgnoreCase(childbeans.user_id)) {
                            selPos = pos;
                        }
                        pos++;
                    }
                }
                // looping through All Contacts
            } catch (JSONException e) {
                System.out.println("Andy Error " + e);
                e.printStackTrace();
            }
        }

		/*cAdapter = new ChildrenListAdapter(this, arrayList);
        list.setAdapter(cAdapter);*/
        //emergency alert click
        adapter = new ChildAdapter(getActivity(), arrayList, selPos, bglist);
        viewpager.setAdapter(adapter);

        Handler handl = new Handler();
        handl.postDelayed(new Runnable() {
            @Override
            public void run() {
                ApplicationData.ignorbadge = false;
            }
        }, 4000);

        setbadge();

    }


    public void onClick(View v) {
        // ((MainActivity)getActivity()).lin_emergency.setVisibility(View.GONE);
        //ApplicationData.setMainActivity(getActivity());
        if (v.getId() == R.id.img_backar) {
            if (arrayList != null) {
                if (selPos != 0) {
                    selPos--;
                    img_backar.setImageResource(R.drawable.next_blue);
                    img_backar.setRotation(180);
                    img_nextar.setImageResource(R.drawable.next_blue);
                    img_nextar.setRotation(0);
                } else {
                    img_backar.setImageResource(R.drawable.back_white_left_arrow);
                    img_backar.setRotation(0);
                    img_nextar.setImageResource(R.drawable.next_blue);
                    img_nextar.setRotation(0);
                }

            }
        } else if (v.getId() == R.id.img_nextar) {
            if (arrayList != null) {
                if (selPos < arrayList.size() - 2) {
                    selPos++;
                    img_nextar.setImageResource(R.drawable.next_blue);
                    img_nextar.setRotation(0);
                    img_backar.setImageResource(R.drawable.next_blue);
                    img_backar.setRotation(180);
                } else {
                    img_nextar.setImageResource(R.drawable.back_white_left_arrow);
                    img_nextar.setRotation(180);
                    img_backar.setImageResource(R.drawable.next_blue);
                    img_backar.setRotation(180);
                }

            }
        } else if (v.getId() == R.id.lin_msg) {

            ApplicationData.ishomebuttonview = true;
            Intent i = new Intent(getActivity(), FragmentChat.class);
            startActivity(i);
            DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(getActivity());
            db.clearallchatbadge(childid);
            db.clearbadge("chb", childid);
            chat_badge = "0";
            SharedPreferences.Editor edit = myPrefs.edit();
            edit.putInt("chb", 0);
            edit.commit();

        } else if (v.getId() == R.id.lin_gp) {
            ApplicationData.ishomebuttonview = true;
            DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(getActivity());
            db.clearbadge("abi", childid);
            inc_gp_bdg.setVisibility(View.GONE);
            Intent i = new Intent(getActivity(), GroupMessageActivity.class);
            startActivity(i);

        } else if (v.getId() == R.id.lin_abs) {
            ApplicationData.ishomebuttonview = true;
            Intent i = new Intent(getActivity(), FragmentAbsent.class);
            startActivity(i);
        }
        setbadge();
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public void setbadgemessage(String alert, String message, String type, int kidid, int from_id, Integer badge,
                                int abi, int abn, int chat_msg) {
        setbadge();
    }

    public void setmessagebadge() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    inc_msg_bg.setVisibility(View.GONE);
                    int chatbadge = 0, allbadge = 0;
                    DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(getActivity());
                    String query = "select * from chat_msg_badge";//where User_id= " + '"' + childid + '"';
                    ArrayList<HashMap<String, String>> messagelist = db.selectRecordsFromDBList(query, null);
                    if (messagelist != null) {
                        for (HashMap<String, String> badgemap : messagelist) {
                            if (badgemap.get("User_id").equalsIgnoreCase(childid)) {
                                chatbadge = chatbadge + Integer.parseInt(badgemap.get("Badge"));
                                allbadge = Integer.parseInt(badgemap.get("AllBadge"));
                            }
                        }
                        if (allbadge != 0 || Integer.parseInt(chat_badge) != 0) {
                            inc_msg_bg.setVisibility(View.VISIBLE);
                            if (allbadge > 99 || Integer.parseInt(chat_badge) > 99)
                                txtBadge.setText("N");
                            else
                                txtBadge.setText(String.valueOf(allbadge + Integer.parseInt(chat_badge)));

                        } else {
                            inc_msg_bg.setVisibility(View.GONE);
                        }
                        for (HashMap<String, String> badgemap : messagelist) {
                            boolean isnotchat = false;
                            for (int i = 0; i < bglist.size(); i++) {

                                if (bglist.get(i).kidid.equalsIgnoreCase(badgemap.get("User_id"))) {
                                    isnotchat = true;
                                    BadgeBean bean = new BadgeBean();
                                    bean.kidid = badgemap.get("User_id");
                                    bean.type = "";
                                    bean.badge = String.valueOf(Integer.parseInt(chat_badge) + Integer.parseInt(abibadge) + Integer.parseInt(abnbadge) + Integer.parseInt(badgemap.get("AllBadge")));
                                    //bglist.get(i).badge)
                                    bglist.remove(i);
                                    bglist.add(i, bean);
                                    break;
                                }
                            }
                            if (!isnotchat) {

                                BadgeBean bean = new BadgeBean();
                                bean.kidid = badgemap.get("User_id");
                                bean.type = "chat_msg";
                                bean.badge = badgemap.get("AllBadge");
                                bglist.add(bean);
                            }

                        }

                    }
                    adapter = new ChildAdapter(getActivity(), arrayList, selPos, bglist);
                    viewpager.setAdapter(adapter);
                    // adapter.updatelist(getActivity(), bglist, arrayList, selPos);
                    if (selPos == arrayList.size() - 1) {
                        viewpager.setCurrentItem(selPos - 1);
                    } else
                        viewpager.setCurrentItem(selPos);
                }

            });
        }
    }

    private class UpdateChildBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (intent.hasExtra("selposition")) {
                selPos = intent.getIntExtra("selposition", 0);
            }
            if (action != null && action.equalsIgnoreCase(Constant.SELECT_CHILD)) {
                child_array = myPrefs.getString("child_array", "");
                parent_id = myPrefs.getString("parent_id", "");
                parent_name = myPrefs.getString("parent_name", "");
                childid = myPrefs.getString("childid", "");

                setbadge();
            }
        }
    }

  /*  @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(updatechildbroadcast);
    }*/
}