#ifndef _DF_DRIVER_NET_DFTLS_AEAD_STRUCTURE_HPP
#define _DF_DRIVER_NET_DFTLS_AEAD_STRUCTURE_HPP

#include <stdint.h>

namespace df
{

#if defined(__GNUC__) || defined(__clang__)
#define DFTLS_PACKED __attribute__((packed))
#elif defined(_MSC_VER)
#define DFTLS_PACKED __pragma(pack(push, 1))
#else
#error "Unsupported compiler. Define packing macros for your compiler."
#endif

  template <int ASYMMETRIC_SIZE = 64, int MAC_SIZE = 32>
  struct DFTLS_PACKED DFTLS_Packet
  {
    uint8_t content_type;           // 0x00: DTLS type (e.g., handshake, data).
    uint8_t version;                // 0x01: DTLS version (e.g., 0xFEFD for 1.2).
    uint16_t epoch;                 // 0x02–0x03: DTLS epoch (big-endian).
    uint8_t flags;                  // 0x04: DF flags (protocol-specific).
    uint8_t aead_config;            // 0x05: AEAD configuration (e.g., cipher suite).
    uint8_t stream_id[4];           // 0x06–0x09: Plaintext stream ID for multiplexing.
    uint32_t message_number;        // 0x0A–0x0D: Incrementing message number (big-endian).
    uint16_t fragment_number;       // 0x0E–0x0F: Incrementing fragment number (big-endian).
    uint16_t payload_length;        // 0x10–0x11: Length of the payload (big-endian).

    // MAC or signature fields (asymmetric block).
    uint8_t mac[MAC_SIZE];         // 0x12–0x31: SHA256 or equivalent MAC (big-endian).
    uint8_t short_payload[ASYMMETRIC_SIZE - MAC_SIZE - 10]; // 0x32–0x3F: First 14 bytes of payload or short payload.

    // Variable-sized payload starts here.
    uint8_t payload[];             // Remaining encrypted payload.
  };

#undef DFTLS_PACKED

}

#endif
