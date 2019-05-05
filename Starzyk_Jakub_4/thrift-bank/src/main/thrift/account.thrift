include "errors.thrift"

namespace java account

enum AccountType {
    STANDARD,
    PREMIUM,
}

struct Account {
    1: AccountType category,
    2: string password,
}

service AccountService {
    Account account(
        1: string firstName,
        2: string lastName,
        3: string id,
        4: string limit
    ) throws (
        1: errors.ArgumentError argumentError
    )
}