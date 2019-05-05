include "errors.thrift"

namespace java account

enum AccountType {
    STANDARD,
    PREMIUM,
}

struct AccountInfo {
    1: AccountType category,
    2: string password,
}

service AccountService {
    AccountInfo account(
        1: string firstName,
        2: string lastName,
        3: string id,
        4: string monthlyLimit,
        5: string currencyCode
    ) throws (
        1: errors.ArgumentError argumentError
    )
}
