#ifndef MESSAGE_RING_SERIALIZATION_H
#define MESSAGE_RING_SERIALIZATION_H

#include "lib.h"

typedef enum
{
    OK, ERROR
} SerializationResult;

SerializationResult serialize_token(Token *token, SerializedToken *serialized_token);
SerializationResult deserialize_token(SerializedToken *serialized_token, Token *token);


#endif //MESSAGE_RING_SERIALIZATION_H
