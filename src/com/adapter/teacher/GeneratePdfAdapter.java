package com.adapter.teacher;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cloudstream.cslink.teacher.ActivityAddMark;
import com.cloudstream.cslink.teacher.ApplicationData;
import com.cloudstream.cslink.R;
import com.cloudstream.cslink.teacher.ReportCardActivity;
import com.cloudstream.cslink.teacher.SlideShowActivity;
import com.common.Bean.MarkBean;
import com.common.utils.AppUtils;
import com.common.view.AttachmentMap;
import com.widget.textstyle.CustomGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by etech on 10/10/16.
 */
public class GeneratePdfAdapter extends BaseAdapter {

    private Childbeans child_data;
    private ArrayList<Childbeans> markarray;
    private Context context;
    private HashMap<String, ArrayList<Childbeans>> arraylist;
    private ListView expan_report;
    private ArrayList<String> semester_name = new ArrayList<>();
    private boolean b;
    ArrayList<HashMap<String, HashMap<Integer, String>>> map;
    boolean shown_flag = true;
    private PopupWindow popUp;
    private final ArrayList<Boolean> flag = new ArrayList<Boolean>();
    private Dialog dlg;
    private static String SDCARD_PERMISSIONS[] = new String[]{
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };
    private final static int REQUEST_CONTACTS_CODE = 100;
    String including_image = "no";

    public GeneratePdfAdapter(Context context, ArrayList<Childbeans> markarray, ArrayList<String> semester_name,
                              ListView expand_report, Childbeans child_data) {
        this.context = context;
        this.markarray = markarray;
        this.semester_name = semester_name;
        this.expan_report = expand_report;
        this.child_data = child_data;

        for (int i = 0; i < markarray.size(); i++) {
            flag.add(false);
        }
    }

