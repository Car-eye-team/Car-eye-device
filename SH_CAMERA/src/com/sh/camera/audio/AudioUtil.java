package com.sh.camera.audio;

import com.sh.camera.service.MainService;

public class AudioUtil {

    public static void startTalkBack() {
        MainService.getInstance().startTalkBack();
    }

}
