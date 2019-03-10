cmake_minimum_required(VERSION 3.13)
project(message_ring C)

set(CMAKE_C_STANDARD 11)

add_executable(message_ring user.c user.h tcp.c tcp.h ring.c ring.h serialization.c serialization.h node.c node.h)