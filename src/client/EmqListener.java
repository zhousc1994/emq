package client;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

//实现IMqttMessageListener接口的Emq消息处理类
public class EmqListener implements IMqttMessageListener {

    public void messageArrived(String topic, MqttMessage message) throws Exception {
        try {
            System.out.println("topic: " + topic +" message:"+message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
