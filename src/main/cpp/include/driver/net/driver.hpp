#ifndef _DF_DRIVER_NET_DRIVER_HPP
#define _DF_DRIVER_NET_DRIVER_HPP

#include <stdint.h>

#ifdef __cplusplus
namespace df
{
#endif
  /**
   * Inventory of what is necessary:
   * 
   * Structure which maintains the state as necessary
   */

  typedef void (*UdpIncomingHandler)(struct UdpPacket* packet);

  /**
   * We will leave this undefined since each driver will define its own
   * This can be opaque.
   */
  typedef struct UdpSocket;

  /**
   * Allocate and create a new UdpSocket
   */
  typedef struct UdpSocket* udp_socket_new();

  void udp_packet_close(struct UdpPacket* packet);

  /**
   * Close a UdpSocket / free its resources
   */
  void udp_socket_close(struct UdpSocket* socket);

  /**
   * Bind the UdpSocket to a port
   */
  int udp_socket_bind(struct UdpSocket* socket, uint16_t port);

  /**
   * Set incoming packet handler
   */
  bool udp_socket_set_incoming_handler(struct UdpSocket* socket, UdpIncomingHandler handler);

  /**
   * Listen on a UDP socket (should be async or new thread)
   */
  int udp_socket_listen(struct UdpSocket* socket);

  int udp_socket_send(struct UdpSocket* socket, void* socket);

#ifdef __cplusplus
}
#endif

#endif
