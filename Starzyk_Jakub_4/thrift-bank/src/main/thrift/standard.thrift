include "money.thrift"
include "auth.thrift"

namespace java standard

service StandardService {
    money.Money balance(1: auth.AuthToken authToken) throws (1: auth.Unauthenticated unauthenticated)
}
