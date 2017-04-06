package com.rosa.swift.core.network.json.sap.common;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.network.json.sap.security.SecurityTask;

import java.util.ArrayList;
import java.util.List;

public class DriverSessionResponse {

    @NonNull
    @SerializedName("QUEST")
    private List<Quest> mQuests = new ArrayList<>();

    @SerializedName("ANS")
    private List<Answer> mAnswers = new ArrayList<>();

    @NonNull
    @SerializedName("REASON")
    private List<RejectionReason> mReasons = new ArrayList<>();

    @NonNull
    @SerializedName("MSGS")
    private List<DriverMessage> mMessages = new ArrayList<>();

    @NonNull
    @SerializedName("CUP_VIEWS_INFO")
    private List<PhotoSessionTask> mPhotoSessionTasks = new ArrayList<>();

    @NonNull
    @SerializedName("PHOTO_DOC_INFO")
    private List<PhotoDocumentTask> mPhotoDocumentTasks = new ArrayList<>();

    @NonNull
    @SerializedName("SECURITY_TASKS")
    private List<SecurityTask> mSecurityTasks = new ArrayList<>();

    @SerializedName("GATE_KEEPER_ACTIVE")
    private String mGateKeeperActive;

    @SerializedName("CUP_WAS_DELAYED")
    private String mPhotoSessionWasDelayed;

    @SerializedName("CUP_DELAY_DAYS")
    private int mPhotoSessionDaysToDelay;

    @SerializedName("IDLE_TIME")
    private int mIdleTime;

    @NonNull
    public List<Quest> getQuests() {
        return mQuests;
    }

    public List<Answer> getAnswers() {
        return mAnswers;
    }

    @NonNull
    public List<RejectionReason> getRejectionReasons() {
        return mReasons;
    }

    @NonNull
    public List<DriverMessage> getDriverMessages() {
        return mMessages;
    }

    @NonNull
    public List<PhotoSessionTask> getPhotoSessionTasks() {
        return mPhotoSessionTasks;
    }

    @NonNull
    public List<PhotoDocumentTask> getPhotoDocumentTasks() {
        return mPhotoDocumentTasks;
    }

    @NonNull
    public List<SecurityTask> getSecurityTasks() {
        return mSecurityTasks;
    }

    public String isCupWasDelayed() {
        return mPhotoSessionWasDelayed;
    }

    public int getCupDaysToDelay() {
        return mPhotoSessionDaysToDelay;
    }

    public String isGateKeeperActive() {
        return mGateKeeperActive;
    }

    public int getIdleTime() {
        return mIdleTime;
    }

    public static class Quest {

        @SerializedName("ID")
        public int id;

        @SerializedName("TEXT")
        public String text;

        @SerializedName("IS_REQUIERED")
        public String is_req;

        @SerializedName("BY_LGORT")
        public String by_lgort;

        public int getId() {
            return id;
        }

        public String getText() {
            return text;
        }

        public String isRequired() {
            return is_req;
        }

        public String getWarehouse() {
            return by_lgort;
        }

    }

    public static class Answer {

        @SerializedName("QID")
        public int qid;

        @SerializedName("AID")
        public int aid;

        @SerializedName("TEXT")
        public String text;

        public int getQuestId() {
            return qid;
        }

        public int getAnswerId() {
            return aid;
        }

        public String getText() {
            return text;
        }

        @Override
        public String toString() {
            return text != null ? text : "";
        }
    }

    public static class RejectionReason {

        @SerializedName("R_CODE")
        private String mCode;

        @SerializedName("R_TEXT")
        private String mText;

        @Override
        public String toString() {
            return mText;
        }

        public String getCode() {
            return mCode;
        }

        public String getText() {
            return mText;
        }

    }

    public static class DriverMessage {

        @SerializedName("ID")
        private String mId;

        @SerializedName("TEXT")
        private String mText;

        public String getId() {
            return mId;
        }

        public String getText() {
            return mText;
        }

        @Override
        public String toString() {
            return mText;
        }

    }

    public static class PhotoSessionTask {

        @SerializedName("CUP_VIEW_ID")
        public String cup_view_id;

        @SerializedName("CUP_VIEW_TITLE")
        public String cup_view_title;

        @SerializedName("CUP_VIEW_ORDER")
        public String cup_view_order;

        @SerializedName("CUP_VIEW_PHOTO_DOCID")
        public String view_photo_docid;

        public String getViewId() {
            return cup_view_id;
        }

        public String getViewTitle() {
            return cup_view_title;
        }

        public String getViewOrder() {
            return cup_view_order;
        }

        public String getPhotoDocumentId() {
            return view_photo_docid;
        }

    }

    public static class PhotoDocumentTask {

        @SerializedName("PHOTO_DOC_VIEW")
        private String mPhotoDocumentType;

        @SerializedName("PHOTO_FILENAME")
        private String mPhotoFileName;

        @SerializedName("PHOTO_DATE")
        private String mPhotoDate;

        @SerializedName("PHOTO_ACT_DATE")
        private String mPhotoActualDate;

        @SerializedName("SEND_TASK")
        private String mSendTask;

        public String getPhotoDocumentType() {
            return mPhotoDocumentType;
        }

        public String getPhotoFileName() {
            return mPhotoFileName;
        }

        public String getPhotoDate() {
            return mPhotoDate;
        }

        public String getPhotoActualDate() {
            return mPhotoActualDate;
        }

        public String getSendTask() {
            return mSendTask;
        }

    }

}
