//include "money.thrift"
//include "errors.thrift"

namespace java account

enum AccountType {
    STANDARD,
    PREMIUM,
}

struct Account {
    1: AccountType category,
    2: string password,
}

//exception InvalidFirstName {
//    1: string message
//}
//
//exception InvalidLastName {
//    1: string message
//}
//
//exception InvalidID {
//    1: string message
//}

//exception InvalidPassword {
//    1: string message
//}

//exception Unauthorized {
//    1: string message
//}

//service AccountService {
//    Account account(
//        1: string firstName,
//        2: string lastName,
//        3: string id,
//        4: string limit
//    ) throws (
//        1: errors.ArgumentError argumentError
////        1: errors.InvalidFirstName eifn,
////        2: errors.InvalidLastName eiln,
////        3: errors.InvalidID eiid,
////        4: money.InvalidAmount eia,
////        1: errors.ArgumentErrors errors,
////        2:
//    ),
////
////    string auth(
////        1: string id,
////        2: string passwordHash
////    ) throws (
////        1: InvalidID eiid,
////        2: InvalidPassword eip
////    ),
//}
