package com.aixiaoqi.socket;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/4 0004.
 */
public class SendYiZhengService implements TlvAnalyticalUtils.SendToSdkLisener{
    ReceiveSocketService mReceiveSocketService;

    public   void sendGoip(String header){
        //TODO 根据消息头组装数据发送到一正服务器
        sendService(header);
    }
    public  void initSocket(ReceiveSocketService receiveSocketService){
        //TODO 初始化TCP socket
        if(TlvAnalyticalUtils.sendToSdkLisener==null){
            TlvAnalyticalUtils.setListener(this);
        }
        mReceiveSocketService=receiveSocketService;
        receiveSocketService.initSocket();
    }
    public  int count=0 ;
    public  void sendService(String header){
        String number=Integer.toHexString(count+1);
        count=count+1;
        if(number.length()%4==1){
            number="000"+number;
        }else if(number.length()%4==2){
            number="00"+number;
        }else if(number.length()%4==3){
            number="0"+number;
        }
        Log.e("C_TAG","number="+number);
        List<TlvEntity> yiZhengTlvList=new ArrayList<>();
        if(Contant.CONNECTION.equals(header))
            connection(yiZhengTlvList);
        else if(Contant.PRE_DATA.equals(header)){
            sdkReturn(yiZhengTlvList);
        }else if(Contant.UPDATE_CONNECTION.equals(header)){
            updateConnection(yiZhengTlvList);
        }
        MessagePackageEntity messagePackageEntity =new MessagePackageEntity(Contant.SESSION_ID,number,header,yiZhengTlvList);
        String str=messagePackageEntity.combinationPackage();
        mReceiveSocketService.sendMessage(str);
    }

    private   void sdkReturn(List<TlvEntity> yiZhengTlvList) {
        TlvEntity yiZhengTlv=new TlvEntity("01","00");
        TlvEntity yiZhengTlv1=new TlvEntity(Contant.SDK_TAG,Contant.SDK_VALUE);
        yiZhengTlvList.add(yiZhengTlv);
        yiZhengTlvList.add(yiZhengTlv1);
    }

    private   void updateConnection(List<TlvEntity> yiZhengTlvList) {
        TlvEntity yiZhengTlv=new TlvEntity("01","00");
        TlvEntity yiZhengTlv1=new TlvEntity("101","b4");
        yiZhengTlvList.add(yiZhengTlv);
        yiZhengTlvList.add(yiZhengTlv1);
    }

    private   void connection(List<TlvEntity> yiZhengTlvList) {
        for(int i=0;i<Contant.CONNENCT_TAG.length;i++){
            TlvEntity yiZhengTlv=new TlvEntity(Contant.CONNENCT_TAG[i],Contant.CONNENCT_VALUE[i]);
            yiZhengTlvList.add(yiZhengTlv);
            Log.e("connection","Tag"+Contant.CONNENCT_TAG[i]+"\nvalue="+Contant.CONNENCT_VALUE[i]);
        }
    }


    @Override
    public void send(byte evnindex,int length, byte[] bytes) {
        //TODO 发送健全数据到SDK
        JNIUtil.getInstance().simComEvtApp2Drv((byte)0,evnindex,length,bytes);
    }

    @Override
    public void sendServer(String hexString) {
        //TODO 把关于是010101的值修改发送到一正服务器
        mReceiveSocketService.sendMessage(hexString);
    }



}
