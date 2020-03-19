package client;

import org.eclipse.paho.client.mqttv3.*;

import java.util.HashMap;
import java.util.Map;

public class SharedSubCallbackRouter implements MqttCallback {

    private Map<String, IMqttMessageListener> topicFilterListeners;

    public SharedSubCallbackRouter(Map<String, IMqttMessageListener> topicFilterListeners) {
        this.topicFilterListeners = topicFilterListeners;
    }


    public void addSubscriber(String topicFilter, IMqttMessageListener listener) {
        if (this.topicFilterListeners == null) {
            this.topicFilterListeners = new HashMap<>();
        }
        this.topicFilterListeners.put(topicFilter, listener);
    }


    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        for (Map.Entry<String, IMqttMessageListener> listenerEntry : topicFilterListeners.entrySet()) {
            String topicFilter = listenerEntry.getKey();
            if (isMatched(topicFilter, topic)) {
                listenerEntry.getValue().messageArrived(topic, message);
            }
        }

    }

    private boolean isMatched(String topicFilter, String topic) {
        if (topicFilter.startsWith("$queue/")) {
            topicFilter = topicFilter.replaceFirst("\\$queue/", "");
        } else if (topicFilter.startsWith("$share/")) {
            topicFilter = topicFilter.replaceFirst("\\$share/", "");
            topicFilter = topicFilter.substring(topicFilter.indexOf('/'));
        }
        return MqttTopic.isMatched(topicFilter, topic);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
