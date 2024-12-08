package sh.devya.DataFloJava.client;

import sh.devya.DataFloJava.messages.DataMessage;

public interface IConsumer {
  void handleMessage(DataMessage message);
  void handleError(Object error);
}
