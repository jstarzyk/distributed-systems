include "auth.thrift"
include "errors.thrift"
include "standard.thrift"
//include "credit.thrift"
include "money.thrift"

namespace java premium

struct CreditSummary {
    1: money.Money domesticTotal,
    2: optional money.Money foreignTotal,
}

service PremiumService extends standard.StandardService {
//    credit.CreditSummary credit(
    CreditSummary credit(
        1: string currency,
        2: string amount,
        3: string dueDate,
        4: auth.AuthToken token
    ) throws (
        1: errors.ArgumentError argumentError,
        2: auth.Unauthenticated unauthenticated,
        3: auth.Unauthorized unauthorized,
    )
}
