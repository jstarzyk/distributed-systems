namespace java money

//typedef i32 FractionAmount
//typedef i64 BaseAmount

enum Currency {
    PLN,
    USD,
    EUR,
    GBP,
    CHF,
}

//const map<Currency, FractionAmount> SUBUNITS = {
//    PLN: 100,
//    USD: 100,
//    EUR: 100,
//    GBP: 100,
//    CHF: 100,
//}
//const i32 SUBUNITS = 100

struct Money {
    1: Currency currency,
    2: double amount
//    2: BaseAmount baseAmount,
//    3: FractionAmount fractionAmount,
}

//exception InvalidAmount {
//    1: string message
//}
//
//exception InvalidCurrency {
//    1: string message
//}
