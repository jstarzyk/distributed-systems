//namespace java money

//typedef i64 BaseMax
//typedef i32 FractionalMax

//enum Currency {
//    PLN,
//    USD,
//    EUR,
//    GBP,
//    CHF,
//}

//struct Amount {
//    1: BaseMax base,
//    2: FractionalMax fractional,
//}

//const map<Currency, FractionAmount> SUBUNITS = {
//    PLN: 100,
//    USD: 100,
//    EUR: 100,
//    GBP: 100,
//    CHF: 100,
//}
//const i32 SUBUNITS = 100

//struct Money {
//    1: Currency currency,
//    2: Amount amount,
//    2: string amount,


//    2: i64 baseAmount,
//    3: i32 fractionalAmount
//    2: BaseAmount baseAmount,
//    3: FractionAmount fractionAmount,
//}

//exception InvalidAmount {
//    1: string message
//}
//
//exception InvalidCurrency {
//    1: string message
//}
