package sh.devya.DataFloJava.dataflo;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import sh.devya.DataFloJava.client.IConsumer;
import sh.devya.DataFloJava.messages.DataMessage;
import sh.devya.DataFloJava.provider.IProvider;

public class Stream implements AutoCloseable {
  private final ReadWriteLock rwLock;
  private final String topic;
  private final Map<String, String> headers;      // maybe replace these objects with a stream request instead

  private IProvider provider;
  private final Set<IConsumer> consumers;

  Stream(String topic, Map<String, String> headers) {
    this.rwLock = new ReentrantReadWriteLock();
    this.topic = topic;
    this.headers = headers;
    this.consumers = new HashSet<>();

    this.provider = null;
  }

  public String getTopic() {
    return topic;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public int getNumberOfConsumers() {
    return consumers.size();
  }

  public void subscribe(IConsumer consumer) {
    try {
      rwLock.writeLock().lock();
      consumers.add(consumer);                    // No need to check first since the set does that internally
    } finally {
      rwLock.writeLock().unlock();
    }
  }

  public void unsubscribe(IConsumer consumer) {
    try {
      rwLock.writeLock().lock();
      consumers.remove(consumer);
    } finally {
      rwLock.writeLock().unlock();
    }
  }

  public void setProvider(IProvider provider) {
    try {
      rwLock.writeLock().lock();
      this.provider = provider;
    } finally {
      rwLock.writeLock().unlock();
    }
  }

  public void removeProvider(Object reason) {
    try {
      rwLock.writeLock().lock();
      provider = null;
    } finally {
      rwLock.writeLock().unlock();
    }
  }

  public int handleMessage(DataMessage message) {
    int nCons = 0;
    try {
      rwLock.readLock().lock();
      consumers
        .stream()
        .parallel()
        .forEach(consumer -> {
          try {
            consumer.handleMessage(message);
          } catch (Exception e) {
            // Ignore exceptions otherwise message will fail to deliver to other consumers
          }
        });
      nCons = consumers.size();
    } finally {
      rwLock.readLock().unlock();
    }
    return nCons;
  }

  // TODO: define error object type
  public int handleError(Object error) {
    int nCons = 0;
    try {
      rwLock.readLock().lock();
      consumers
        .stream()
        .parallel()
        .forEach(consumer -> {
          try {
            consumer.handleError(error);
          } catch (Exception e) {
            // Ignore exceptions otherwise message will fail to deliver to other consumers
          }
        });
      nCons = consumers.size();
    } finally {
      rwLock.readLock().unlock();
    }

    return nCons;
  }

  @Override
  public void close() throws Exception {
    // TODO Auto-generated method stub
  }
}
