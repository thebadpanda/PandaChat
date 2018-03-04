package com.example.arsenko.chatonfirebase;

public class ChatMessage extends AbstractChat{

    private String mMessage;
    private String mName;
    private String mUid;
//    private long mTime;


     ChatMessage(String message, String name, String uid){
        this.mMessage = message;
        this.mName = name;
//        this.mTime = time;
        this.mUid = uid;
//        time = new Date().getTime();
    }

    public ChatMessage(){
        // Must be for Chat work !
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public String getName() {
        return mName;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String mUid) {
        this.mUid = mUid;
    }

    public void setName(String name) {

        this.mName = name;
    }

//    long getTime() {
//        return mTime;
//    }
//
//    public void setTime(long time) {
//        this.mTime = time;
//    }

    @Override
    public int hashCode() {
         int result = mName == null ? 0 : mName.hashCode();
         result = 31 * result + (mMessage == null ? 0 : mMessage.hashCode());
         result = 31 * result + mUid.hashCode();
         return result;
    }

    @Override
    public boolean equals(Object object) {
        if(this == object) return true;
        if(object == null || getClass() != object.getClass()) return false;

        ChatMessage chatMessage = (ChatMessage) object;

        return mUid.equals(chatMessage.mUid)
                && (mName == null ? chatMessage.mName == null : mName.equals(chatMessage.mName))
                && (mMessage == null ? chatMessage.mMessage == null : mMessage.equals(chatMessage.mMessage));
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "mName='" + mName + '\'' +
                ", mMessage='" + mMessage + '\'' +
                "mUid='" + mUid + '\'' +
                '}';
    }
}
