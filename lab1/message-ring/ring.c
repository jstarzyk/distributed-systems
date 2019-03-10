#include "ring.h"
#include <stdio.h>
#include <stdlib.h>

Node *Node_new(char *id, uint ip, uint port)
{
    Node *result = malloc(sizeof(Node));
    memcpy(result->id, id, ID_SIZE);
    result->ip = ip;
    result->port = port;
    return result;
}

MessageHeader *MessageHeader_new(char *from, char *to, MessageType type)
{
    MessageHeader *result = malloc(sizeof(MessageHeader));
    memcpy(result->from, from, ID_SIZE);
    memcpy(result->to, to, ID_SIZE);
    result->type = type;
    return result;
}

void create_message(Message *result, MessageHeader *header, char *content)
{
    result->header = *header;
    result->content = content;
}

//Message *Message_new(MessageHeader *header, char *content)
//{
//    Message *result = malloc(sizeof(Message));
//    result->header = *header;
//    result->content = content;
//    return result;
////    if (type == JOIN_REQUEST) {
////
////    } else if (type == JOIN_REPLY) {
////
////    } else if (type == NETWORK_STATE) {
////
////    } else if (type == PING) {
////
////    } else {
////
////    }
//}

void create_token(Token *result, int n)
{
    result->n = n;
    result->messages = malloc(n * sizeof(Message));

    MessageHeader *mh1 = MessageHeader_new("A", "B", JOIN_REQUEST);
    Message_JoinRequest *mc1 = malloc(sizeof(Message_JoinRequest));
    mc1->node = *Node_new("A", 1200, 80);
    create_message(&result->messages[0], mh1, (char *) mc1);

    MessageHeader *mh2 = MessageHeader_new("C", "D", JOIN_REPLY);
    Message_JoinReply *mc2 = malloc(sizeof(Message_JoinReply));
    mc2->node = *Node_new("A", 1300, 80);
    create_message(&result->messages[0], mh2, (char *) mc2);

    MessageHeader *mh3 = MessageHeader_new("E", "F", NETWORK_STATE);
    Message_NetworkState *mc3 = malloc(sizeof(Message_NetworkState));
    mc3->n = 3;
    mc3->nodes = malloc(mc3->n * sizeof(Node));
//    mc3->node = *Node_new("A", 1400, 80);
    create_message(&result->messages[0], mh3, (char *) mc3);

    MessageHeader *mh4 = MessageHeader_new("G", "H", PING);
    Message_JoinRequest *mc1 = malloc(sizeof(Message_JoinRequest));
    mc1->node = *Node_new("A", 1200, 80);
    create_message(&result->messages[0], mh1, (char *) mc1);

}

int main()
{
    Token token;
    token.n = 4;
    Message_JoinRequest m1;
    Message m1 =
    Message_JoinReply m2;
    Message_NetworkState m3;
    Message_Ping m4;
    token.messages = malloc(token.n * sizeof(Message));
    token.messages[0] =
}
