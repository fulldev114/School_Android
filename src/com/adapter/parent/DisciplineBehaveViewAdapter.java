package com.adapter.parent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudstream.cslink.parent.ApplicationData;
import com.cloudstream.cslink.R;
import com.common.utils.ConstantApi;
import com.common.utils.GlobalConstrants;
import com.common.view.CircularImageView;
import com.request.AsyncTaskCompleteListener;
import com.request.ETechAsyncTask;
import com.widget.textstyle.CustomGridView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by etech on 10/10/16.
 */
public class DisciplineBehaveViewAdapter extends BaseAdapter {

    private final RelativeLayout rel_markview;
    private final LinearLayout lin_note;
    private Childbeans child_data;
    private ArrayList<Childbeans> markarray;
    private Activity context;
    private HashMap<String, ArrayList<Childbeans>> arraylist;
    private ListView lst_ds;
    private ArrayList<String> semester_name = new ArrayList<>();
    private boolean b;
    private final ArrayList<Boolean> flag = new ArrayList<Boolean>();
    ArrayList<HashMap<String, HashMap<Integer, String>>> map;
    boolean shown_flag = true;
    private PopupWindow popUp;
    private Dialog dlg;

    public DisciplineBehaveViewAdapter(Activity context, ArrayList<Childbeans> markarray, ListView lst_ds, Childbeans child_data,
                                       RelativeLayout rel_markview, LinearLayout lin_note) {
        this.context = context;
        this.markarray = markarray;
        this.lst_ds = lst_ds;
        this.child_data = child_data;
        this.rel_markview = rel_markview;
        this.lin_note = lin_note;
    }

    public void updatenotify(Activity context, ArrayList<Childbeans> markarray, ListView lst_ds, Childbeans child_data) {

        this.context = context;
        this.markarray = markarray;
        this.lst_ds = lst_ds;
        this.child_data = child_data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return markarray.size();
    }

