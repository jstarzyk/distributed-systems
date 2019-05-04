include "bank.thrift"
include "money.thrift"

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
    ) throws (
        1: bank.Unauthorized unauthorized,
        2: money.InvalidCurrency eic,
        3: money.InvalidAmount eia,
        4: bank.InvalidDate eid
    )
}