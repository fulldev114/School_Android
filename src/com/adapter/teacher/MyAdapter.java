package com.adapter.teacher;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudstream.cslink.teacher.ApplicationData;
import com.cloudstream.cslink.teacher.ChatActivity;
import com.cloudstream.cslink.R;
import com.common.view.CircularImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyAdapter extends BaseAdapter {

    private final int height, width;
    private final Childbeans datalist;
    private Activity activity;

    private LayoutInflater inflater = null;

    public List<Childbeans> _items;
    private String rec_image, send_image, phone;
    static int SECOND_MILLIS = 1000;
    static int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    static int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    static int DAY_MILLIS = 24 * HOUR_MILLIS;
    private String itemcopy = "";
    private boolean isFromTeacherChat = false;


    public MyAdapter(Activity chatActivity, List<Childbeans> InvitationList, String receiver_image,
                     String sender_image, int height, int width, Childbeans datalist, boolean isFromTeacherChat) {
        // TODO Auto-generated constructor stub
        this.activity = chatActivity;
        this._items = InvitationList;
        this.rec_image = receiver_image;
        this.height = height;
        this.width = width;
        this.send_image = sender_image;
        this.datalist = datalist;
        this.isFromTeacherChat = isFromTeacherChat;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // notifyDataSetChanged();
    }

    public void updateReceiptsList(List<Childbeans> messageList, boolean isFromTeacherChat) {
        this._items = messageList;
        this.isFromTeacherChat = isFromTeacherChat;
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
        public TextView send_msg, receive_msg, date_send, date_receive, to_seen;//, from_phone;
        public CircularImageView send_avatar, receive_avatar;
        public RelativeLayout layout_receive_msg, layout_send_msg;
        public ImageView imgToMobile;
        public TextView send_time;
        public TextView txtFromSeen;
        public TextView receive_time;
        public ImageView img_mobile;
        public TextView txt_number, txt_relation;
        public LinearLayout lin_mobile;
        public LinearLayout lin_sender, lin_receive;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder _holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adres_custom_list, parent, false);
            _holder = new ViewHolder();
            _holder.send_msg = (TextView) convertView.findViewById(R.id.showingsend_msg);
            _holder.date_send = (TextView) convertView.findViewById(R.id.showingsend_msg_date);
            _holder.send_avatar = (CircularImageView) convertView.findViewById(R.id.to_img_profile);

            _holder.receive_msg = (TextView) convertView.findViewById(R.id.showingreceive_msg);
            _holder.date_receive = (TextView) convertView.findViewById(R.id.showingrecive_msg_date);
            _holder.receive_avatar = (CircularImageView) convertView.findViewById(R.id.from_img_profile);

            _holder.layout_receive_msg = (RelativeLayout) convertView.findViewById(R.id.show_recivemsg);
            _holder.layout_send_msg = (RelativeLayout) convertView.findViewById(R.id.show_sendmsg);

            _holder.to_seen = (TextView) convertView.findViewById(R.id.to_seen);
            //	_holder.from_phone = (TextView) convertView.findViewById(R.id.from_phone);
            _holder.img_mobile = (ImageView) convertView.findViewById(R.id.img_mobile);
            _holder.txt_number = (TextView) convertView.findViewById(R.id.txt_number);
            _holder.txt_relation = (TextView) convertView.findViewById(R.id.txt_relation);
            _holder.lin_mobile = (LinearLayout) convertView.findViewById(R.id.lin_mobile);
            _holder.send_time = (TextView) convertView.findViewById(R.id.showingsend_msg_time);
            _holder.lin_sender = (LinearLayout) convertView.findViewById(R.id.lin);
            _holder.txtFromSeen = (TextView) convertView.findViewById(R.id.from_seen);
            _holder.receive_time = (TextView) convertView.findViewById(R.id.showingrecive_msg_time);
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

            if (!_items.get(position).created_at.equalsIgnoreCase("") && _items.get(position).created_at.length() > 0) {
                // long days = ApplicationData.getDateDifference(_items.get(position).created_at);
                _holder.date_send.setText(ApplicationData.convertlocalize(_items.get(position).created_at));//gettimeval(days, _items.get(position).created_at));
            }
            _holder.send_msg.setText(sm);

            if (_items.get(position).message_status != null && _items.get(position).message_status.length() > 0) {
                if (_items.get(position).message_status.equalsIgnoreCase("Received"))
                    _holder.to_seen.setText(activity.getString(R.string.str_seen));  //Tread
                else
                    _holder.to_seen.setText(activity.getString(R.string.str_not_seen));  //Tread
            }

            ApplicationData.setProfileImg(_holder.send_avatar, ApplicationData.web_server_url + "uploads/" + this.send_image, this.activity);

        } else if (_items.get(position).sender.equals("from")) {

            _holder.layout_receive_msg.setVisibility(View.VISIBLE);
            _holder.layout_send_msg.setVisibility(View.GONE);
            _holder.receive_msg.setText(_items.get(position).message_body);


            if (!_items.get(position).created_at.equalsIgnoreCase("") && _items.get(position).created_at.length() > 0) {
                //long days = ApplicationData.getDateDifference(_items.get(position).created_at);
                _holder.date_receive.setText(ApplicationData.convertlocalize(_items.get(position).created_at));//gettimeval(days, _items.get(position).created_at));
            }

            if (_items.get(position).parenttype != null && _items.get(position).parenttype.length() > 0 &&
                    !_items.get(position).parenttype.equals("null")) {
                String type = "";
                if (datalist != null) {
                    Childbeans childb = _items.get(position);
                    if (_items.get(position).parenttype.equalsIgnoreCase("1")) {
                        if (datalist.mobile1 != null && datalist.mobile1.length() > 0 && !datalist.mobile1.equals("null")) {
                            type = datalist.mobile1 + ",";
                            _holder.img_mobile.setVisibility(View.VISIBLE);
                            childb.child_moblie = datalist.mobile1;
                            _items.set(position, childb);
                        } else
                            _holder.img_mobile.setVisibility(View.GONE);
                        type = type + activity.getResources().getString(R.string.parent1);
                        //phone = datalist.mobile1;
                    } else if (_items.get(position).parenttype.equalsIgnoreCase("2")) {
                        if (datalist.parent2mobile != null && datalist.parent2mobile.length() > 0 && !datalist.parent2mobile.equals("null")) {
                            type = datalist.parent2mobile + ",";
                            _holder.img_mobile.setVisibility(View.VISIBLE);
                            childb.child_moblie = datalist.parent2mobile;
                            _items.set(position, childb);
                        } else {
                            _holder.img_mobile.setVisibility(View.GONE);
                        }
                        type = type + activity.getResources().getString(R.string.parent2);
                        //  phone = datalist.parent2mobile;
                    } else if (_items.get(position).parenttype.equalsIgnoreCase("3")) {
                        if (datalist.parent3mobile != null && datalist.parent3mobile.length() > 0 && !datalist.parent3mobile.equals("null")) {
                            type = datalist.parent3mobile + ",";
                            _holder.img_mobile.setVisibility(View.VISIBLE);
                            childb.child_moblie = datalist.parent3mobile;
                            _items.set(position, childb);
                        } else
                            _holder.img_mobile.setVisibility(View.GONE);

                        type = type + activity.getResources().getString(R.string.parent3);
                        // phone = datalist.parent3mobile;
                    }
                    _holder.lin_mobile.setVisibility(View.VISIBLE);
                    _holder.txt_number.setText(type);  //Tread
                } else {
                    _holder.lin_mobile.setVisibility(View.GONE);
                }
            }
          /*  if (_items.get(position).child_moblie != null && _items.get(position).child_moblie.length() > 0 &&
                    !_items.get(position).child_moblie.equals("null")) {
                _holder.lin_mobile.setVisibility(View.VISIBLE);
                _holder.txt_number.setText(_items.get(position).child_moblie + "," + _items.get(position).parenttype);  //Tread
            } else {
                _holder.lin_mobile.setVisibility(View.GONE);
                //_holder.txt_number.setText(this.activity.getResources().getString(R.string.str_tmp_phone));  //Tread
            }*/
            _holder.txtFromSeen.setText("");
            ApplicationData.setProfileImg(_holder.receive_avatar, ApplicationData.web_server_url + "uploads/" + this.rec_image, this.activity);
        }

        _holder.lin_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = _items.get(position).child_moblie;
                if (_holder.lin_mobile.getVisibility() == View.VISIBLE) {

                    if (_holder.img_mobile.getVisibility() == View.VISIBLE) {
                        ApplicationData.calldialog(activity, "+47", phone,
                                activity.getResources().getString(R.string.call), activity.getResources().getString(R.string.cancel), new ApplicationData.DialogListener() {
                                    @Override
                                    public void diaBtnClick(int diaID, int btnIndex) {
                                        if (btnIndex == 2) {
                                            Intent intent = new Intent(Intent.ACTION_DIAL);
                                            intent.setData(Uri.parse("tel:" + "+47" + phone));
                                            activity.startActivity(intent);
                                        }
                                    }
                                }, 1);
                    }
                }
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final PopupWindow popupWindow = new PopupWindow(activity);

                LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View convertView;
                convertView = inflater.inflate(R.layout.adres_item_copystring, null);

                convertView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                // some other visual settings for popup window
                popupWindow.setFocusable(true);
                TextView txt_spin = (TextView) convertView.findViewById(R.id.txt_spin);
                popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                // popupWindow.setOutsideTouchable(true);
                // set the list view as pop up window content
                popupWindow.setOutsideTouchable(true);
                popupWindow.setContentView(convertView);

                int[] loc_int = new int[2];