    public void updatenotify(Context context, ArrayList<Childbeans> markarray,
                             ArrayList<String> semester_name, ListView expand_report, Childbeans child_data) {

        this.context = context;
        this.markarray = markarray;
        this.semester_name = semester_name;
        this.expan_report = expand_report;
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
        public TextView txt_sub_nm;
        public CustomGridView grid_mark;
        public LinearLayout lin_note, ly_sublist;
        public CheckBox chk_item;
        public LinearLayout lin_chk;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewHolderChild holderchild;
        if (convertView == null) {
            holderchild = new ViewHolderChild();
            convertView = inflater.inflate(R.layout.adres_item_mark_checkbox, parent, false);
            holderchild.txt_sub_nm = (TextView) convertView.findViewById(R.id.txt_sub_nm);
            holderchild.grid_mark = (CustomGridView) convertView.findViewById(R.id.grid_mark);
            holderchild.ly_sublist = (LinearLayout) convertView.findViewById(R.id.ly_sublist);
            holderchild.chk_item = (CheckBox) convertView.findViewById(R.id.chk_item);
            holderchild.lin_chk = (LinearLayout) convertView.findViewById(R.id.lin_chk);
            convertView.setTag(holderchild);
        } else {
            holderchild = (ViewHolderChild) convertView.getTag();
        }

        holderchild.grid_mark.setNumColumns(4);// gridView.setGravity(Gravity.CENTER);//

        Childbeans childname = markarray.get(position);
        if (childname != null && childname.subject_name != null && childname.subject_name.length() > 0) {
            holderchild.txt_sub_nm.setText(childname.subject_name);
        }

        GridViewadpater adpater = new GridViewadpater(context, childname, position);
        holderchild.grid_mark.setAdapter(adpater);


        //Select all is checked then all checkbox is checked
        holderchild.chk_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    flag.set(position, true);
                    notifyDataSetChanged();
                } else {
                    flag.set(position, false);
                    notifyDataSetChanged();
                }

            }
        });

        holderchild.lin_chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag.get(position) == true) {
                    holderchild.chk_item.setChecked(false);
                    flag.set(position, false);
                    notifyDataSetChanged();
                } else {
                    holderchild.chk_item.setChecked(true);
                    flag.set(position, true);
                    notifyDataSetChanged();
                }
            }
        });

        holderchild.chk_item.setChecked(flag.get(position));

        return convertView;
    }


    //popup window adapter
    class GridViewadpater extends BaseAdapter {
        private final Childbeans data;
        private final int parentposition;
        Context context;
        ArrayList<Boolean> check_flag;

        public GridViewadpater(Context context, Childbeans data, int parentposition) {
            this.context = context;
            this.data = data;
            this.parentposition = parentposition;
        }

        @Override
        public int getCount() {
            return data.markarray.size();
        }

        @Override
        public Object getItem(int position) {
            return data.markarray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ViewHolderStudent holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.adres_item_grid_mark, parent, false);
                holder = new ViewHolderStudent();
                //  holder.txt_number = (TextView) convertView.findViewById(R.id.txt_number);
                //  holder.lin_class = (LinearLayout) view.findViewById(R.id.lin_class);
                holder.img_mark = (ImageView) convertView.findViewById(R.id.img_mark);
                holder.img_info = (ImageView) convertView.findViewById(R.id.img_info);
                holder.lyt_info = (LinearLayout) convertView.findViewById(R.id.lyt_info);
                holder.view_line = (View) convertView.findViewById(R.id.view_line);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolderStudent) convertView.getTag();
            }

            final MarkBean bean = data.markarray.get(position);

            //holder.txt_number.setText(bean.mark);

            if (bean.comment != null && bean.comment.length() > 0) {
                holder.img_info.setImageResource(R.drawable.info_green);
                holder.img_info.setEnabled(true);
            } else {
                holder.img_info.setImageResource(R.drawable.gray_info);
                holder.img_info.setEnabled(false);
            }

            if (bean.image != null && bean.image.length() > 0) {
                holder.img_mark.setImageResource(R.drawable.green_image);
                holder.img_mark.setEnabled(true);
            } else {
                holder.img_mark.setImageResource(R.drawable.gray_image);
                holder.img_mark.setEnabled(false);
            }

            int k = position;
            if (((k + 1) % 4 == 0) || position == data.markarray.size() - 1)
                holder.view_line.setVisibility(View.GONE);
            else
                holder.view_line.setVisibility(View.VISIBLE);

            holder.img_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.comment != null && bean.comment.length() > 0) {
                        try {
                            showDetailDialog(data.markarray.get(position), position, parentposition);
                            //ApplicationData.showMessage(context, context.getString(R.string.comment), bean.comment, context.getString(R.string.str_ok));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            holder.img_mark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<AttachmentMap> imgList = new ArrayList<>();
                    AttachmentMap attach = new AttachmentMap();
                    attach.setAttachmentName(bean.image);
                    imgList.add(attach);

                    if (imgList != null && imgList.size() > 0) {
                        Intent intent = new Intent(context, SlideShowActivity.class);
                        intent.putExtra("ImageList", imgList);
                        context.startActivity(intent);
                    }
                }
            });
            return convertView;
        }
    }

    public void showDetailDialog(final MarkBean markBean, final int position, final int parentposition) {

        if (dlg != null && dlg.isShowing())
            dlg.dismiss();
        dlg = new Dialog(context);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.adres_dlg_report_view_dialog);
        //  dlg.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.dark_blue)));
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dlg.setCanceledOnTouchOutside(false);
        dlg.show();

        TextView txtTitle = (TextView) dlg.findViewById(R.id.txtTitle);
        TextView text_class = (TextView) dlg.findViewById(R.id.text_class);
        TextView txt_sub = (TextView) dlg.findViewById(R.id.txt_sub);
        TextView txt_exam_about = (TextView) dlg.findViewById(R.id.txt_exam_about);
        TextView txt_comment = (TextView) dlg.findViewById(R.id.txt_comment);
        TextView txt_marks = (TextView) dlg.findViewById(R.id.txt_marks);
        TextView txt_ok = (TextView) dlg.findViewById(R.id.txt_ok);
        TextView txt_download = (TextView) dlg.findViewById(R.id.txt_download);
        LinearLayout lay_edit = (LinearLayout) dlg.findViewById(R.id.lay_edit);
        final CheckBox chkbox = (CheckBox) dlg.findViewById(R.id.chk_box);
        LinearLayout lin_chk = (LinearLayout) dlg.findViewById(R.id.lin_chk);

        txtTitle.setText(child_data.child_name);
        text_class.setText(child_data.class_name);
        txt_sub.setText(markarray.get(parentposition).subject_name + " (" + context.getString(R.string.testno) + " " + markBean.exam_no + ")");
        txt_exam_about.setText(markBean.exam_about);
        txt_marks.setText(markBean.mark);
        txt_comment.setText(markBean.comment);


        txt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });

        txt_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Childbeans> subtopdfarray = new ArrayList<Childbeans>();
                ArrayList<Childbeans> studentlist = new ArrayList<Childbeans>();
                studentlist.add(child_data);
                Childbeans bean = new Childbeans();
                bean.addotherdetail(bean, markarray.get(parentposition));
                MarkBean markarray1 = markarray.get(parentposition).markarray.get(position);
                bean.markarray.add(markarray1);

                subtopdfarray.add(bean);
                ((ReportCardActivity) context).calluploadpdf(context, subtopdfarray, child_data, including_image, markBean.exam_no);

            }
        });

        lin_chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkbox.isChecked()) {
                    chkbox.setChecked(false);
                    including_image = "no";
                } else {
                    chkbox.setChecked(true);
                    including_image = "yes";
                }
            }
        });


        chkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkbox.setChecked(true);
                    including_image = "yes";
                } else {
                    chkbox.setChecked(false);
                    including_image = "no";
                }
            }
        });

        lay_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ActivityAddMark.class);
                i.putExtra("user_detail", markarray.get(parentposition));
                i.putExtra("mark_detail", markBean);
                i.putExtra("parentposition", parentposition);
                i.putExtra("position", position);
                i.putExtra("fromdowload", true);
                context.startActivity(i);
            }
        });

    }

    class ViewHolderStudent {

        public TextView txt_number;
        public LinearLayout lyt_info;
        public ImageView img_mark, img_info;
        public View view_line;
    }

    //fetch data to generate pdf
   /* public List<Childbeans> getselectedarray(Context context) {

        ArrayList<Childbeans> arraych= new ArrayList<Childbeans>();

        for(int loop=0;loop<flag.size();loop++)
        {
            if(flag.get(loop))
            {
                arraych.add(markarray.get(loop));
            }
        }
        return arraych;
    }*/
    public String getSelectedSubId(Context context) {

        String selected_subjects_id = "";
        int counter = 0;
        for (int loop = 0; loop < flag.size(); loop++) {
            if (flag.get(loop)) {
                if (counter == 0)
                    selected_subjects_id = markarray.get(loop).subject_id;
                else
                    selected_subjects_id = selected_subjects_id + "," + markarray.get(loop).subject_id;

                counter++;
            }
        }
        return selected_subjects_id;
    }


}

