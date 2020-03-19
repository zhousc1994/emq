package client;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.HashMap;
import java.util.Map;

public class Main1 {

    public static void main(String[] args) {
        try {
            MqttClient mqttClient = new MqttClient("tcp://192.168.3.235:1883", "3d9a5bab-8f6f-4de3-9bf0-bc989a8edcfe");
            System.out.println("Main1");
            mqttClient.connect();
            Map<String, IMqttMessageListener> listeners = new HashMap<>();
            IMqttMessageListener emqListener = new EmqListener();
            listeners.put("$queue/testmqtt", emqListener);
            mqttClient.setCallback(new SharedSubCallbackRouter(listeners));
            mqttClient.subscribe("$queue/testmqtt", new EmqListener());
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
}
