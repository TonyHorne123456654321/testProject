package com.aixiaoqi.socket;

import org.greenrobot.eventbus.EventBus;

import de.blinkt.openvpn.model.IsSuccessEntity;
import de.blinkt.openvpn.model.SimRegisterType;

/**
 * Created by Administrator on 2017/2/9 0009.
 */

public class EventBusUtil {

    public static void registerFail(int callbacktype,int regstatues) {
        IsSuccessEntity entity = new IsSuccessEntity();
        entity.setType(callbacktype);
        entity.setFailType(regstatues);
        entity.setSuccess(false);
        EventBus.getDefault().post(entity);
    }
    public static void simRegisterType(String registerType) {
        SimRegisterType entity = new SimRegisterType();
        entity.setSimRegisterType(registerType);
        EventBus.getDefault().post(entity);
    }
}
