package com.db.parent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;
import android.util.Log;

import com.cloudstream.cslink.parent.ApplicationData;
import com.cloudstream.cslink.parent.ChatActivity;
import com.cloudstream.cslink.R;
import com.adapter.parent.Childbeans;
import com.common.Bean.BadgeBean;
import com.xmpp.parent.Constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * @author Aalap Shah
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private final String tag = "DatabseHelper";
    private static String DB_NAME = "CSlink.sqlite";
    private static String TABLE_NAME_BDG = "badge_table";
    private static String CHAT_MESSAGE_BADGE = "chat_msg_badge";
    private static String CHAT_HISTORY_TABLE = "chat_msg_history";
    private static String MESSAGE_DELIVERY_STATUS = "message_delivery_status";
    private final static int DATABASE_VERSION = 1;
    String MESSAGE_ID = "Message_id";
    String User_id = "User_id";
    String Username = "Username";
    String Message = "Message";
    String Child_marked_id = "Child_marked_id";
    String Child_mobile = "Child_mobile";
    String Sender_jid = "Sender_jid";
    String Receiver_id = "Receiver_id";
    String Receiver_jid = "Receiver_jid";
    String Receiver_name = "Receiver_name";
    String Message_status = "Message_status";
    String Sender_image = "Sender_image";
    String Receiver_image = "Receiver_image";
    String is_fromme = "is_fromme";
    String Created = "Created";

    String Alert = "Alert";
    String MessgeType = "MessgeType";
    String NOTI_Message = "Message";
    String KidId = "KidId";
    String FromId = "FromId";
    String KidName = "KidName";
    String Badge = "Badge";
    String Messageid = "Messageid";
    String AllBadge = "AllBadge";
    String Abi = "Abi";
    String Abn = "Abn";
    String Chb = "Chb";

    //ImagePic & ImageLogo removed from corpLoc
    final String chat_history = "CREATE TABLE "+ CHAT_HISTORY_TABLE +
            "(Message_id VARCHAR," +
            "User_id VARCHAR," +
            "Username VARCHAR," +
            "Message VARCHAR," +
            "Child_marked_id VARCHAR," +
            "Child_mobile VARCHAR," +
            "Sender_jid VARCHAR," +
            "Receiver_id VARCHAR," +
            "Receiver_jid VARCHAR," +
            "Receiver_name VARCHAR," +
            "Message_status VARCHAR," +
            "Sender_image VARCHAR" +
            ",Receiver_image VARCHAR" +
            ",is_fromme TEXT" +
            ",Created VARCHAR)";

    final String chat_badge = "CREATE TABLE " + CHAT_MESSAGE_BADGE +
            "(Id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "Sender_jid VARCHAR," +
            "Receiver_jid VARCHAR," +
            "Message VARCHAR," +
            "User_id VARCHAR," +
            "Messageid VARCHAR," +
            "Badge VARCHAR," +
            "AllBadge VARCHAR," +
            "Created DATETIME)";

    final String badge = "CREATE TABLE " + TABLE_NAME_BDG +
            "(Id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "Alert VARCHAR," +
            "MessgeType VARCHAR," +
            "Message VARCHAR," +
            "KidId VARCHAR," +
            "FromId VARCHAR," +
            "KidName VARCHAR," +
            "Abi VARCHAR," +
            "Abn VARCHAR," +
            "Chb VARCHAR," +
            "Badge VARCHAR)";

    final String message_delivery_status = "CREATE TABLE " + MESSAGE_DELIVERY_STATUS +
            "(Message_id VARCHAR," +
            "Sender_jid VARCHAR," +
            "Receiver_jid VARCHAR," +
            "Message_status VARCHAR)";

    private static DatabaseHelper mDBConnection;
    SQLiteDatabase db;
    private String TAG = "DatabaseHelper";


    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();
    }

    public static synchronized DatabaseHelper getDBAdapterInstance(Context context) {
        if (mDBConnection == null) {
            mDBConnection = new DatabaseHelper(context);
        }
        return mDBConnection;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(chat_history);
            db.execSQL(badge);
            db.execSQL(chat_badge);
            db.execSQL(message_delivery_status);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+CHAT_HISTORY_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_BDG);
        db.execSQL("DROP TABLE IF EXISTS "+CHAT_MESSAGE_BADGE);
        db.execSQL("DROP TABLE IF EXISTS "+MESSAGE_DELIVERY_STATUS);

        onCreate(db);
    }

    public void createTable(String tableName, HashMap<String, String> primaryKey, HashMap<String, String> columns) {
        //CREATE TABLE tbl_corploc_nearby (CorpLocationID VARCHAR PRIMARY KEY  NOT NULL , CorporationID VARCHAR, CorpLocationName VARCHAR, Address1 VARCHAR, Address2 VARCHAR, City VARCHAR, State VARCHAR, Country VARCHAR, Zip VARCHAR, Email VARCHAR, Phone VARCHAR, ImageLogoURL VARCHAR, Latitude VARCHAR, Longitude VARCHAR, ImageUrl VARCHAR, ClassID VARCHAR, Created DATETIME, ActiveFlag VARCHAR, UserOptInFlag VARCHAR, DistanceInMiles DOUBLE DEFAULT (null) , LastChangeDate DATETIME, LastChangeDateTimeZone VARCHAR DEFAULT (null) , LastChangeDateTimezoneType VARCHAR)
        StringBuilder createTableQuery = new StringBuilder("Create Table ");
        createTableQuery.append(tableName + " ( ");
        if (primaryKey != null) {
            List<String> list = new ArrayList<String>(primaryKey.keySet());
            String columnName = list.get(0);
            createTableQuery.append(columnName + " " + primaryKey.get(columnName) + " PRIMARY KEY NOT NULL, ");
        }
        if (columns != null) {
            ArrayList<String> columnNames = new ArrayList<String>(columns.keySet());
            for (String columnName : columnNames) {
                createTableQuery.append(columnName + " " + columns.get(columnName) + ",");
            }
        }
        createTableQuery.append(")");

        Log.i(tag, "createTableQuery " + createTableQuery);

    }


    /**
     * This function used to insert the Record in DB.
     *
     * @param tableName
     * @param nullColumnHack
     * @param initialValues
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public long insertRecordsInDB(String tableName, String nullColumnHack, ContentValues initialValues) {
        return db.insert(tableName, nullColumnHack, initialValues);
    }

    /**
     * This function used to update the Record in DB.
     *
     * @param tableName
     * @param initialValues
     * @param whereClause
     * @param whereArgs
     * @return 0 in case of failure otherwise return no of row(s) are updated
     */
    public int updateRecordsInDB(String tableName, ContentValues initialValues, String whereClause, String whereArgs[]) {
        return db.update(tableName, initialValues, whereClause, whereArgs);
    }

    /**
     * This function used to delete the Record in DB.
     *
     * @param tableName
     * @param whereClause
     * @param whereArgs
     * @return 0 in case of failure otherwise return no of row(s) are deleted.
     */
    public int deleteRecordInDB(String tableName, String whereClause, String[] whereArgs) {
        return db.delete(tableName, whereClause, whereArgs);
    }


    public ArrayList<HashMap<String, String>> selectRecordsFromDBList(String query, String[] selectionArgs) {
        ArrayList<HashMap<String, String>> retList = null;
        try {
            HashMap<String, String> mapRow;

            Cursor cursor = db.rawQuery(query, selectionArgs);

            if (cursor.moveToFirst()) {
                retList = new ArrayList<HashMap<String, String>>();

                do {
                    mapRow = new HashMap<String, String>();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        mapRow.put(cursor.getColumnName(i), cursor.getString(i));
                    }
                    retList.add(mapRow);
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            return retList;
        } catch (Exception e) {
            Log.e("selectRecordsFromDBLis", e.getMessage(), e);
            retList = null;
        }
        return retList;
    }

    /**
     * This method is for only single record fetch
     *
     * @param query
     * @param selectionArgs
     * @return single row fetched from query
     */
    public HashMap<String, String> selSingleRecordFromDB(String query, String[] selectionArgs) {

        HashMap<String, String> mapRow = null;
        try {
            Cursor cursor = db.rawQuery(query, selectionArgs);

            if (cursor.moveToFirst()) {
                mapRow = new HashMap<String, String>();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    mapRow.put(cursor.getColumnName(i), cursor.getString(i));
                }
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(tag, "selSingleRecordFromDB Error:" + e, e);
            mapRow = null;
        }
        return mapRow;
    }

    /*public void open() {
        if(mDBConnection!=null)
        mDBConnection.open();
    }*/


    public void inserthistory(Childbeans childbeans) {
        boolean isexist = false;
        String query = "Select * from chat_msg_history where " + MESSAGE_ID + "=" + '"' + childbeans.message_id + '"';
        Cursor cr = db.rawQuery(query, null);

        if (cr != null && cr.getCount() > 0) {
            isexist = true;
        }
        ContentValues cv = new ContentValues();
        cv.put(MESSAGE_ID, childbeans.message_id);
        cv.put(User_id, childbeans.sender_id);
        cv.put(Username, childbeans.name);
        cv.put(Message, childbeans.message_body);
        cv.put(Child_marked_id, childbeans.child_marked_id);
        cv.put(Child_mobile, childbeans.child_moblie);
        cv.put(Sender_jid, childbeans.sender_id_jid);
        cv.put(Receiver_id, childbeans.teacher_id);
        cv.put(Receiver_jid, childbeans.receiver_id_jid);
        cv.put(Message_status, childbeans.message_status);
        cv.put(Sender_image, childbeans.image);
        cv.put(is_fromme, childbeans.sender);
        cv.put(Created, childbeans.created_at);

        if (!isexist) {
            db.insert(CHAT_HISTORY_TABLE, null, cv);
        } else
            db.update(CHAT_HISTORY_TABLE, cv, MESSAGE_ID + "=" + '"' + childbeans.message_id + '"', null);
    }

    public void insertdeliverystatus(Childbeans childbeans) {

        ContentValues cv = new ContentValues();
        cv.put(MESSAGE_ID, childbeans.message_id);
        cv.put(Sender_jid, childbeans.sender_id_jid);
        cv.put(Receiver_jid, childbeans.receiver_id_jid);
        cv.put(Message_status, "Seen");
        db.insert(MESSAGE_DELIVERY_STATUS, null, cv);
    }

    public void insertarrayhistory(ArrayList<Childbeans> msglist) {
        for (int i = 0; i < msglist.size(); i++) {
            Childbeans childbeans = msglist.get(i);
            ContentValues cv = new ContentValues();
            cv.put(User_id, childbeans.sender_id);
            cv.put(Username, childbeans.name);
            cv.put(Message, childbeans.message_body);
            cv.put(Child_marked_id, childbeans.child_marked_id);
            cv.put(Child_mobile, childbeans.child_moblie);
            cv.put(Sender_jid, childbeans.sender_id_jid);
            cv.put(Receiver_id, childbeans.teacher_id);
            cv.put(Receiver_jid, childbeans.receiver_id_jid);
            cv.put(Receiver_name, childbeans.receiver_name);
            cv.put(Message_status, childbeans.message_status);
            cv.put(Sender_image, childbeans.image);
            cv.put(is_fromme, childbeans.sender);
            cv.put(Created, childbeans.created_at);
            Log.e("insert data", childbeans.message_body);
            db.insert("chat_msg_history", null, cv);
        }
    }

    public boolean updatestatus(Context context, Childbeans data) {
        ContentValues cv = new ContentValues();
        cv.put("Message_status", data.message_status);
        db.update("chat_msg_history", cv, "(" + Receiver_jid + "='" + data.receiver_id_jid + "' AND Sender_jid='" + data.sender_id_jid + "' OR " +
                Receiver_jid + "='" + data.sender_id_jid + "' AND Sender_jid='" + data.receiver_id_jid + "') AND " + MESSAGE_ID
                + "='" + data.message_id + "'", null);
        return true;
    }

    public void insertbadge(String alert, String message, String type, String kidid,
                            String from_id, String kidname, int abibadge, int abnbadge, int chbbadge, int badge) {
        boolean isdataexists = false;
        String badgeno = "0", abi, abn, chb = null;
        Log.d(TAG, "badge value : " + abnbadge);
        String query = "select * from " + TABLE_NAME_BDG + " where " + KidId + "=" + '"' + kidid + '"';//MessgeType + "=" + '"' + type + '"' + " AND " +
        Cursor cr = db.rawQuery(query, null);

        cr.moveToFirst();
        if (cr != null && cr.getCount() > 0) {
            abi = cr.getString(7);
            abn = cr.getString(8);
            chb = cr.getString(9);
            badgeno = cr.getString(10);
            isdataexists = true;
        }
        cr.close();
       /* if (!badgeno.equalsIgnoreCase("0"))
            badge = Integer.parseInt(badgeno) + 1;
        else
            badge++;*/

        ContentValues cv = new ContentValues();
        cv.put(Alert, alert);
        cv.put(MessgeType, type);
        cv.put(NOTI_Message, message);
        cv.put(KidId, kidid);
        cv.put(FromId, from_id);
        cv.put(KidName, kidname);
        cv.put(Abi, String.valueOf(abibadge));
        cv.put(Abn, String.valueOf(abnbadge));
        if(type.equalsIgnoreCase("chat_msg"))
        cv.put(Chb, String.valueOf(chbbadge));
        else
            cv.put(Chb, chb);
        cv.put(Badge, badge);
        if (!isdataexists)
            db.insert(TABLE_NAME_BDG, null, cv);
        else
            db.update(TABLE_NAME_BDG, cv, KidId + "=" + "'" + kidid + "'", null);//MessgeType + "=" + "'" + type + "' AND " +
    }

    public void insertbadgeifnot(String alert, String message, String type, String kidid,
                                 String from_id, String kidname, int abi, int abn, String tot) {
        String badgeno = "0";
        String query = "select * from " + TABLE_NAME_BDG + " where " + KidId + "=" + "'" + kidid + "'";// MessgeType + "=" + "'" + type + "' AND " +
        Cursor cr = db.rawQuery(query, null);
        cr.moveToFirst();

        //add value to Contenvalues
        ContentValues cv = new ContentValues();
        cv.put(Alert, alert);
        cv.put(MessgeType, type);
        cv.put(NOTI_Message, message);
        cv.put(KidId, kidid);
        cv.put(FromId, from_id);
        cv.put(KidName, kidname);
        cv.put(Abi, abi + "");
        cv.put(Abn, abn + "");

        if (cr != null && cr.getCount() > 0) { // check data is already exists or not
          /*  int chbbg = Integer.parseInt(cr.getString(9));
            int newbg = chbbg + abi + abn;
            cv.put(Chb, chbbg);
            cv.put(Badge, newbg + "");
            db.update(TABLE_NAME_BDG, cv, KidId + "=" + "'" + kidid + "'", null);*/
        } else {
            int newbg=abi+abn;
            cv.put(Chb, "0");
            cv.put(Badge, newbg);
            db.insert(TABLE_NAME_BDG, null, cv);
        }

    }

    public void clearbadge(String type, String kidid) {
        String query = "select * from " + TABLE_NAME_BDG + " where " + KidId + "=" + "'" + kidid + "'";//MessgeType + "=" + "'" + type + "' AND " +
        Cursor cr = db.rawQuery(query, null);

        cr.moveToFirst();
        if (cr != null && cr.getCount() > 0) {
            ContentValues cv = new ContentValues();
            int val = 0, badge = 0;
            if (type.equals("abi")) {
                val = Integer.parseInt(cr.getString(7));
                cv.put(Abi, "0");
            } else if (type.equals("abn")) {
                val = Integer.parseInt(cr.getString(8));
                cv.put(Abn, "0");
            } else if (type.equals("chb")) {
                val = Integer.parseInt(cr.getString(9));
                cv.put(Chb, "0");
            }

            badge = Integer.parseInt(cr.getString(10)) - val;
            cv.put(Badge, badge + "");
            db.update(TABLE_NAME_BDG, cv, KidId + "=" + "'" + kidid + "'", null);// MessgeType + "=" + "'" + type + "' AND " +
        }

    }


    public void insertchatbadge(Childbeans newMessage, String childid) {
        boolean isdataexists = false;
        String badgeno = "0", allbadge = "0";
        int badge = 0;

        String query = "select Badge from " + CHAT_MESSAGE_BADGE + " where " + Sender_jid + "=" + '"' + newMessage.receiver_id_jid +
                '"' + " AND " + Receiver_jid + "=" + '"' + newMessage.sender_id_jid + '"' + " AND " + Messageid + "=" + '"' + newMessage.message_id + '"';
        Cursor cr = db.rawQuery(query, null);

        cr.moveToFirst();
        if (cr != null && cr.getCount() > 0) {
            badgeno = cr.getString(0);
            isdataexists = true;
            cr.close();
            badge = Integer.parseInt(badgeno);
        } else {
            String query1 = "select Badge,User_id from " + CHAT_MESSAGE_BADGE + " where " + Sender_jid + "=" + '"' + newMessage.receiver_id_jid +
                    '"' + " AND " + Receiver_jid + "=" + '"' + newMessage.sender_id_jid + '"';
            Cursor cr1 = db.rawQuery(query1, null);

            cr1.moveToFirst();
            if (cr1 != null && cr1.getCount() > 0) {
                badgeno = cr1.getString(0);
                childid = cr1.getString(1);
                isdataexists = true;
                cr1.close();
            }
            if (!badgeno.equalsIgnoreCase("0")) {
                badge = Integer.parseInt(badgeno) + 1;
                //allbadge = String.valueOf(Integer.parseInt(allbadge) + 1);
            } else {
                Log.d(TAG, "badge value : " + badge);
                badge++;
            }
        }

        String allbadgqr = "select AllBadge from " + CHAT_MESSAGE_BADGE + " where " + User_id + "=" + '"' + childid + '"';
        Cursor crab = db.rawQuery(allbadgqr, null);

        crab.moveToFirst();
        if (crab != null && crab.getCount() > 0) {
            allbadge = crab.getString(0);
        }
        allbadge = String.valueOf(Integer.parseInt(allbadge) + 1);
        ContentValues cvr = new ContentValues();
        cvr.put(AllBadge, allbadge);

        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sd.format(Calendar.getInstance().getTime());

        ContentValues cv = new ContentValues();
        cv.put(Sender_jid, newMessage.receiver_id_jid);
        cv.put(Receiver_jid, newMessage.sender_id_jid);
        cv.put(Message, newMessage.message);
        cv.put(User_id, childid);
        cv.put(Messageid, newMessage.message_id);
        cv.put(Badge, badge + "");
        cv.put(AllBadge, allbadge);
        cv.put(Created, time);
        if (!isdataexists) {
            db.insert(CHAT_MESSAGE_BADGE, null, cv);
        } else
            db.update(CHAT_MESSAGE_BADGE, cv, Sender_jid + "=" + '"' + newMessage.receiver_id_jid + '"' + " AND " + Receiver_jid + "=" + '"' + newMessage.sender_id_jid + '"', null);

        db.update(CHAT_MESSAGE_BADGE, cvr, User_id + "=" + '"' + childid + '"', null);
    }


    public void clearchatbadge(String childid, String jid) {
        String query = "select Badge from " + CHAT_MESSAGE_BADGE + " where " + User_id + "=" + "'" + childid + "' AND " + Receiver_jid + "=" + "'" + jid + "'";
        Cursor cr = db.rawQuery(query, null);
        //cr.moveToFirst();
        if (cr != null && cr.getCount() > 0) {
            ContentValues cv = new ContentValues();
            cv.put(Badge, "0");
            db.update(CHAT_MESSAGE_BADGE, cv, User_id + "=" + "'" + childid + "' AND " + Receiver_jid + "=" + "'" + jid + "'", null);
        }
    }

    public void clearallchatbadge(String childid) {
        String query = "select Badge from " + CHAT_MESSAGE_BADGE + " where " + User_id + "=" + "'" + childid + "'";
        Cursor cr = db.rawQuery(query, null);

        cr.moveToFirst();
        if (cr != null && cr.getCount() > 0) {
            ContentValues cv = new ContentValues();
            cv.put(AllBadge, "0");
            db.update(CHAT_MESSAGE_BADGE, cv, User_id + "=" + "'" + childid + "'", null);
        }
    }

    public String getmessagestatus(String packetID) {

        String status = "Sent";
        String query = "select count(Message_id) from " + MESSAGE_DELIVERY_STATUS + " where " + MESSAGE_ID + "=" + "'" + packetID + "'";
        Cursor cc = db.rawQuery(query, null);
        cc.moveToFirst();
        if (cc != null && cc.getCount() > 0) {
            status = "Received";
        }

        return status;
    }

    public void updateBadge(Childbeans newMessage, String childid) {
        boolean isdataexists = false;
        String badgeno = "0", allbadge = "0";
        int badge = 0;

        String query1 = "select User_id,Badge,AllBadge from " + CHAT_MESSAGE_BADGE + " where " + Sender_jid + "=" + '"' + newMessage.receiver_id_jid +
                '"' + " AND " + Receiver_jid + "=" + '"' + newMessage.sender_id_jid + '"';
        Cursor cr1 = db.rawQuery(query1, null);

        cr1.moveToFirst();
        if (cr1 != null && cr1.getCount() > 0) {
            childid = cr1.getString(0);
            badgeno = cr1.getString(1);
            allbadge = cr1.getString(2);

            isdataexists = true;
            cr1.close();
        }
        if (!badgeno.equalsIgnoreCase("0")) {
            badge = Integer.parseInt(badgeno) + 1;
            //allbadge = String.valueOf(Integer.parseInt(allbadge) + 1);
        } else {
            Log.d(TAG, "badge value : " + badge);
            badge++;
        }

        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sd.format(Calendar.getInstance().getTime());
        ContentValues cv = new ContentValues();
        cv.put(Sender_jid, newMessage.receiver_id_jid);
        cv.put(Receiver_jid, newMessage.sender_id_jid);
        cv.put(Message, newMessage.message);
        cv.put(User_id, childid);
        cv.put(Messageid, newMessage.message_id);
        cv.put(Badge, badge + "");
        cv.put(AllBadge, allbadge);
        cv.put(Created, time);
        if (!isdataexists) {
            db.insert(CHAT_MESSAGE_BADGE, null, cv);
        } else
            db.update(CHAT_MESSAGE_BADGE, cv, Sender_jid + "=" + '"' + newMessage.receiver_id_jid + '"' + " AND " + Receiver_jid + "=" + '"' + newMessage.sender_id_jid + '"', null);
    }

}
