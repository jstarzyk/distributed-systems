include "auth.thrift"
include "errors.thrift"
include "standard.thrift"

namespace java premium

struct CreditInfo {
    1: string domesticTotal,
    2: optional string foreignTotal,
}

service PremiumService extends standard.StandardService {
    CreditInfo credit(
        1: string amount,
        2: string currencyCode,
        3: string dueDate,
        4: auth.AuthToken token,
    ) throws (
        1: errors.ArgumentError argumentError,
        2: auth.Unauthenticated unauthenticated,
        3: auth.Unauthorized unauthorized,
    )
}
