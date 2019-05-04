include "money.thrift"

namespace java bank

exception InvalidDate {
    1: string message
}

exception InvalidCredentials {
    1: string message
}

exception Unauthorized {
    1: string message
}

service BankService {
    string auth(
        1: string id,
        2: string passwordHash
    ) throws (
        1: InvalidCredentials eic
//        1: InvalidID eiid,
//        2: InvalidPassword eip
    ),

    money.Money balance() throws (1: Unauthorized unauthorized)
}
