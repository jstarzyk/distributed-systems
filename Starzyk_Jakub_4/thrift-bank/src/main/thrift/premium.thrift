include "bank.thrift"
include "money.thrift"
include "errors.thrift"

namespace java premium

struct CreditSummary {
    1: money.Money domesticTotal,
    2: optional money.Money foreignTotal,
}

service PremiumService extends bank.BankService {
    CreditSummary credit(
        1: string currency,
        2: string amount,
        3: string dueDate,
        4: bank.AuthToken authToken
    ) throws (
//        1: money.InvalidCurrency eic,
//        2: money.InvalidAmount eia,
//        3: bank.InvalidDueDate eidd,
//        4: bank.Unauthenticated unauthenticated,
//        5: bank.Unauthorized unauthorized,
        1: errors.ArgumentError argumentError,
        2: bank.Unauthenticated unauthenticated,
        3: bank.Unauthorized unauthorized,

    )
}