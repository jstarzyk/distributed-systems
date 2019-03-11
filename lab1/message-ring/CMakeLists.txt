cmake_minimum_required(VERSION 3.13)
project(message_ring C)

set(CMAKE_C_STANDARD 11)

add_executable(message_ring tcp.c tcp.h lib.c lib.h serialization.c serialization.h node.c)
target_link_libraries(message_ring pthread)
