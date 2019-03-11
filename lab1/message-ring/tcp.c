#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <pthread.h>

#include "tcp.h"
#include "serialization.h"


//void error(const char * message) {
//    perror(message);
//    exit(1);
//}

void send_token(NodeData *data)
{
    Token *token = data->token;
    Node *neighbor = data->neighbor;
    SerializedToken st;
    SerializationResult res = serialize_token(token, &st);

    if (res != OK) {
        error("serialize_token");
    }

    int fd;
    fd = socket(AF_INET, SOCK_STREAM, 0);
    if (fd == -1) {
        error("socket");
    }

    struct sockaddr_in dest;
    memset(&dest, 0, sizeof(dest));
    dest.sin_family = AF_INET;
    dest.sin_addr.s_addr = htonl(neighbor->ip);
    dest.sin_port = htons((uint16_t) neighbor->port);

    if (connect(fd, (struct sockaddr *)&dest, sizeof(struct sockaddr_in)) == -1) {
        error("connect");
    }

    size_t bytes_to_send = st.size;
    size_t offset = 0;

    while (bytes_to_send > 0) {
        ssize_t sent_bytes = send(fd, st.data + offset, bytes_to_send, 0);
        if (sent_bytes == -1) {
            error("send");
        } else {
            bytes_to_send -= sent_bytes;
            offset += sent_bytes;
        }
    }

    close(fd);
}


void handle_connection(NodeData *data, token_handler handler, int conn) {
    uint32_t size = 0;
    ssize_t res_size = read(conn, &size, sizeof(uint32_t));
    if (res_size == -1) {
        error("read size error");
    }
    if (res_size != sizeof(uint32_t)) {
        error("read size mismatch");
    }

    size = ntohl(size);

    char *buffer = malloc(size);
    size_t bytes_to_read = size;
    size_t offset = 0;

    while (bytes_to_read > 0) {
        ssize_t res_data = read(conn, buffer + offset, bytes_to_read);
        if (res_data == -1) {
            error("read data error");
        }
        bytes_to_read -= res_data;
        offset += res_data;
    }

    SerializedToken st;
    st.size = size;
    st.data = buffer;

    Token token;
    if (OK != deserialize_token(&st, &token)) {
        error("deserialize_token");
    }

    free(buffer);

    handler(data, &token);
}

typedef struct
{
    int fd;
    NodeData *data;
    token_handler handler;
} ThreadArgs;


void *lll(void *arg)
{
    ThreadArgs *_arg = (ThreadArgs *) arg;
    for (;;) {
        struct sockaddr_in peer;
        socklen_t peer_addr_size;
        int conn = accept(_arg->fd, (struct sockaddr *)&peer, &peer_addr_size);
        if (conn == -1) {
            error("accept");
        }
        handle_connection(_arg->data, _arg->handler, conn);
    }
}

void register_node(NodeData *data, token_handler handler)
{
    int fd;
    fd = socket(AF_INET, SOCK_STREAM, 0);

    if (fd == -1) {
        error("socket");
    }

    //TODO: setsockopt(fd, )

    struct sockaddr_in addr;
    memset(&addr, 0, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_addr.s_addr = htonl(data->self->ip);
    addr.sin_port = htons((uint16_t) data->self->port);

    if (bind(fd, (struct sockaddr *)&addr, sizeof(struct sockaddr_in)) == -1) {
        error("bind");
    }

    if (listen(fd, 0) == -1) {
        error("listen");
    }

    pthread_t pid;
    ThreadArgs arg;
    arg.handler = handler;
    arg.data = data;
    arg.fd = fd;
    int res = pthread_create(&pid, NULL, lll, &arg);
    if (res == -1) {
        error("pthread_create");
    }
}

uint net_get_ip(char *ip)
{
    // TODO: Handle errors
    in_addr_t addr = inet_addr(ip);
    return ntohl(addr);
}