include "money.thrift"

namespace java credit

struct CreditSummary {
    1: money.Money domesticTotal,
    2: optional money.Money foreignTotal,
}
