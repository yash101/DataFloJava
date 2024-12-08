package sh.devya.DataFloJava.client;

import java.util.Objects;

import sh.devya.DataFloJava.dataflo.IDataFlo;

public class BasicClient implements IClient {
  protected IDataFlo dataFlo;
  /**
   * "Connects" to a dataflo instance
   * 
   * @param dataflo dataflo instance
   */
  @Override
  public void connect(IDataFlo dataflo)
  {
    if (Objects.nonNull(dataflo)) {
      throw new IllegalStateException("Client is already initialized with a DataFlo");
    }

    dataFlo  = dataflo;
  }

  @Override
  public final void close() {
    if (Objects.isNull(dataFlo)) {
      throw new IllegalStateException("Not currently initialized.");
    }

    dataFlo.closeClient(this);
    dataFlo = null;
  }

  protected final void subscribe(Object subscrptionRequest) {
  }
}
