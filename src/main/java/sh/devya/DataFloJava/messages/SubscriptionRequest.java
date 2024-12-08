package sh.devya.DataFloJava.messages;

import java.util.Map;

public record SubscriptionRequest(String topic, Map<String, String> headers) {
}
