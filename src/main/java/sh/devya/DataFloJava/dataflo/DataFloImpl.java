package sh.devya.DataFloJava.dataflo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

import sh.devya.DataFloJava.client.IClient;
import sh.devya.DataFloJava.client.IConsumer;
import sh.devya.DataFloJava.messages.SubscriptionRequest;
import sh.devya.DataFloJava.provider.IProvider;
import sh.devya.DataFloJava.resolver.IResolver;

/**
 * Basic DataFlo impleementation.
 * 
 * DataFlo is an event router. It's job is to forward messages from a provider to a consumer.
 * To create the routes that messages flow through, DataFlo calls 
 */
public class DataFloImpl implements IDataFlo {
  private IResolver resolver = null;

  // List of streams, the index is the stream ID
  private List<Stream> streams = new ArrayList<>();
  private Set<Integer> unusedStreamIds = new HashSet<Integer>();

  private Map<IClient, Set<IConsumer>> consumersOwnedByClient = new HashMap<>();
  private Map<IConsumer, Stream> streamConsumedByConsumer = new HashMap<>();

  private Map<IProvider, Set<Stream>> streamHandledByProducer = new HashMap<>();

  DataFloImpl(IResolver resolver)
  {
    Objects.requireNonNull(resolver);
    this.resolver = resolver;
    this.resolver.initialize(this);
  }

  /**
   * We don't really need to keep track of clients normally
   * 
   * @param client the client connecting to this DataFlo
   */
  @Override
  public void clientNew(IClient client) throws IllegalStateException {
    Objects.requireNonNull(client);
    
    if (consumersOwnedByClient.containsKey(client)) {
      throw new IllegalStateException("client already exists in DataFlo");
    }

    consumersOwnedByClient.put(client, new CopyOnWriteArraySet<>());
  }

  /**
   * Close the client and GC all of its resources
   * 
   * @throws IllegalStateException when client does not exist
   */
  @Override
  public void clientClose(IClient client) throws IllegalStateException {
    try {
      consumersOwnedByClient
        .get(client)
        .stream()
        .forEach(consumer -> {
          clientUnsubscribe(consumer);
        });
    } catch (NullPointerException e) {
      throw new IllegalStateException(e.getCause());
    }
  }

  /**
   * Steps:
   * 1. Ask resovler to request a stream ID
   */
  @Override
  public CompletableFuture<IConsumer> clientSubscribe(
      IClient client, SubscriptionRequest request) {

    return resolver
      .trySubscribeClient(request)
      .thenApply(streamId -> {
        if (streamId >= streams.size()) {
          throw new IllegalStateException("Invalid stream ID was provided by the resolver");
        }

        IConsumer consumer = client.getConsumerInstance(request, streamId);
        Stream stream = new Stream(request.topic(), request.headers());
        stream.subscribe(consumer);

        streams.set(streamId, stream);
        streamConsumedByConsumer.put(consumer, stream);

        // Create the new Stream and return a consumer
        return consumer;
      });
  }

  @Override
  public void clientUnsubscribe(IConsumer consumer) {
    // figure out which stream is using a consumer
    Stream streamInUse = streamConsumedByConsumer.get(consumer);
    streamInUse.unsubscribe(consumer);

    if (streamInUse.getNumberOfConsumers() == 0) {
      resolver.tryReleaseStream(streamInUse);
    }
  }
 
  /**
   * Check if there are any unused stream IDs available in the unusedStreamIds set otherwise allocate another
   */
  public int getNewStreamId() {
    return unusedStreamIds
      .stream()
      .findFirst()
      .map(id -> {
        unusedStreamIds.remove(id);
        return id;
      })
      .orElseGet(() -> {
        streams.add(null);
        return streams.size();
      });
  }

  public void recycleStreamId(int streamId) {
    streams.set(streamId, null);
    unusedStreamIds.add(streamId);

    // GC
    int numNull = 0;
    for (int i = streams.size() - 1; i >= 0; i--) {
      if (Objects.nonNull(streams.get(i))) {
        break;
      }
      unusedStreamIds.remove(i);
      numNull++;
    }

    streams
      .subList(streams.size() - numNull, streams.size())
      .clear();
  }

  /**
   * Add a new provider to the DataFlo instance
   * 
   * @param provider is the interface implementation for the provider
   */
  @Override
  public void providerNew(IProvider provider) {
    Objects.requireNonNull(provider);
    if (streamHandledByProducer.containsKey(provider)) {
      throw new IllegalStateException("Provider already exists in this DataFlo instance");
    }

    streamHandledByProducer.put(provider, new HashSet<>());
  }

  @Override
  public void providerClose(IProvider provider) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'sorClose'");
  }
}
