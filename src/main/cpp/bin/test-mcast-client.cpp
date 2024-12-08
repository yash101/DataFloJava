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
#include <chrono>
#include <thread>

using namespace std;

int main(int argc, char** argv)
{
#ifdef _WIN32
  init_winsock();
#endif

  if (argc < 3)
  {
    cerr << "Usage: \"group\" port" << endl;
  }

  string group = string(argv[1]);
  short port = atoi(argv[2]);

  int fd = socket(AF_INET, SOCK_DGRAM, 0);
  if (fd < 0)
  {
    cerr << "Socket creation failure" << endl;
    return 1;
  }

  struct sockaddr_in addr;
  memset(&addr, 0, sizeof(addr));
  addr.sin_family = AF_INET;
  addr.sin_addr.s_addr = inet_addr(group.c_str());
  addr.sin_port = port;

  while (true)
  {
    cout << "Data to send? " << endl << "> ";
    cout.flush();
    string x;
    cin >> x;
    cout << "Sending data with " << x << endl;

    int sent = sendto(fd, x.c_str(), x.size() + 1, 0, (struct sockaddr*) &addr, sizeof(addr));

    if (sent < 0)
    {
      int err = errno;
      cerr << "Error sending data: " << err << " : " << strerror(err) << endl;
      return 1;
    }

    std::this_thread::sleep_for(std::chrono::seconds(1));
  }
}
