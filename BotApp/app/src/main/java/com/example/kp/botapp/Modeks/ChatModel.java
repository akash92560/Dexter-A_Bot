package com.example.kp.botapp.Modeks;

/**
 * Created by KP on 3/24/2018.
 */

public class ChatModel {

   public String message;
    public boolean isSend;
    public boolean voice;

    public ChatModel(String message, boolean isSend, boolean voice) {
        this.message = message;
        this.isSend = isSend;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

    public boolean isVoice() {
        return voice;
    }
}
