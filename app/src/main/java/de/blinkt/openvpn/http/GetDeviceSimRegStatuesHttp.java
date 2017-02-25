package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.BindCardModel;
import de.blinkt.openvpn.model.SimRegStatue;

/**
 * Created by Administrator on 2017/2/9 0009.
 */

public class GetDeviceSimRegStatuesHttp extends BaseHttp {
    public SimRegStatue getSimRegStatue() {
        return simRegStatue;
    }

    private  SimRegStatue simRegStatue;
    public  GetDeviceSimRegStatuesHttp(InterfaceCallback interfaceCallback,int cmdType_){
      super(interfaceCallback,cmdType_);
        isCreateHashMap=false;
    }
    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        slaverDomain_ = HttpConfigUrl.GET_DEVICE_SIM_REG_STATUES;
        sendMethod_ = GET_MODE;
    }

    @Override
    protected void parseObject(String response) {
        super.parseObject(response);
        simRegStatue = new Gson().fromJson(response,SimRegStatue.class);
    }
}
