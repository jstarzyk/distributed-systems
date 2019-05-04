include "money.thrift"

namespace java bank

struct AuthToken {
    1: string id,
    2: string passwordHash
}

//exception InvalidDueDate {
//    1: string message
//}

exception Unauthenticated {
    1: string message
}

exception Unauthorized {
    1: string message
}

service BankService {
//    string auth(
//        1: string id,
//        2: string passwordHash
//    ) throws (
//        1: InvalidCredentials eic
//    ),

//    money.Money balance() throws (1: Unauthorized unauthorized)
    money.Money balance(1: AuthToken authToken) throws (1: Unauthenticated unauthenticated)
}
