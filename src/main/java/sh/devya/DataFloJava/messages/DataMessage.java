package sh.devya.DataFloJava.messages;

import java.util.Map;

public record DataMessage(
  String topic,
  Map<String, String> headers,
  Object data
) {
}
