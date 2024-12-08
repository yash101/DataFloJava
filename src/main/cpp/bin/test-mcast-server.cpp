#ifndef _WIN32
  #include <sys/types.h>
  #include <sys/socket.h>
  #include <netinet/in.h>
  #include <netinet/ip.h>
  #include <netinet/ip6.h>
  #include <arpa/inet.h>
  #include <time.h>
  #include <unistd.h>
#else
  #include <Winsock2.h>
  #include <Ws2tcpip.h>
  #include <Windows.h>

  void init_winsock()
  {
    static WSADATA wsaData;
    if (WSAStartup(0x0101, &wsaData))
    {
      cerr << "WSAStartup failed" << endl;
      return -1;
    }
  }
#endif

#include <string>
#include <iostream>
#include <memory>

using namespace std;

int main(int argc, char** argv)
{
#ifdef _WIN32
  if (init_winsock())
    return 1;
#endif

  if (argc < 3)
  {
    cerr << "Usage: \"unicast group\" port" << endl;
    return 1;
  }

  string group = string(argv[1]);
  short port = atoi(argv[2]);

  int fd = socket(AF_INET, SOCK_DGRAM, 0);
  if (fd < 0)
  {
    cerr << "Socket creation failure" << endl;
    return 1;
  }

  static volatile u_int yes = 1;
  if (setsockopt(fd, SOL_SOCKET, SO_REUSEADDR, (const void*) &yes, sizeof(yes)) < 0)
  {
    cerr << "Failed to set SO_REUSEADDR on socket" << endl;
  }

  struct sockaddr_in listen_addr;
  memset(&listen_addr, 0, sizeof(listen_addr));
  listen_addr.sin_family = AF_INET;
  listen_addr.sin_port = port;

  struct ip_mreq mreq;
  mreq.imr_multiaddr.s_addr = inet_addr(group.c_str());

  if (argc >= 4)
  {
    string listen_ip = string(argv[3]);
    inet_pton(AF_INET, listen_ip.c_str(), &listen_addr.sin_addr);
    mreq.imr_interface.s_addr = inet_addr(listen_ip.c_str());
  }
  else
  {
    listen_addr.sin_addr.s_addr = htonl(INADDR_ANY);
    mreq.imr_interface.s_addr = htonl(INADDR_ANY);
  }

  if (::bind(fd, (struct sockaddr*) &listen_addr, sizeof(listen_addr)) < 0)
  {
    cerr << "Failed to bind socket to listen address" << endl;
    return 1;
  }

  if (setsockopt(fd, IPPROTO_IP, IP_ADD_MEMBERSHIP, (const void*) &mreq, sizeof(mreq)) < 0) {
    cerr << "Failed to join the multicast group" << endl;
    return 1;
  }

  struct iovec iov;
  struct msghdr msg;
  struct sockaddr_in server_addr;
  struct sockaddr_in client_addr;

  size_t buffer_len = 65536;
  unique_ptr<char> buffer = unique_ptr<char>(new char[buffer_len]);

  size_t control_buffer_len = 1024;
  unique_ptr<char> control_buffer = unique_ptr<char>(new char[control_buffer_len]);

  if (!buffer || !control_buffer)
  {
    cerr << "Allocation failed for message buffer or control buffer" << endl;
    return 1;
  }

  while (true)
  {
    memset(&msg, 0, sizeof(msg));
    memset(buffer.get(), 0, buffer_len);
    memset(control_buffer.get(), 0, control_buffer_len);

    iov.iov_base = buffer.get();
    iov.iov_len = buffer_len;

    msg.msg_name = &client_addr;
    msg.msg_namelen = sizeof(client_addr);
    msg.msg_iov = &iov;
    msg.msg_iovlen = 1;
    msg.msg_control = control_buffer.get();
    msg.msg_controllen = control_buffer_len;

    ssize_t received_len = recvmsg(fd, &msg, 0);

    if (received_len < 0)
    {
      int err = errno;
      cerr << "Recvmessage failed: " << err << " " << strerror(err) << endl;
      continue;
    }

    if (msg.msg_flags & MSG_TRUNC)
    {
      cerr << "Warn: packet truncated; received length = " << received_len << endl;
    }

    int len = strnlen(buffer.get(), buffer_len);
    string data = string(buffer.get(), len);

    string pstr = "Received packet R="
      + std::to_string(received_len)
      + " L=" + to_string(data.size())
      + " F=" + string(inet_ntoa(client_addr.sin_addr)) + ":" + to_string(client_addr.sin_port)
      + " T=" + string(inet_ntoa(server_addr.sin_addr)) + ":" + to_string(server_addr.sin_port)
      + " S=\"" + data + "\"";
    
    cout << pstr << endl;
  }
}
