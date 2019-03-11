#ifndef MESSAGE_RING_TCP_H
#define MESSAGE_RING_TCP_H

#include "lib.h"

typedef void (*token_handler)(NodeData *, Token*);

void send_token(NodeData *data);
void register_node(NodeData *data, token_handler handler);
uint net_get_ip(char *ip);

#endif //MESSAGE_RING_TCP_H
