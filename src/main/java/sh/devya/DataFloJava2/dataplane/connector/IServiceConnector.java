package sh.devya.DataFloJava2.dataplane.connector;

import java.util.function.Consumer;

import sh.devya.DataFloJava2.message.Message;

public interface IServiceConnector extends IConnector {
  void publish(Message message);
  void publish_error(ErrorMessage message);

  /**
   * Shut down this connector gracefully
   */
  void shutdown();

  void onError(Consumer<?> callback);
}
