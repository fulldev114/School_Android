package com.common.view;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by etm-11 on 25/5/16.
 */

public class AttachmentMap implements Serializable {
    String attachmentId, refMapId, attachmentStatus, attachmentName, mapName, syncStatus, serverWorkSheetId;
    Bitmap bitmap;


    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public String getAttachmentStatus() {
        return attachmentStatus;
    }

    public void setAttachmentStatus(String attachmentStatus) {
        this.attachmentStatus = attachmentStatus;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getRefMapId() {
        return refMapId;
    }

    public void setRefMapId(String refMapId) {
        this.refMapId = refMapId;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getServerWorkSheetId() {
        return serverWorkSheetId;
    }

    public void setServerWorkSheetId(String serverWorkSheetId) {
        this.serverWorkSheetId = serverWorkSheetId;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}

