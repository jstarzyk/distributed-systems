#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <netinet/in.h>

#include "serialization.h"

#define SERIALIZED_MESSAGE_HEADER_SIZE (2 * ID_SIZE + sizeof(uint32_t))
#define SERIALIZED_NODE_SIZE (ID_SIZE + 2 * sizeof(uint32_t))


size_t _calculate_serialized_message_size(Message *message)
{
    size_t result = SERIALIZED_MESSAGE_HEADER_SIZE;
    switch (message->header.type) {
        case JOIN_REQUEST:
        case JOIN_REPLY:
            result += SERIALIZED_NODE_SIZE;
            break;
        case NETWORK_STATE:
            result += sizeof(uint32_t);
            for (int i = 0; i < ((Message_NetworkState *) message->content)->n; i++) {
                result += SERIALIZED_NODE_SIZE;
            }
            break;
        case PING:
            result += DATA_SIZE;
            break;
    }
    return result;
}

size_t _calculate_serialized_token_size(Token *token)
{
    size_t result = sizeof(token->n);
    for (int i = 0; i < token->n; i++) {
        result += _calculate_serialized_message_size(&token->messages[i]);
    }
    return result;
}

SerializationResult _serialize_message_header(MessageHeader *message_header, char *ptr)
{
    memcpy(ptr, message_header->from, ID_SIZE);
    size_t offset = ID_SIZE;
    memcpy(ptr + offset, message_header->to, ID_SIZE);
    offset += ID_SIZE;
    *(uint32_t *) (ptr + offset) = htonl(message_header->type);

    return OK;
}

SerializationResult _deserialize_message_header(char *ptr, MessageHeader *message_header)
{
    memcpy(message_header->from, ptr, ID_SIZE);
    size_t offset = ID_SIZE;
    memcpy(message_header->to, ptr + offset, ID_SIZE);
    offset += ID_SIZE;
    message_header->type = ntohl(*(uint32_t *) (ptr + offset));

    return OK;
}

SerializationResult _serialize_node(Node *node, char *ptr)
{
    memcpy(ptr, node->id, ID_SIZE);
    size_t offset = ID_SIZE;
    *(uint32_t *) (ptr + offset) = htonl(node->ip);
    offset += sizeof(uint32_t);
    *(uint32_t *) (ptr + offset) = htonl(node->port);

    return OK;
}


SerializationResult _deserialize_node(char *ptr, Node *node)
{
    memcpy(node->id, ptr, ID_SIZE);
    size_t offset = ID_SIZE;
    node->ip = ntohl(*(uint32_t *) (ptr + offset));
    offset += sizeof(uint32_t);
    node->port = ntohl(*(uint32_t *) (ptr + offset));

    return OK;
}

SerializationResult _serialize_message(Message *message, char *ptr)
{
    _serialize_message_header(&message->header, ptr);
    char *content_ptr = ptr + SERIALIZED_MESSAGE_HEADER_SIZE;

    if (message->header.type == JOIN_REQUEST) {
        _serialize_node(&(((Message_JoinRequest *) message->content)->node), content_ptr);
    } else if (message->header.type == JOIN_REPLY) {
        _serialize_node(&(((Message_JoinReply *) message->content)->node), content_ptr);
    } else if (message->header.type == NETWORK_STATE) {
        Message_NetworkState *content = (Message_NetworkState *) message->content;

        uint32_t n = htonl((uint32_t) content->n);
        *(uint32_t *) content_ptr = n;
        content_ptr += sizeof(uint32_t);

        for (int i = 0; i < content->n; i++) {
            _serialize_node(&content->nodes[i], content_ptr);
            content_ptr += SERIALIZED_NODE_SIZE;
        }
    } else if (message->header.type == PING) {
        memcpy(content_ptr, &(((Message_Ping *) message->content)->data), DATA_SIZE);
    } else {
        return ERROR;
    }

    return OK;
}

SerializationResult _deserialize_message(char *ptr, Message *message)
{
    _deserialize_message_header(ptr, &message->header);
    char *content_ptr = ptr + SERIALIZED_MESSAGE_HEADER_SIZE;

    if (message->header.type == JOIN_REQUEST) {
        message->content = malloc(sizeof(Message_JoinRequest));
        _deserialize_node(content_ptr, &(((Message_JoinRequest *) message->content)->node));
    } else if (message->header.type == JOIN_REPLY) {
        message->content = malloc(sizeof(Message_JoinReply));
        _deserialize_node(content_ptr, &(((Message_JoinReply *) message->content)->node));
    } else if (message->header.type == NETWORK_STATE) {
        Message_NetworkState *content = (Message_NetworkState *) message->content;

        int n = ntohl(*(uint32_t *) content_ptr);
        content_ptr += sizeof(uint32_t);
        content->n = n;
        content->nodes = malloc(n * sizeof(Node));

        for (int i = 0; i < content->n; i++) {
            _deserialize_node(content_ptr, &content->nodes[i]);
            content_ptr += SERIALIZED_NODE_SIZE;
        }
    } else if (message->header.type == PING) {
        memcpy(&(((Message_Ping *) message->content)->data), content_ptr, DATA_SIZE);
    } else {
        return ERROR;
    }

    return OK;
}

SerializationResult serialize_token(Token *token, SerializedToken *serialized_token)
{
    size_t size =  sizeof(size_t) + _calculate_serialized_token_size(token);
    char *data = malloc(size);

    size_t offset = 0;
    *(uint32_t *) (data + offset) = htonl((uint32_t) size);
    offset += sizeof(uint32_t);
    *(uint32_t *) (data + offset) = htonl((uint32_t) token->n);

    for (int i = 0; i < token->n; i++) {
        Message *message = &token->messages[i];
        SerializationResult res = _serialize_message(message, data + offset);
        if (res != OK) {
            return res;
        }
        offset += _calculate_serialized_message_size(message);
    }

    serialized_token->size = size;
    serialized_token->data = data;

    return OK;
}

SerializationResult deserialize_token(SerializedToken *serialized_token, Token *token)
{
    char *data = serialized_token->data;

    size_t offset = 0;
    int size = ntohl(*(uint32_t *)(data + offset));
    if (size != serialized_token->size) {
        // TODO: Error
    }
    offset += sizeof(uint32_t);

    int n = ntohl(*(uint32_t *)(data + offset));
    offset += sizeof(uint32_t);

    token->n = n;
    token->messages = malloc(n * sizeof(Message));

    for (int i = 0; i < n; i++) {
        SerializationResult res = _deserialize_message(data + offset, &token->messages[i]);
        if (res != OK) {
            return res;
        }
        offset += _calculate_serialized_message_size(&token->messages[i]);
        if (offset >= size) {
            return ERROR;
        }
    }

    return OK;
}
