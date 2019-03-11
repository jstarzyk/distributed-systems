#include <unistd.h>
#include <string.h>

#include "lib.h"
#include "tcp.h"

#define SEND_PROBABILITY 0.5


typedef enum
{
    OK, ERROR
} ParsingResult;

Token *create_empty_token()
{
    Token *result = malloc(sizeof(Token));
    result->n = 0;
    result->messages = NULL;
    return result;
}

ParsingResult parse_args(int argc, char **argv, NodeData *result)
{
    if (argc < 6) {
        return ERROR;
    }
    int arg_no = 0;
    char *self_id = argv[++arg_no];
    uint self_port = (uint) atoi(argv[++arg_no]);
    char *neighbor_ip = argv[++arg_no];
    uint neighbor_port = (uint) atoi(argv[++arg_no]);
    char *has_token = argv[++arg_no];

    strncpy(result->self->id, self_id, ID_SIZE);
    result->self->ip = net_get_ip("127.0.0.1");
    result->self->port = self_port;
    result->neighbor->ip = net_get_ip(neighbor_ip);
    result->neighbor->port = neighbor_port;
    if (strcmp(has_token, "y") == 0) {
        result->token = create_empty_token();
    } else {
        result->token = NULL;
    }

    return OK;
}

void handle_message_to_self(NodeData *data, Message *message)
{
    // TODO: Remove Message_NetworkState
    free(message->content);
}


int is_message_to_other(Node *self, MessageHeader *header)
{
    return strcmp(self->id, header->to);
}

char *choose_node_id(NodeData *data)
{
    double send_p = drand48();
    if (send_p < SEND_PROBABILITY) {
        int r = rand() % data->network_node_ids_n;
        return data->network_node_ids[r];
    } else {
        return NULL;
    }
}


Message *generate_random_message(NodeData *data) {
    char *random_node_id = choose_node_id(data);
    if (random_node_id != NULL) {
        Message *message = malloc(sizeof(Message));
        strncpy(message->header.from, data->self->id, ID_SIZE);
        strncpy(message->header.to, random_node_id, ID_SIZE);
        message->header.type = PING;
        Message_Ping *ping = malloc(sizeof(Message_Ping));
        strcpy(ping->data, "Hello");
        message->content = (char *) ping;
        return message;
    } else {
        return NULL;
    }
}


void handle_token(NodeData *data) {
    int to_self_n = 0;
    Token *token = data->token;
    Message **to_other_ptrs = malloc(token->n * sizeof(Message *));

    Message *generated_message = generate_random_message(data);

    for (int i = 0; i < token->n; i++) {
        Message *message = &token->messages[i];
        if (is_message_to_other(data->self, &message->header) != 0) {
            to_other_ptrs[i] = message;
        } else {
            to_other_ptrs[i] = NULL;
            to_self_n++;
            handle_message_to_self(data, message);
        }
    }

    if (to_self_n > 0 || generated_message != NULL) {
        int num_messages_to_send = token->n - to_self_n;
        if (generated_message != NULL) {
            num_messages_to_send++;
        }

        Message *messages = malloc(num_messages_to_send * sizeof(Message));
        size_t offset = 0;

        for (int i = 0; i < token->n; i++) {
            if (to_other_ptrs[i] != NULL) {
                memcpy(messages + offset, to_other_ptrs[i], sizeof(Message));
                offset += sizeof(Message);
            }
        }

        if (generated_message != NULL) {
            memcpy(messages + offset, generated_message, sizeof(Message));
        }

        token->n = num_messages_to_send;
        free(token->messages);
        token->messages = messages;
    }

    free(to_other_ptrs);
}


NodeState next_state(NodeData *data, NodeState current_state)
{
    if (current_state == START) {
        if (data->token == NULL) {
            return NO_TOKEN;
        } else {
            return HAS_TOKEN;
        }
    } else if (current_state == HAS_TOKEN) {
        if (data->token != NULL) {
            handle_token(data);
            send_token(data);
            return NO_TOKEN;
        } else {
            error("state");
        }
    } else if (current_state == NO_TOKEN) {
        if (data->token == NULL) {
            return NO_TOKEN;
        } else {
            return HAS_TOKEN;
        }
    } else {
        error("state");
    }
}


void net_received_token(NodeData *data, Token *token)
{
    data->token = token;
}


int main(int argc, char **argv)
{
    NodeData data;
    if (parse_args(argc, argv, &data) != OK) {
        error("parse_args");
    }

    NodeState state = START;

    register_node(&data, &net_received_token);

    while (state != EXIT) {
        state = next_state(&data, state);
        sleep(1);
    }
}
