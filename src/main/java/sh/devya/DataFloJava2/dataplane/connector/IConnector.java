package sh.devya.DataFloJava2.dataplane.connector;

import java.util.concurrent.Future;

public interface IConnector {
  Future<?> bind(String connectionString);
  void shutdown();
  void shutdownUngraceful();
  boolean isConnected();
}
