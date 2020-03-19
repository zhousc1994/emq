package client;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        try {
            MqttClient mqttClient = new MqttClient("tcp://192.168.3.235:1883", "7b7bd2cf-f0e0-4c99-b8b4-f401391ab6c4");
            System.out.println("Main");
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
