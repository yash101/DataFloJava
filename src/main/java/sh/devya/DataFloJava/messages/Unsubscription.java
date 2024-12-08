package sh.devya.DataFloJava.messages;

import java.util.Map;

public record Unsubscription(String topic, Map<String, String> headers) {
}
