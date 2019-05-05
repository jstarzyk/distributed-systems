include "auth.thrift"

namespace java standard

service StandardService {
    string balance(1: auth.AuthToken token) throws (1: auth.Unauthenticated unauthenticated)
}
