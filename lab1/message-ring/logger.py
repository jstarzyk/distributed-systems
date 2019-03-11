#!/usr/bin/env python

import struct
import socket
import datetime

FILE_NAME = 'ring_token.log'
IP = '224.0.0.1'
PORT = 4444


def format_log_msg(msg):
    return "%s: %s" % (datetime.datetime.now(), msg)


def make_socket():
    result = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
    result.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    result.bind((IP, PORT))
    req = struct.pack("4sl", socket.inet_aton(IP), socket.INADDR_ANY)
    result.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, req)
    return result


def loop(sock, out):
    while True:
        data, addr = sock.recvfrom(1024)
        size = struct.unpack("!I", data[:4])
        msg = struct.unpack_from("%ds" % size, data, 4)
        msg_text = msg[0].decode('utf-8')
        # msg_text = str(msg[0])

        print("Received log message: %s" % msg_text)
        out.write(format_log_msg(msg_text))


def main():
    print("Waiting for log messages...")
    with make_socket() as sock:
    # sock = make_socket()
        with open(FILE_NAME, "w") as out:
            loop(sock, out)


if __name__ == "__main__":
    main()