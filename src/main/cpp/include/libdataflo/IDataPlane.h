#ifndef _DF_LIBDATAFLO_IDATAPLANE_H
#define _DF_LIBDATAFLO_IDATAPLANE_H

#include <memory>
#include <stdint.h>
#include <future>

namespace tb
{
  class IClient;
  class IProvider;
  class DataQueryRequest;
  class DataQueryResponse;
  class DataSubscriptionRequest;
  class StreamStartedResponse;
  class Data;

  class Service { };

  /**
   * IDataPlane
   * 
   * Routes data between producers, consumers, other IDataPlane instances, and dataflo
   */
  class IDataPlane : public Service
  {
    void mountService(std::shared_ptr<Service> service);
    void unmountService(std::shared_ptr<Service> service);

    std::future<void> doServiceQuery(std::shared_ptr<void>, DataQueryRequest& request);

    void clientResolve(std::shared_ptr<IClient> client, DataQueryRequest& request);
    void clientSubscribe(std::shared_ptr<IClient> client, DataSubscriptionRequest& request);

    void providerResolve(uint32_t providerID, DataQueryRequest& request);
    void providerPostResolveResponse(std::shared_ptr<IProvider> provider, DataQueryResponse& response);

    void providerRequestStreamStart(uint32_t providerID, uint32_t streamID, DataSubscriptionRequest& request); // then: provider.requestStreamStart(...) or pass down
    void providerPostStreamStarted(std::shared_ptr<IProvider> provider, StreamStartedResponse& response); // then: parent.providerPostStreamStarted(...)

    void providerRequestStreamStop(uint32_t providerID, uint32_t streamID); // then: provider.requestStreamStop(...) or pass down

    void mapRoute(uint32_t streamID, uint32_t sourceID, uint32_t destID);   // then: parent.dataPlaneMapComplete(uint32_t streamID, uint32_t sourceId, uint32_t destID)
    void unmapRoute(uint32_t streamID, uint32_t sourceID, uint32_t destID); // then: parent.dataPlaneUnmapComplete(uint32_t streamID, uint32_t soruceID, uint32_t destID)

    void setRouteCharacteristics(uint32_t streamID, uint32_t flags);

    void publish(uint32_t streamID, uint32_t sourceID, Data& data);
  };
}

#endif
