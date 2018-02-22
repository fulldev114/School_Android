package com.adapter.parent;

import com.common.Bean.MarkBean;

import java.io.Serializable;
import java.util.ArrayList;

public class Childbeans implements Serializable {

    public String child_image;
    public String child_name;
    public String school_name;
    public String child_moblie;
    public String school_class_id;
    public String user_id;
    public String child_gender;
    public String child_age;
    public String school_id;
    public String class_id;
    public String class_name;
    public String child_subject_name;
    public String child_subject_id;
    public String child_teacher_id;
    public String child_class_id;
    public String child_temlate_id;
    public String child_school_id;
    public String child_template_title;
    public String message_id;
    public String sender_id;
    public String sendername;
    public String subject_name;
    public String created_at;
    public String senderimage;
    public String name;
    public String image;
    public String message_body="";
    public String sender;
    public String child_period_id;
    public String senderschool;
    public String subject_id;
    public String emailaddress;
    public String message_subject;
    public String message_desc;
    public String child_marked_id;
    public String chat_time;

    public int badge = 0,abi_badge=0,abn_badge=0;
    public String sender_id_jid = "";
    public String teacher_name = "";
    public String teacher_id_jid = "";
    public String receiver_id_jid = "";
    public String message_status = "";
    public String parent_id = "";
    public String teacher_id = "";

    public long messageTimeMilliseconds;
    public int messageType;
    public String servicename;
    public String username;
    public String message;
    private int messageRowId;
    private String to;
    private String from;
    public String hostname;
    public String receiver_image = "";
    public String receiver_name = "";
    public String isread;
    public String isab;
    public String pri_message_id;
    public String fromname;
    public String rolename;
    public String toname;
    public String jid;
    public String jid_pwd;
    public String parent_name;
    public String parent2_name;
    public String parent1_phone;
    public String parent2_phone;
    public String incharger;
    public String nc_mobile;
    public String contactmobile;
    public String status1;
    public String status2;
    public String status3;

    //semester
    public String semester_id = "";
    public String semester_name = "";
    public String character_id = "";
    public String character_name = "";
    public String year = "";
    public String mark = "";
    public String comment = "";
    public String exam_about = "";
    public boolean typing = false;
    public String sendbyid = "";
    public boolean flag = false;
    public String parent1email="";
    public String parent2email="",contactname="";
    public String parentno="";
    public boolean iscarboncopy=false;
    public String type="";
    public int abi=0,abn=0,emb=0,chb=0;
    public String descipline_id="",descipline_name="",remarks_name="",date="",remarks_id="",id="";

    public Childbeans(String to, String message, long messageTimeMilliseconds, int messageType) {
        this.to = to;
        this.message = message;
        this.messageTimeMilliseconds = messageTimeMilliseconds;
        this.messageType = messageType;
    }

    public ArrayList<MarkBean> markarray = new ArrayList<MarkBean>();

    public Childbeans() {

    }
    public void addotherdetail(Childbeans bean,Childbeans markdetail) {
        bean.user_id=markdetail.user_id;
        bean.year = markdetail.year;

        bean.semester_id = markdetail.semester_id;
        bean.class_id = markdetail.class_id;
        bean.subject_id = markdetail.subject_id;
        bean.created_at = markdetail.created_at;
        bean.semester_name =markdetail.semester_name;
        bean.subject_name =markdetail.subject_name;
        bean.teacher_name=markdetail.teacher_name;
    }
}
