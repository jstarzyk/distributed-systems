#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <pthread.h>

#include "net.h"
#include "serialization.h"


void send_token(NodeData *data)
{

    Token *token = data->token;
    Node *neighbor = data->neighbor;
    SerializedToken st;
    SerializationResult res = serialize_token(token, &st);

    if (res != OK) {
        error("serialize_token");
    }

    printf("Sending token with %d messages, %ld bytes\n", token->n, st.size);

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
    printf("hc: enter\n");
    uint32_t net_size = 0;
    ssize_t res_size = read(conn, &net_size, sizeof(uint32_t));
    if (res_size == -1) {
        error("read size error");
    }
    if (res_size != sizeof(uint32_t)) {
        error("read size mismatch");
    }

    uint32_t size = ntohl(net_size);
    printf("hc: read size: %d\n", size);

    char *buffer = malloc(size);
//    printf("1\n");
    *((uint32_t *)buffer) = net_size;
//    printf("2\n");

    size_t offset = sizeof(uint32_t);
    size_t bytes_to_read = size - offset;

    while (bytes_to_read > 0) {
        ssize_t res_data = read(conn, buffer + offset, bytes_to_read);
        if (res_data == -1) {
            error("read data error");
        }
        bytes_to_read -= res_data;
        offset += res_data;
    }
//    printf("3\n");

    SerializedToken st;
    st.size = size;
    st.data = buffer;

    Token token;
    if (OK != deserialize_token(&st, &token)) {
        error("deserialize_token");
    }
//    printf("4\n");

    free(buffer);

    printf("hc: calling handler...\n");
    handler(data, &token);
    printf("hc: exit\n");
}

typedef struct
{
    int fd;
    NodeData *data;
    token_handler handler;
} ThreadArgs;


void *network_loop(void *arg)
{
    ThreadArgs *_arg = (ThreadArgs *) arg;
    for (;;) {
        struct sockaddr_in peer;
        socklen_t peer_addr_size;
        printf("nl: accepting, fd = %d\n", _arg->fd);
        int conn = accept(_arg->fd, (struct sockaddr *)&peer, &peer_addr_size);
        if (conn == -1) {
            error("accept");
        }
        handle_connection(_arg->data, _arg->handler, conn);
    }

    // TODO: free(_arg);
}

void register_node(NodeData *data, token_handler handler)
{
    int fd;
    fd = socket(AF_INET, SOCK_STREAM, 0);

    if (fd == -1) {
        error("socket");
    }

    printf("socket created with fd: %d\n", fd);

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
    ThreadArgs *arg = malloc(sizeof(ThreadArgs));
    arg->handler = handler;
    arg->data = data;
    arg->fd = fd;
    int res = pthread_create(&pid, NULL, network_loop, arg);
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

void send_log_info(char *message, uint addr, uint port)
{
    printf("Sending log info: %s\n", message);

    int fd;
    fd = socket(AF_INET, SOCK_DGRAM, 0);
    if (fd == -1) {
        error("socket");
    }

    struct sockaddr_in dest;
    memset(&dest, 0, sizeof(dest));
    dest.sin_family = AF_INET;
    dest.sin_addr.s_addr = htonl(addr);
    dest.sin_port = htons((uint16_t) port);

    size_t len = strlen(message);
    size_t buffer_size = sizeof(uint32_t) + len;
    char *buffer = malloc(buffer_size);
    *(uint32_t *) buffer = htonl((uint32_t) len);
    memcpy(buffer + sizeof(uint32_t), message, len);

    ssize_t res = sendto(fd, buffer, buffer_size, 0, (struct sockaddr *) &dest, sizeof(dest));
    if (res < 0) {
        error("send_log_info");
    }

    close(fd);
}