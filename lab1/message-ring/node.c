#include "node.h"
#include "ring.h"

typedef struct
{
    char *id;
    char *in_port;
    char *out_ip;
    char *out_port;
    char *has_token;
    char *protocol;
} Args;

typedef enum
{
    OK, ERROR
} ParsingResult;

ParsingResult parse_args(int argc, char **argv, Args *result)
{

}

int main(int argc, char **argv)
{
    Args args;
    if (parse_args(argc, argv, &args) != OK) {
        return -1;
    }


}
