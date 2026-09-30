/**
 * CSE201 Lab 2 - OOP Review: Banking System (last year's lab, 2025-2026).
 * Only Lab02 is public. Do not add a package declaration.
 */
import java.time.LocalDateTime;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.List;

public class Lab02 {
    public static void main(String[] args) {
        try {
            Bank bank = new Bank();
            Customer ali = new RegularCustomer("C1", "Ali");
            bank.addCustomer(ali);
            bank.openAccountForCustomer("C1", new CheckingAccount("CHK-1", 100.0, 50.0));
            bank.openAccountForCustomer("C1", new SavingsAccount("SAV-1", 1000.0, 0.05));

            Account chk = bank.findAccount("CHK-1");
            chk.withdraw(130.0);
            chk.deposit(20.0);
            ((SavingsAccount) bank.findAccount("SAV-1")).applyInterest();
            chk.printTransactionHistory();
            System.out.println(chk);

            ali.makePayment(50.0);
            ali.setPaymentMethod(new CreditCardPayment("1234 5678 9012 3456", "Ali Veli"));
            ali.makePayment(50.0);
        } catch (UnsupportedOperationException e) {
            System.out.println();
            System.out.println("Stopped at a method that is not implemented yet:");
            System.out.println("  " + e.getStackTrace()[0]);
        }
    }
}

// Implement interface, abstract classes, concrete classes according to instructions
// (read the description: the declarations below are the ones it requires; fill in the bodies)

interface PaymentMethod
{
    void pay(double amount);
}

 class CreditCardPayment implements PaymentMethod {
    private final String cardNumber;
    private final String cardHolderName;

    public CreditCardPayment(String cardNumber,String cardHolderName) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void pay(double amount) {
        throw new UnsupportedOperationException("Not implemented");
    }

    private boolean verifySecurePayment() {
        throw new UnsupportedOperationException("Not implemented");
    }

    private boolean confirmTransaction(double amount) {
        throw new UnsupportedOperationException("Not implemented");
    }

    private String maskCardNumber() {
        throw new UnsupportedOperationException("Not implemented");
    }
}

class PayPalPayment implements PaymentMethod{
    private final String email;

    public PayPalPayment(String email) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void pay(double amount) {
        throw new UnsupportedOperationException("Not implemented");
    }

    private boolean connectServer() {
        throw new UnsupportedOperationException("Not implemented");
    }

    private String retreiveInfo() {
        throw new UnsupportedOperationException("Not implemented");
    }
}

class Transaction implements Comparable<Transaction> {
    private final LocalDateTime transactionDate;
    private final String transactionType;
    private final double amount;

    public Transaction(String transactionType, double amount) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public LocalDateTime getTransactionDate() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int compareTo(Transaction other) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not implemented");
    }
}

abstract class Account {
    protected final String accountNumber;
    protected double balance;
    protected final TreeSet<Transaction> transactions = new TreeSet<>();

    public Account(String accountNumber, double initialBalance) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public String getAccountNumber() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public double getBalance() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void deposit(double amount) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public abstract boolean withdraw(double amount);

    protected void record(String type, double amount) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void printTransactionHistory() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not implemented");
    }
}

class CheckingAccount extends Account {
    private final double overdraftLimit;

    public CheckingAccount(String accountNumber, double initialBalance, double overdraftLimit) {
        super(accountNumber, initialBalance);
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean withdraw(double amount) {
        throw new UnsupportedOperationException("Not implemented");
    }
}

class SavingsAccount extends Account {
    private final double interestRate;

    public SavingsAccount(String accountNumber, double initialBalance, double interestRate) {
        super(accountNumber, initialBalance);
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean withdraw(double amount) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void applyInterest() {
        throw new UnsupportedOperationException("Not implemented");
    }
}

 abstract class Customer {
    protected final String customerId;
    protected final String name;
    protected final List<Account> accounts =new ArrayList<>();
    protected PaymentMethod paymentMethod;

    public Customer(String customerId, String name) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void addAccount(Account account) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void setPaymentMethod(PaymentMethod method) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void makePayment(double amount) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public abstract double getTransactionFee();
}
 class RegularCustomer extends Customer {
    
    public RegularCustomer(String customerId, String name) {
        super(customerId,name);
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public double getTransactionFee() {
        throw new UnsupportedOperationException("Not implemented");
    }
}

 class PremiumCustomer extends Customer {
    
    public PremiumCustomer(String customerId, String name) {
        super(customerId,name);
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public double getTransactionFee() {
        throw new UnsupportedOperationException("Not implemented");
    }
}

class Bank {
    private final List<Customer> customers=new ArrayList<>();
    private final java.util.HashMap<String, Account>accountsMap =new java.util.HashMap<>();

    public void addCustomer(Customer customer) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public boolean openAccountForCustomer(String customerId, Account account) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public Account findAccount(String accountNumber) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public Customer findCustomer(String customerId) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
