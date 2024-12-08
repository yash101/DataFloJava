package sh.devya.DataFloJava.dataflo;

import java.util.concurrent.CompletableFuture;

import sh.devya.DataFloJava.client.IClient;
import sh.devya.DataFloJava.client.IConsumer;
import sh.devya.DataFloJava.messages.SubscriptionRequest;
import sh.devya.DataFloJava.provider.IProvider;

/**
 * IDataFlo is the interface for a DataFlo event router.
 * The idea is to loosely couple the event router so it could be provided over different interfaces (say network)
 * 
 * Current high level flow:
 * 
 * 
 */
public interface IDataFlo {
  /**
   * Add a new client to DataFlo
   * 
   * @param client
   */
  void clientNew(IClient client);

  /**
   * Remove a client from DataFlo
   * 
   * @param client
   */
  void clientClose(IClient client);

  /**
   * Subscribe a client to data
   * @param client client who wants data
   * @param request topic and headers for data routing
   */
  CompletableFuture<IConsumer> clientSubscribe(IClient client, SubscriptionRequest request);

  /**
   * Cancel a subscription to a client
   * 
   * @param consumer consumer who is receiving data
   */
  void clientUnsubscribe(IConsumer consumer);

  /**
   * Add a new data provider
   *
   * @param provider is the provider which will create Producer streams to push data
   */
  void providerNew(IProvider provider);

  /**
   * Close a data provider and remove it from DataFlo
   * 
   * @param provider is the provider which needs to be removed
   */
  void providerClose(IProvider provider);
}
