package com.adapter.parent;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudstream.cslink.parent.ApplicationData;
import com.cloudstream.cslink.parent.ChatActivity;
import com.cloudstream.cslink.R;
import com.common.utils.AppUtils;
import com.common.view.CircularImageView;
import com.xmpp.parent.Constant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyAdapter extends BaseAdapter {

    private final SharedPreferences mypref;
    private String phone;
    private Activity activity;

    private LayoutInflater inflater = null;

    public List<Childbeans> _items;
    private String rec_image, send_image;
    static int SECOND_MILLIS = 1000;
    static int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    static int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    static int DAY_MILLIS = 24 * HOUR_MILLIS;
    private final int height, width;
    private String itemcopy = "";


    public MyAdapter(ChatActivity chatActivity, List<Childbeans> InvitationList, String receiver_image,
                     String sender_image, String phone, int height, int width) {
        // TODO Auto-generated constructor stub
        this.activity = chatActivity;
        this._items = InvitationList;
        this.rec_image = receiver_image;
        this.send_image = sender_image;
        this.phone = phone;
        this.height = height;
        this.width = width;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mypref = activity.getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
        // notifyDataSetChanged();
    }

    public void updateListAdapter(List<Childbeans> messageList, String receiver_image, String sender_image, String phone) {
       // this._items.clear();
        this._items=messageList;
        this.rec_image = receiver_image;
        this.send_image = sender_image;
        this.phone = phone;
        this.notifyDataSetChanged();
    }

    public int getCount() {
        return _items.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public String getcopyText() {
        return itemcopy;
    }

    // .............View holder use to hold the view comes in the form of
    // array list............//
    // ........................ Here we link the all the objects with the
    // xml class ...............//
    class ViewHolder {
        public TextView send_msg, receive_msg, date_send, date_receive, txtToSeen, txtFromSeen, receive_time, send_time;
        public CircularImageView send_avatar, receive_avatar;
        public RelativeLayout layout_receive_msg, layout_send_msg;
        public ImageView imgToMobile;
        public ImageView img_mobile;
        public TextView txt_number, txt_relation;
        public LinearLayout lin_mobile;
        public LinearLayout lin_sender, lin_receive;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder _holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_chat_list, null);
            _holder = new ViewHolder();
            _holder.send_msg = (TextView) convertView.findViewById(R.id.showingsend_msg);
            _holder.date_send = (TextView) convertView.findViewById(R.id.showingsend_msg_date);
            _holder.send_avatar = (CircularImageView) convertView.findViewById(R.id.to_img_profile);
            _holder.txtToSeen = (TextView) convertView.findViewById(R.id.to_seen);
            _holder.send_time = (TextView) convertView.findViewById(R.id.showingsend_msg_time);
            _holder.receive_msg = (TextView) convertView.findViewById(R.id.showingreceive_msg);
            _holder.date_receive = (TextView) convertView.findViewById(R.id.showingrecive_msg_date);
            _holder.receive_avatar = (CircularImageView) convertView.findViewById(R.id.from_img_profile);
            _holder.txtFromSeen = (TextView) convertView.findViewById(R.id.from_seen);
            _holder.receive_time = (TextView) convertView.findViewById(R.id.showingrecive_msg_time);
            _holder.img_mobile = (ImageView) convertView.findViewById(R.id.img_mobile);
            _holder.txt_number = (TextView) convertView.findViewById(R.id.txt_number);
            _holder.txt_relation = (TextView) convertView.findViewById(R.id.txt_relation);
            _holder.lin_mobile = (LinearLayout) convertView.findViewById(R.id.lin_mobile);
            _holder.layout_receive_msg = (RelativeLayout) convertView.findViewById(R.id.show_recivemsg);
            _holder.layout_send_msg = (RelativeLayout) convertView.findViewById(R.id.show_sendmsg);
            _holder.lin_sender = (LinearLayout) convertView.findViewById(R.id.lin);
            _holder.lin_receive = (LinearLayout) convertView.findViewById(R.id.lin_receive);
            convertView.setTag(_holder);

        } else {
            _holder = (ViewHolder) convertView.getTag();
        }

        if (_items.get(position).sender.equals("me")) {
            _holder.layout_receive_msg.setVisibility(View.GONE);
            _holder.layout_send_msg.setVisibility(View.VISIBLE);
            _holder.lin_mobile.setVisibility(View.GONE);

            String sm = _items.get(position).message_body;
            _holder.send_msg.setText(sm);

            if (!_items.get(position).created_at.equalsIgnoreCase("") && _items.get(position).created_at.length() > 0) {
                //    long days = ApplicationData.convertlocalize(_items.get(position).created_at);
                _holder.date_send.setText(ApplicationData.convertlocalize(_items.get(position).created_at));//gettimeval(days, _items.get(position).created_at));
            }

            ApplicationData.setProfileImg(this.activity, ApplicationData.web_server_url + ApplicationData.Imagepath + this.send_image, _holder.send_avatar);
            //_holder.txtToMobile.setText(_items.get(position).child_moblie.equals("null") ? "" : _items.get(position).child_moblie);
            //  if(_items.get(position).child_moblie.equals("null") && _items.get(position).child_moblie.length()==0 )

            if (_items.get(position).parentno != null && _items.get(position).parentno.length() > 0 && _items.get(position).parentno != null) {
                _holder.lin_mobile.setVisibility(View.VISIBLE);
                String name=getnamefromno(_items.get(position).parentno);
                if (_items.get(position).parentno.equalsIgnoreCase(mypref.getString("parent_no", "")))
                {
                    _holder.txt_number.setText(mypref.getString("phone", "") + ", " + name);
                    phone=mypref.getString("phone", "");
                }
                else if (_items.get(position).parentno.equalsIgnoreCase(mypref.getString("parent_no2", ""))) {
                    _holder.txt_number.setText(mypref.getString("parentmobile2", "") + ", " + name);
                    phone=mypref.getString("parentmobile2", "");
                }
                else if (_items.get(position).parentno.equalsIgnoreCase(mypref.getString("parent_no3", ""))) {
                    _holder.txt_number.setText(mypref.getString("parentmobile3", "") + ", " + name);
                    phone=mypref.getString("parentmobile3", "");
                }
            } else {
                _holder.lin_mobile.setVisibility(View.GONE);
            }

            if (_items.get(position).iscarboncopy) {
                _holder.txtToSeen.setVisibility(View.GONE);
            } else {
                if (_items.get(position).message_status != null && _items.get(position).message_status.length() > 0) {
                    _holder.txtToSeen.setVisibility(View.VISIBLE);
                    if (_items.get(position).message_status.equalsIgnoreCase("Received"))
                        _holder.txtToSeen.setText(activity.getString(R.string.str_seen));  //Tread
                    else
                        _holder.txtToSeen.setText(activity.getString(R.string.str_not_seen));  //Tread
                }
            }


        } else if (_items.get(position).sender.equals("from")) {

            _holder.layout_receive_msg.setVisibility(View.VISIBLE);
            _holder.layout_send_msg.setVisibility(View.GONE);
            _holder.lin_mobile.setVisibility(View.GONE);
            String sm = _items.get(position).message_body;

            if (!_items.get(position).created_at.equalsIgnoreCase("") && _items.get(position).created_at.length() > 0) {
                //  long days = ApplicationData.getDateDifference(_items.get(position).created_at);
                _holder.date_receive.setText(ApplicationData.convertlocalize(_items.get(position).created_at));//gettimeval(days, _items.get(position).created_at));
            }
            _holder.receive_msg.setText(sm);
            _holder.txtFromSeen.setText("");

            ApplicationData.setProfileImg(this.activity, ApplicationData.web_server_url + "uploads/" + this.rec_image, _holder.receive_avatar);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupWindow popupWindow = new PopupWindow(activity);
                LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View convertView;
                convertView = inflater.inflate(R.layout.item_copystring, null);
                convertView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                // some other visual settings for popup window
                popupWindow.setFocusable(true);
                TextView txt_spin = (TextView) convertView.findViewById(R.id.txt_spin);
                popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
                popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                // popupWindow.setOutsideTouchable(true);
                // set the list view as pop up window content
                popupWindow.setOutsideTouchable(true);
                popupWindow.setContentView(convertView);

                int[] loc_int = new int[2];

                Rect location = new Rect();
                location.left = loc_int[0];
                location.top = loc_int[1];
                location.right = location.left + v.getWidth();
                location.bottom = location.top + v.getHeight();

                    if (_items.get(position).sender.equals("me")) {
                        // popupWindow.showAsDropDown(v, location.right / 2, -(_holder.lin_sender.getMeasuredHeight()) - 100);

                        popupWindow.showAsDropDown(v, (v.getWidth() / 2) - 40, -(v.getHeight())-70);
                    } else {
                        popupWindow.showAsDropDown(v, (v.getWidth() / 2) - 40, -(v.getHeight())-70);
                    }


//                if (_items.get(position).sender.equals("me")) {
//                    popupWindow.showAsDropDown(v, location.right / 3, -(_holder.lin_sender.getMeasuredHeight()) - 120);
//
//                } else
//                    popupWindow.showAsDropDown(v, location.right / 3, -(_holder.lin_receive.getMeasuredHeight()) - 120);


                txt_spin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (_items.get(position).message_body.length() != 0) {

                            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                            android.content.ClipData clip = android.content.ClipData.newPlainText("Clip", _items.get(position).message_body);
                            clipboard.setPrimaryClip(clip);
                        }

                        itemcopy = _items.get(position).message_body;
                        popupWindow.dismiss();
                        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                            }
                        });
                    }
                });
            }
        });

        _holder.lin_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_holder.lin_mobile.getVisibility() == View.VISIBLE) {

                    ApplicationData.calldialog(activity, activity.getResources().getString(R.string.number), phone,
                            activity.getResources().getString(R.string.call), activity.getResources().getString(R.string.cancel), new ApplicationData.DialogListener() {
                                @Override
                                public void diaBtnClick(int diaID, int btnIndex) {
                                    if (btnIndex == 2) {
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        intent.setData(Uri.parse("tel:" +activity.getResources().getString(R.string.number) +phone));
                                        activity.startActivity(intent);
                                    }
                                }
                            }, 1);
                }
            }
        });


        return convertView;
    }

    private String getnamefromno(String parentno) {
        if(parentno.equalsIgnoreCase("1"))
            return activity.getString(R.string.parent1);
        else if(parentno.equalsIgnoreCase("2"))
            return activity.getString(R.string.parent2);
        else if(parentno.equalsIgnoreCase("3"))
            return activity.getString(R.string.profile_hint_contact);
        else
            return "";
    }

    private long gettime(String created_at) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy,HH:mm");
        // SimpleDateFormat dateFormat2 = new SimpleDateFormat("hh:mm");
        long millis = 0;
        try {
            Date date = dateFormat.parse(created_at);
            millis = date.getTime();
            //millis = Long.valueOf(created_at);
        } catch (Exception e) {

        }
        return millis;
        // TODO Auto-generated method stub
    }



}