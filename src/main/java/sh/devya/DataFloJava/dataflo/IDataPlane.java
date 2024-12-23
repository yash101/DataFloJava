/*
 * IDataPlane.java
 * 
 * IDataPlane is an interface which describes a dataplane.
 * Traffic flows through a dataplane. DataFlo, along with the resolver create the control plane
 */

package sh.devya.DataFloJava.dataflo;

import java.util.Map;

public interface IDataPlane {
  // We need to keep a routing table:
  // List<Producer<StreamID>> -> List<Consumer<StreamID>>
  // Probably implemented as multiple maps:

  Map<Object, Integer> mapProducerToStreamId;
  Map<Integer, Object> mapStreamIdToConsumer;
  List<Stream> mapStreamIdToConsumers;
  
  
  // IDEA:
  // Stream can contain QoS information: load balancing, reliability
}
