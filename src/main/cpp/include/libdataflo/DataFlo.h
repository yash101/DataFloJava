#pragma once

#include "IDataPlane.h"
#include <memory>

namespace tb
{
  class IResolver;

  class DataFlo
  {
  private:
    std::shared_ptr<IDataPlane> m_defaultDataPlane;
    std::shared_ptr<IResolver> m_resolver;

  public:
    // Definitions:
    // Streams: 32 bit stream IDs
    // Clients: 32 bit client IDs
    // Services: 32 bit service IDs
  };
}
