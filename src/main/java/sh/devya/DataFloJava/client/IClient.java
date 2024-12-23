package sh.devya.DataFloJava.client;

import sh.devya.DataFloJava.dataflo.IDataFlo;
import sh.devya.DataFloJava.messages.SubscriptionRequest;

public interface IClient {
  IConsumer getConsumerInstance(SubscriptionRequest request, int streamId);
  void connect(IDataFlo dataflo);
  void close();
}
