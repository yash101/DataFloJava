#pragma once

#include <exception>
#include <functional>

namespace df
{
  template <typename T>
  class Promise
  {
    bool isExceptional;
    std::exception exception;
    T value;

    Promise() :
      isExceptional(false)
    { }

    inline T& get()
    {
      return value;
    }
  };

  template <typename F, typename T>
  class Future
  {
    Promise<T> promise;

  private:
    inline Future(F&& function)
    {
    }

  public:
    inline Future<T> future(F&& function)
    {
      function(promise);
    }

    inline void onError(std::exception& e)
    {
    }

    inline Future<G, U> map(F&& function)
    {
    }
  };
}