package sh.devya.DataFloJava.resolver;

import java.util.concurrent.CompletableFuture;

import sh.devya.DataFloJava.dataflo.DataFloImpl;
import sh.devya.DataFloJava.dataflo.Stream;
import sh.devya.DataFloJava.messages.SubscriptionRequest;

/**
 * The purpose of a resolver is to resolve routes.
 * Think of this as the control plane. The DataFlo is the data plane.
 * 
 * The goal is that the resolver should house the logic on handling general system
 * events. The goal of the DataFlo is to just complete the streams, passing the message
 * from producer to all subscribers.
 */
public interface IResolver {
  /**
   * Called by DataFlo with the IDataFlo instance for usage by the Resolver
   * 
   * @param dataflo IDataFlo implementation. This needs to be the implementation, not the interface.
   */
  void initialize(DataFloImpl dataflo);

  /**
   * Try to subscribe a client based off it's request parameters
   * 
   * @param request contains the details provided by a client to subscribe to data
   * @return a completable future with the stream ID or an exception
   */
  CompletableFuture<Integer> trySubscribeClient(SubscriptionRequest request);

  /**
   * Try to release a stream (since no one [probably] is subscribed)
   * 
   * @param request
   * @return
   */
  CompletableFuture<Void> tryReleaseStream(Stream stream);
}
