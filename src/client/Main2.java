package client;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.HashMap;
import java.util.Map;

public class Main2 {

    public static void main(String[] args) {
        try {
            MqttClient mqttClient = new MqttClient("tcp://192.168.3.235:1883", "bf6a4a12-a4b7-4874-b457-09c2cc8259a2");
            System.out.println("Main2");
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
