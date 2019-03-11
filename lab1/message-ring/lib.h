#ifndef MESSAGE_RING_TOKEN_H
#define MESSAGE_RING_TOKEN_H

#include <stdlib.h>

#define ID_SIZE 16
#define DATA_SIZE 1024

typedef enum
{
    START,
    HAS_TOKEN,
    NO_TOKEN,
    EXIT
} NodeState;

typedef enum
{
    JOIN_REQUEST,
    JOIN_REPLY,
    NETWORK_STATE,
    PING
} MessageType;

typedef struct
{
    char id[ID_SIZE];
    uint ip;
    uint port;
} Node;

typedef struct
{
    char from[ID_SIZE];
    char to[ID_SIZE];
    MessageType type;
} MessageHeader;

typedef struct
{
    MessageHeader header;
    char *content;
} Message;

typedef struct
{
    int n;
    Message *messages;
} Token;

typedef struct
{
    size_t size;
    char *data;
} SerializedToken;

typedef struct
{
    Node node;
} Message_JoinRequest;

typedef struct
{
    Node node;
} Message_JoinReply;

typedef struct
{
    int n;
    Node *nodes;
} Message_NetworkState;

typedef struct
{
    char data[DATA_SIZE];
} Message_Ping;


typedef struct
{
    Node *self;
    Node *neighbor;
    Token *token;
    char **network_node_ids;
    int network_node_ids_n;
} NodeData;

void error(const char * message);


#endif //MESSAGE_RING_TOKEN_H
