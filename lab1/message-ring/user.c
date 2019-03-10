#include <stdlib.h>
#include <stdio.h>
#include "user.h"

#define ID_SIZE 128


typedef struct User
{
    char id[ID_SIZE];
    int in_port;
    int out_ip; //server
    int out_port; //server
    // int has_token;
    // protocol
} User;

User user;


int user_routine()
{
    // read
    // sleep 1 sec
    // forward
}


int print_line(char *msg)
{
    printf("%s\n", msg);
}

int set_string(char *msg, char *ptr)
{
    print_line(msg);
    scanf("%s", ptr);
}

int set_int(char *msg, int *ptr)
{
    print_line(msg);
    scanf("%d", ptr);
}

void new_user()
{
    user.id = malloc(ID_SIZE * sizeof(char));
    set_string("")
    printf("%s\n", user.id);
}

void delete_user()
{
    free(user.id);
//    free(user);
}

//int main()
//{
//    new_user();
//    delete_user();
//}