//                Rect location = new Rect();
//                location.left = loc_int[0];
//                location.top = loc_int[1];
//                location.right = location.left + v.getWidth();
//                location.bottom = location.top + v.getHeight();


                if (!isFromTeacherChat) {
                    if (_items.get(position).sender.equals("me")) {
                        // popupWindow.showAsDropDown(v, location.right / 2, -(_holder.lin_sender.getMeasuredHeight()) - 100);

                        popupWindow.showAsDropDown(v, (v.getWidth() / 2) - 40, -(v.getHeight()) - 10);
                    } else {
                        popupWindow.showAsDropDown(v, (v.getWidth() / 2) - 40, -(v.getHeight()) - 10);
                    }
                } else {
                    if (_items.get(position).sender.equals("me")) {
                        // popupWindow.showAsDropDown(v, location.right / 2, -(_holder.lin_sender.getMeasuredHeight()) - 100);
                        popupWindow.showAsDropDown(v, (v.getWidth() / 2) - 40, -(v.getHeight()) - 80);
                    } else {
                        popupWindow.showAsDropDown(v, (v.getWidth() / 2) - 40, -(v.getHeight()) - 80);
                    }
                }

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
        return convertView;
    }

    private String gettimeval(long days, String created_at) {
        String time_value = "";
        String[] val = new String[2];
       /* if (days == 0) {
            val = created_at.split(",");
            if (val.length > 1)
                time_value = val[1];
            return time_value;
        }
        *//*else if(days==1)
            time_value=activity.getString(R.string.yesterday);*//*
        else*/
        time_value = created_at;

        return time_value;
    }

    private void setcopybackground(int position, PopupWindow popupWindow, View v, int y) {

    }
}