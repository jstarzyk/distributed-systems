namespace java errors

struct InvalidFirstName {
}

struct InvalidLastName {
}

struct InvalidID {
}

struct InvalidAmount {
}

struct InvalidCurrency {
}

struct InvalidDueDate {
}

union UArgumentError {
    1: InvalidFirstName ifn,
    2: InvalidLastName iln,
    3: InvalidID iid,
    4: InvalidAmount ia,
    5: InvalidCurrency ic,
    6: InvalidDueDate idd,
}

exception ArgumentError {
    1: UArgumentError error,
    2: string message,
}