    @Override
    public Object getItem(int position) {
        return markarray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolderChild {
        public TextView txt_sender_name, txt_date, txt_comment;
        public CustomGridView grid_mark;
        public LinearLayout lin_note, ly_sublist;
        public CircularImageView img_profile;
        public TextView txt_disciplin_name, txt_remark;
        public ImageView imgdel;
        public LinearLayout lin_receive;
        public View view_line;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewHolderChild holderchild;
        if (convertView == null) {
            holderchild = new ViewHolderChild();
            convertView = inflater.inflate(R.layout.item_view_discipline, parent, false);

            holderchild.txt_disciplin_name = (TextView) convertView.findViewById(R.id.txt_disciplin_name);
            holderchild.txt_remark = (TextView) convertView.findViewById(R.id.txt_remark);
            holderchild.grid_mark = (CustomGridView) convertView.findViewById(R.id.grid_mark);
            holderchild.ly_sublist = (LinearLayout) convertView.findViewById(R.id.ly_sublist);
            holderchild.img_profile = (CircularImageView) convertView.findViewById(R.id.from_img_profile);
            holderchild.imgdel = (ImageView) convertView.findViewById(R.id.imgdel);
            holderchild.txt_comment = (TextView) convertView.findViewById(R.id.txt_comment);
            holderchild.txt_sender_name = (TextView) convertView.findViewById(R.id.txt_sender_name);
            holderchild.txt_date = (TextView) convertView.findViewById(R.id.txt_date);
            holderchild.lin_receive = (LinearLayout) convertView.findViewById(R.id.lin_receive);
            holderchild.view_line=(View)convertView.findViewById(R.id.view_line);

            convertView.setTag(holderchild);
        } else {
            holderchild = (ViewHolderChild) convertView.getTag();
        }

        final Childbeans childname = markarray.get(position);

        if (childname.descipline_id.equalsIgnoreCase("1")) {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holderchild.lin_receive.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.blue_bubble_db));
            } else {
                holderchild.lin_receive.setBackground(context.getResources().getDrawable(R.drawable.blue_bubble_db));
            }
        } else if (childname.descipline_id.equalsIgnoreCase("2")) {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holderchild.lin_receive.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_1));
            } else {
                holderchild.lin_receive.setBackground(context.getResources().getDrawable(R.drawable.bg_1));
            }
        }

        if (childname.descipline_name != null && childname.descipline_name.length() > 0) {
            holderchild.txt_disciplin_name.setText(childname.descipline_name + context.getString(R.string.collon));
        }
        if (childname.remarks_name != null && childname.remarks_name.length() > 0) {
            holderchild.txt_remark.setText(childname.remarks_name);
        }
        else
        {
            holderchild.txt_disciplin_name.setText(childname.descipline_name);
        }

        if (childname.comment != null && childname.comment.length() > 0) {
            holderchild.view_line.setVisibility(View.VISIBLE);
            holderchild.txt_comment.setVisibility(View.VISIBLE);
            holderchild.txt_comment.setText(childname.comment);
        }
        else
        {
            holderchild.view_line.setVisibility(View.GONE);
            holderchild.txt_comment.setVisibility(View.GONE);
        }

        if (childname.date != null && childname.date.length() > 0) {
            holderchild.txt_date.setText(ApplicationData.convertToNorweiDate(childname.date, context));
        }
        if (childname.teacher_name != null && childname.teacher_name.length() > 0) {
            holderchild.txt_sender_name.setText(childname.teacher_name);
        }
        if (childname.image != null && childname.image.length() > 0) {
            ApplicationData.setProfileImg(context, ApplicationData.web_server_url + "/uploads/" + childname.image, holderchild.img_profile);
        }

        holderchild.imgdel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ApplicationData.calldialog(context, "", context.getString(R.string.delete_item), context.getString(R.string.str_yes), context.getString(R.string.str_no), new ApplicationData.DialogListener() {
                    @Override
                    public void diaBtnClick(int diaID, int btnIndex) {
                        if (btnIndex == 2) {
                            HashMap<String, Object> map = new HashMap<String, Object>();
                            map.put("desc_id", childname.id);
                            if (!GlobalConstrants.isWifiConnected(context)) {
                                return;
                            }
                            ETechAsyncTask task = new ETechAsyncTask(context, new AsyncTaskCompleteListener<String>() {
                                @Override
                                public void onTaskComplete(int statusCode, String result, String webserviceCb, Object tag) {
                                    try {
                                        if (statusCode == ETechAsyncTask.COMPLETED) {
                                            JSONObject jObject = new JSONObject(result);
                                            try {
                                                if (webserviceCb.equalsIgnoreCase(ConstantApi.DELETE_DISCIPLINE_DATA)) {
                                                    String flag = jObject.getString("flag");
                                                    if (flag.equalsIgnoreCase("1")) {
                                                        ApplicationData.showMessage(context, "", context.getString(R.string.delete_desc),
                                                                context.getString(R.string.str_ok));

                                                        markarray.remove(position);
                                                        updatenotify(context, markarray, lst_ds, child_data);
                                                        if (markarray.size() > 0) {
                                                            rel_markview.setVisibility(View.VISIBLE);
                                                            lin_note.setVisibility(View.GONE);
                                                        } else {
                                                            rel_markview.setVisibility(View.GONE);
                                                            lin_note.setVisibility(View.VISIBLE);
                                                        }
                                                    } else {
                                                        if (jObject.has("msg")) {
                                                            String msg = jObject.getString("msg");
                                                            ApplicationData.showMessage(context, "", msg, context.getString(R.string.str_ok));
                                                        }
                                                    }
                                                }
                                            } catch (Exception e) {
                                                Log.e("DisciplineBehaveRepot", "onTaskComplete() " + e, e);
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, ConstantApi.DELETE_DISCIPLINE_DATA, map);
                            task.execute(ApplicationData.main_url + ConstantApi.DELETE_DISCIPLINE_DATA + ".php?");

                        }
                    }

                }, 2);
            }
        });
        return convertView;
    }
}
