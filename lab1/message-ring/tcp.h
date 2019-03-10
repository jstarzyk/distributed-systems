#ifndef MESSAGE_RING_TCP_H
#define MESSAGE_RING_TCP_H

#include "ring.h"

typedef void (*token_handler(Token*));

void send_token(Token *token, Node *neighbor);
void register_node(Node *node, token_handler handler);

#endif //MESSAGE_RING_TCP_H
