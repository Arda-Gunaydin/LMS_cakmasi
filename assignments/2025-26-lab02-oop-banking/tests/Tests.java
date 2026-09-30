import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;

/** Hidden tests for last year's Lab 2: OOP Review - Banking System (school style: one test = one check). */
public class Tests {

    static final Class<IllegalArgumentException> IAE = IllegalArgumentException.class;

    /** What the body printed, without \r and without the last line break. */
    static String out(T.Body body) throws Throwable {
        return T.captureOut(body).replace("\r", "").replaceAll("\n$", "");
    }

    static String f(String fmt, Object... args) {
        return String.format(fmt, args);
    }

    static Object card(String number, String holder) {
        return T.make("CreditCardPayment", number, holder);
    }

    static Object checking(String no, double bal, double od) {
        return T.make("CheckingAccount", no, bal, od);
    }

    static Object savings(String no, double bal, double rate) {
        return T.make("SavingsAccount", no, bal, rate);
    }

    static double bal(Object acc) {
        return ((Number) T.call(acc, "getBalance")).doubleValue();
    }

    static int transactions(Object acc) {
        return ((Collection<?>) T.field(acc, "transactions")).size();
    }

    static boolean noPublicFields(String cls) {
        for (Field fl : T.cls(cls).getDeclaredFields()) {
            if (Modifier.isPublic(fl.getModifiers())) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {

        T.test("(structure)", "test_Lab02_structure", "class hierarchy", () -> {
            T.expectTrue("PaymentMethod must be an interface", () -> T.isInterface("PaymentMethod"));
            T.expectTrue("CreditCardPayment must implement PaymentMethod", () -> T.isA("CreditCardPayment", "PaymentMethod"));
            T.expectTrue("PayPalPayment must implement PaymentMethod", () -> T.isA("PayPalPayment", "PaymentMethod"));
            T.expectTrue("Transaction must implement Comparable", () -> T.isA("Transaction", "java.lang.Comparable"));
            T.expectTrue("Account must be abstract", () -> T.isAbstract("Account"));
            T.expectTrue("Customer must be abstract", () -> T.isAbstract("Customer"));
            T.expectTrue("Account.withdraw must be abstract", () -> T.isAbstract(T.declared("Account", "withdraw", 1)));
            T.expectTrue("Customer.getTransactionFee must be abstract",
                    () -> T.isAbstract(T.declared("Customer", "getTransactionFee", 0)));
            T.expectTrue("CheckingAccount and SavingsAccount must extend Account",
                    () -> T.extendsDirectly("CheckingAccount", "Account") && T.extendsDirectly("SavingsAccount", "Account"));
            T.expectTrue("RegularCustomer and PremiumCustomer must extend Customer",
                    () -> T.extendsDirectly("RegularCustomer", "Customer") && T.extendsDirectly("PremiumCustomer", "Customer"));
            for (String c : new String[] {"CreditCardPayment", "PayPalPayment", "Transaction", "Account", "CheckingAccount",
                    "SavingsAccount", "Customer", "Bank"}) {
                T.expectTrue(c + " must not have public fields", () -> noPublicFields(c));
            }
            T.expectTrue("maskCardNumber, verifySecurePayment and confirmTransaction must be private helpers",
                    () -> Modifier.isPrivate(T.declared("CreditCardPayment", "maskCardNumber", 0).getModifiers())
                            && Modifier.isPrivate(T.declared("CreditCardPayment", "verifySecurePayment", 0).getModifiers())
                            && Modifier.isPrivate(T.declared("CreditCardPayment", "confirmTransaction", 1).getModifiers()));
        });

        T.test("CreditCardPayment", "test_CreditCardPayment_paid", "CreditCardPayment accepted payments", () -> {
            T.method("CreditCardPayment", "pay", 1);
            Object c = card("1234 5678 9012 3456", "Ali Veli");
            T.expect("pay(50.0) should print the Paid line with the masked number",
                    f("Paid %.2f with credit card ****-****-****-3456 (holder: Ali Veli)", 50.0),
                    () -> out(() -> T.call(c, "pay", 50.0)));
            T.expect("pay(10000.0) (exactly the limit) should be accepted",
                    f("Paid %.2f with credit card ****-****-****-3456 (holder: Ali Veli)", 10000.0),
                    () -> out(() -> T.call(c, "pay", 10000.0)));
            T.expect("pay(0.0) should be accepted",
                    f("Paid %.2f with credit card ****-****-****-3456 (holder: Ali Veli)", 0.0),
                    () -> out(() -> T.call(c, "pay", 0.0)));
            T.expect("a card number without spaces is masked too: ****-****-****-9876",
                    f("Paid %.2f with credit card ****-****-****-9876 (holder: Ayse)", 12.5),
                    () -> out(() -> T.call(card("4111111111119876", "Ayse"), "pay", 12.5)));
            T.expect("a card number with 4 or fewer digits is shown unchanged: \"1 2 3\" -> 123",
                    f("Paid %.2f with credit card 123 (holder: Ayse)", 1.0),
                    () -> out(() -> T.call(card("1 2 3", "Ayse"), "pay", 1.0)));
            T.expect("a card number of exactly 4 digits \"9876\" is shown unchanged",
                    f("Paid %.2f with credit card 9876 (holder: Ayse)", 1.0),
                    () -> out(() -> T.call(card("9876", "Ayse"), "pay", 1.0)));
            T.expect("a card number of 5 digits \"12345\" -> ****-****-****-2345",
                    f("Paid %.2f with credit card ****-****-****-2345 (holder: Ayse)", 1.0),
                    () -> out(() -> T.call(card("12345", "Ayse"), "pay", 1.0)));
        });

        T.test("CreditCardPayment", "test_CreditCardPayment_refused", "CreditCardPayment refused payments", () -> {
            Object c = card("4111111111111111", "Ali");
            T.expect("pay(10000.01) should print Transaction not confirmed.", "Transaction not confirmed.",
                    () -> out(() -> T.call(c, "pay", 10000.01)));
            T.expect("pay(-5.0) should print Transaction not confirmed.", "Transaction not confirmed.",
                    () -> out(() -> T.call(c, "pay", -5.0)));
            T.expect("a blank holder name should print Verification failed.", "Verification failed.",
                    () -> out(() -> T.call(card("4111111111111111", "   "), "pay", 10.0)));
            T.expect("a null holder name should print Verification failed.", "Verification failed.",
                    () -> out(() -> T.call(card("4111111111111111", null), "pay", 10.0)));
            T.expect("verification is checked first: blank holder and pay(-1) print Verification failed.", "Verification failed.",
                    () -> out(() -> T.call(card("4111111111111111", ""), "pay", -1.0)));
        });

        T.test("PayPalPayment", "test_PayPalPayment_pay", "PayPalPayment.pay", () -> {
            T.method("PayPalPayment", "pay", 1);
            T.expect("pay(25.0) should print Paid 25.00 via PayPal account ali@example.com",
                    f("Paid %.2f via PayPal account ali@example.com", 25.0),
                    () -> out(() -> T.call(T.make("PayPalPayment", "ali@example.com"), "pay", 25.0)));
            T.expect("there is no upper limit: pay(50000.0)", f("Paid %.2f via PayPal account x@y.com", 50000.0),
                    () -> out(() -> T.call(T.make("PayPalPayment", "x@y.com"), "pay", 50000.0)));
            T.expect("a blank email should print Cannot connect.", "Cannot connect.",
                    () -> out(() -> T.call(T.make("PayPalPayment", " "), "pay", 10.0)));
            T.expect("a null email should print Cannot connect.", "Cannot connect.",
                    () -> out(() -> T.call(T.make("PayPalPayment", (Object) null), "pay", 10.0)));
        });

        T.test("Transaction", "test_Transaction_basics", "Transaction", () -> {
            Object t1 = T.make("Transaction", "DEPOSIT", 100.0);
            Thread.sleep(3);
            Object t2 = T.make("Transaction", "WITHDRAWAL", 40.0);
            T.expectTrue("getTransactionDate() must not be null", () -> T.call(t1, "getTransactionDate") != null);
            T.expectTrue("an older transaction must compare as smaller: t1.compareTo(t2) < 0",
                    () -> (Integer) T.call(t1, "compareTo", t2) < 0);
            T.expectTrue("t2.compareTo(t1) should be > 0", () -> (Integer) T.call(t2, "compareTo", t1) > 0);
            T.expect("t1.compareTo(t1) should be 0", 0, () -> T.call(t1, "compareTo", t1));
            T.expect("toString() should be <date> | DEPOSIT | 100.00", f("%s | %s | %.2f", T.call(t1, "getTransactionDate"), "DEPOSIT", 100.0),
                    () -> T.call(t1, "toString"));
        });

        T.test("Account", "test_Account_deposit", "Account constructor, deposit and toString", () -> {
            Object s = savings("S1", 10.0, 0.0);
            T.expect("getAccountNumber() should be S1", "S1", () -> T.call(s, "getAccountNumber"));
            T.expect("getBalance() should be the initial 10.0", 10.0, () -> T.call(s, "getBalance"));
            T.expect("the initial balance is not a transaction", 0, () -> transactions(s));
            T.expect("deposit must not print anything", "", () -> out(() -> T.call(s, "deposit", 15.5)));
            T.expect("deposit(15.5) on 10.0 should give balance 25.5", 25.5, () -> T.call(s, "getBalance"));
            T.expect("deposit should record one transaction", 1, () -> transactions(s));
            T.expectThrows("deposit(0) should throw IllegalArgumentException", IAE, () -> T.call(s, "deposit", 0.0));
            T.expectThrows("deposit(-3) should throw IllegalArgumentException", IAE, () -> T.call(s, "deposit", -3.0));
            T.expect("a refused deposit must not change the balance", 25.5, () -> T.call(s, "getBalance"));
            T.expect("toString() should be S1 | Balance: 25.50", f("S1 | Balance: %.2f", 25.5), () -> T.call(s, "toString"));
        });

        T.test("CheckingAccount", "test_CheckingAccount_withdraw", "CheckingAccount.withdraw with overdraft", () -> {
            Object c = checking("CHK-1", 100.0, 50.0);
            T.expect("withdraw(130) with balance 100 and overdraft 50 should print the Withdrew line",
                    f("Withdrew %.2f from CHK-1. New balance: %.2f", 130.0, -30.0),
                    () -> out(() -> T.expect("withdraw(130) should return true", true, () -> T.call(c, "withdraw", 130.0))));
            T.expect("the balance should be -30.0", -30.0, () -> T.call(c, "getBalance"));
            T.expect("withdraw(30) should be denied (-60 is beyond the overdraft)",
                    f("Withdrawal %.2f denied for CHK-1. Exceeds overdraft.", 30.0),
                    () -> out(() -> T.expect("withdraw(30) should return false", false, () -> T.call(c, "withdraw", 30.0))));
            T.expect("a denied withdraw must not change the balance", -30.0, () -> T.call(c, "getBalance"));
            T.expect("withdraw(20) (exactly down to -50) should succeed", true, () -> T.call(c, "withdraw", 20.0));
            T.expect("two WITHDRAWAL transactions should be recorded", 2, () -> transactions(c));
            T.expectThrows("withdraw(0) should throw IllegalArgumentException", IAE, () -> T.call(c, "withdraw", 0.0));
        });

        T.test("SavingsAccount", "test_SavingsAccount_withdraw", "SavingsAccount.withdraw without overdraft", () -> {
            Object s = savings("SAV-1", 100.0, 0.05);
            T.expect("withdraw(100.01) with balance 100 should be denied",
                    f("Withdrawal %.2f denied for SAV-1. Insufficient funds.", 100.01),
                    () -> out(() -> T.expect("withdraw(100.01) should return false", false, () -> T.call(s, "withdraw", 100.01))));
            T.expect("withdraw(60) should print the Withdrew line", f("Withdrew %.2f from SAV-1. New balance: %.2f", 60.0, 40.0),
                    () -> out(() -> T.call(s, "withdraw", 60.0)));
            T.expect("withdraw(40) (exactly the balance) should succeed", true, () -> T.call(s, "withdraw", 40.0));
            T.expect("the balance should be 0.0", 0.0, () -> T.call(s, "getBalance"));
            T.expect("withdraw(1) from an empty savings account should fail", false, () -> T.call(s, "withdraw", 1.0));
            T.expectThrows("withdraw(-1) should throw IllegalArgumentException", IAE, () -> T.call(s, "withdraw", -1.0));
        });

        T.test("SavingsAccount", "test_SavingsAccount_interest", "applyInterest", () -> {
            T.method("SavingsAccount", "applyInterest", 0);
            Object s = savings("SAV-1", 800.0, 0.05);
            T.expect("applyInterest() on 800 at 5% should print Applied interest 40.00 to SAV-1. New balance: 840.00",
                    f("Applied interest %.2f to SAV-1. New balance: %.2f", 40.0, 840.0), () -> out(() -> T.call(s, "applyInterest")));
            T.expectNear("the balance should be 840.0", 840.0, () -> T.call(s, "getBalance"), 1e-9);
            T.expect("the interest is recorded as one transaction (a DEPOSIT)", 1, () -> transactions(s));
            T.expectThrows("applyInterest() on a zero balance deposits 0, which throws IllegalArgumentException", IAE,
                    () -> T.call(savings("Z", 0.0, 0.05), "applyInterest"));
        });

        T.test("Account", "test_Account_history", "printTransactionHistory", () -> {
            T.method("Account", "printTransactionHistory", 0);
            Object c = checking("CHK-9", 0.0, 100.0);
            T.call(c, "deposit", 20.0);
            Thread.sleep(3);
            out(() -> T.call(c, "withdraw", 5.0));
            Thread.sleep(3);
            T.call(c, "deposit", 7.0);
            String printed = out(() -> T.call(c, "printTransactionHistory"));
            String[] lines = printed.split("\n");
            T.expect("the first line should be Transactions for CHK-9:", "Transactions for CHK-9:", () -> lines[0]);
            T.expect("there should be one line per transaction (3) after the header", 4, () -> lines.length);
            T.expectTrue("the transactions must be printed oldest first: DEPOSIT, WITHDRAWAL, DEPOSIT",
                    () -> lines.length == 4 && lines[1].contains("| DEPOSIT |") && lines[2].contains("| WITHDRAWAL |")
                            && lines[3].contains("| DEPOSIT |"));
            T.expectTrue("each line must be the transaction's toString(): ... | WITHDRAWAL | 5.00",
                    () -> lines.length == 4 && lines[2].endsWith(f("| WITHDRAWAL | %.2f", 5.0)));
        });

        T.test("Customer", "test_Customer_makePayment", "Customer.makePayment", () -> {
            T.method("Customer", "makePayment", 1);
            Object ali = T.make("RegularCustomer", "C1", "Ali");
            T.expectTrue("without a payment method, makePayment should print No payment method set for ... Ali", () -> {
                String o = out(() -> T.call(ali, "makePayment", 10.0));
                return o.startsWith("No payment method set for") && o.contains("Ali") && !o.contains("\n");
            });
            T.call(ali, "setPaymentMethod", card("1234 5678 9012 3456", "Ali Veli"));
            T.expect("a regular customer paying 50 by card",
                    f("Ali (fee %.2f) making payment: Paid %.2f with credit card ****-****-****-3456 (holder: Ali Veli)", 2.5, 50.0),
                    () -> out(() -> T.call(ali, "makePayment", 50.0)));
            T.expect("the fee is only shown, not added: makePayment(10000.0) is still accepted by the card",
                    f("Ali (fee %.2f) making payment: Paid %.2f with credit card ****-****-****-3456 (holder: Ali Veli)", 2.5, 10000.0),
                    () -> out(() -> T.call(ali, "makePayment", 10000.0)));
            Object ayse = T.make("PremiumCustomer", "C2", "Ayse");
            T.call(ayse, "setPaymentMethod", T.make("PayPalPayment", ""));
            T.expect("a premium customer with a blank PayPal email", f("Ayse (fee %.2f) making payment: Cannot connect.", 1.0),
                    () -> out(() -> T.call(ayse, "makePayment", 10.0)));
        });

        T.test("Customer", "test_Customer_state", "Customer fields, fees and accounts", () -> {
            Object cu = T.make("RegularCustomer", "C1", "Ali");
            T.expect("the customerId field should be C1", "C1", () -> T.field(cu, "customerId"));
            T.expect("the name field should be Ali", "Ali", () -> T.field(cu, "name"));
            T.expect("RegularCustomer fee should be 2.5", 2.5, () -> T.call(cu, "getTransactionFee"));
            T.expect("PremiumCustomer fee should be 1.0", 1.0, () -> T.call(T.make("PremiumCustomer", "C2", "Ayse"), "getTransactionFee"));
            Object a = checking("A", 1.0, 0.0);
            T.call(cu, "addAccount", a);
            T.call(cu, "addAccount", savings("B", 1.0, 0.0));
            T.expect("after two addAccount calls the customer should have 2 accounts", 2,
                    () -> ((Collection<?>) T.field(cu, "accounts")).size());
            Object pm = T.make("PayPalPayment", "a@b.c");
            T.call(cu, "setPaymentMethod", pm);
            T.expectSame("setPaymentMethod should store the method", pm, () -> T.field(cu, "paymentMethod"));
        });

        T.test("Bank", "test_Bank_customers", "Bank addCustomer and findCustomer", () -> {
            Object bank = T.make("Bank");
            Object ali = T.make("RegularCustomer", "C1", "Ali");
            Object ayse = T.make("PremiumCustomer", "C2", "Ayse");
            T.call(bank, "addCustomer", ali);
            T.call(bank, "addCustomer", ayse);
            T.expectSame("findCustomer(C1) should return Ali", ali, () -> T.call(bank, "findCustomer", "C1"));
            T.expectSame("findCustomer(C2) should return Ayse", ayse, () -> T.call(bank, "findCustomer", "C2"));
            T.expectNull("findCustomer(C9) should return null", () -> T.call(bank, "findCustomer", "C9"));
            T.expectNull("findCustomer on an empty bank should return null", () -> T.call(T.make("Bank"), "findCustomer", "C1"));
        });

        T.test("Bank", "test_Bank_accounts", "Bank openAccountForCustomer and findAccount", () -> {
            Object bank = T.make("Bank");
            Object ali = T.make("RegularCustomer", "C1", "Ali");
            T.call(bank, "addCustomer", ali);
            Object chk = checking("CHK-1", 100.0, 50.0);
            T.expect("opening CHK-1 for C1 should return true", true, () -> T.call(bank, "openAccountForCustomer", "C1", chk));
            T.expect("opening an account for a missing customer should return false", false,
                    () -> T.call(bank, "openAccountForCustomer", "C9", savings("SAV-9", 0.0, 0.0)));
            T.expectSame("findAccount(CHK-1) should return the opened account", chk, () -> T.call(bank, "findAccount", "CHK-1"));
            T.expectNull("findAccount(SAV-9) should return null (its customer did not exist)", () -> T.call(bank, "findAccount", "SAV-9"));
            T.expect("the customer should own the new account", 1, () -> ((Collection<?>) T.field(ali, "accounts")).size());
        });

        T.test("(code rules)", "test_Lab02_rules", "Lab02 code rules", () -> {
            T.expectFalse("only Lab02 may be public", T.isPublic("Bank") || T.isPublic("Account") || T.isPublic("Customer"));
            T.expectSilent("methods that are not said to print must not print", () -> {
                try {
                    Object bank = T.make("Bank");
                    Object cu = T.make("RegularCustomer", "C1", "Ali");
                    T.call(bank, "addCustomer", cu);
                    Object acc = checking("A", 10.0, 0.0);
                    T.call(bank, "openAccountForCustomer", "C1", acc);
                    T.call(bank, "findAccount", "A");
                    T.call(bank, "findCustomer", "C1");
                    T.call(acc, "deposit", 5.0);
                    T.call(acc, "getBalance");
                    T.call(acc, "toString");
                    T.call(cu, "getTransactionFee");
                    T.call(cu, "setPaymentMethod", card("1", "x"));
                } catch (Throwable ignored) {
                    // printing is what matters here
                }
            });
        });

        T.done();
    }
}
