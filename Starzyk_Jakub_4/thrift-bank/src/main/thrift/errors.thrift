namespace java errors

struct InvalidFirstName {
//    1: string message
}

struct InvalidLastName {
//    1: string message
}

struct InvalidID {
//    1: string message
}
//
struct InvalidDueDate {
//    1: string message
}

struct InvalidAmount {
//    1: string message
}

struct InvalidCurrency {
//    1: string message
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
