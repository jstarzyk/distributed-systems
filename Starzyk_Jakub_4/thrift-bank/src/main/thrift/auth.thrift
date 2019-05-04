namespace java auth

struct AuthToken {
    1: string id,
    2: string passwordHash
}

exception Unauthenticated {
    1: string message
}

exception Unauthorized {
    1: string message
}
