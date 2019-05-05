//include "money.thrift"
//include "errors.thrift"
//include "account.thrift"
//include "auth.thrift"
//include "credit.thrift"

//namespace java bank

//simple AccountService {
//    account.Account account(
//        1: string firstName,
//        2: string lastName,
//        3: string id,
//        4: string limit
//    ) throws (
//        1: errors.ArgumentError argumentError
//    )
//}
//
//simple StandardService {
//    money.Money balance(1: auth.AuthToken authToken) throws (1: auth.Unauthenticated unauthenticated)
//}
//
//simple PremiumService extends StandardService {
//    credit.CreditSummary credit(
//        1: string currency,
//        2: string amount,
//        3: string dueDate,
//        4: auth.AuthToken token
//    ) throws (
//        1: errors.ArgumentError argumentError,
//        2: auth.Unauthenticated unauthenticated,
//        3: auth.Unauthorized unauthorized,
//    )
//}
