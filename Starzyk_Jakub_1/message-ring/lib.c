#include "lib.h"
#include <stdio.h>
#include <stdlib.h>

void error(const char *message)
{
    fprintf(stderr, "error: %s\n", message);
    perror(message);
    exit(1);
}
