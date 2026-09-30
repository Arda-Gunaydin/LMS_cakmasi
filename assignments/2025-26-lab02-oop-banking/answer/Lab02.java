import java.time.LocalDateTime;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.List;

public class Lab02 {
    public static void main(String[] args) {
   
        // test your code here
    }
}


// Implement interface, abstract classes, concrete classes according to instructions

interface PaymentMethod
{
    void pay(double amount);
}

 class CreditCardPayment implements PaymentMethod {
    private final String cardNumber;
    private final String cardHolderName;

    public CreditCardPayment(String cardNumber,String cardHolderName) {
        this.cardNumber=cardNumber;
        this.cardHolderName=cardHolderName;
    }

    @Override
    public void pay(double amount) {
        if (!verifySecurePayment())
        {
            System.out.println("Verification failed.");
            return;
        }
        if (!confirmTransaction(amount))
        {
            System.out.println("Transaction not confirmed.");
            return;
        }
        System.out.printf("Paid %.2f with credit card %s (holder: %s)%n", amount, maskCardNumber(), cardHolderName);
        
    }

    private boolean verifySecurePayment() {
        
        return cardHolderName != null &&!cardHolderName.trim().isEmpty();
    }

    private boolean confirmTransaction(double amount) {
        return amount>=0&&amount<= 10000.0;
    }

    private String maskCardNumber() {
        String digits= cardNumber ==null ? "" :cardNumber.replaceAll("\\s", "");
        if (digits.length() <= 4) return digits;
  
        return "****-****-****-" +digits.substring(digits.length()-4);
    }
}

class PayPalPayment implements PaymentMethod{
    private final String email;

    public PayPalPayment(String email)
    {
        this.email=email;
    }

    @Override
    public void pay(double amount)
    {
        if (!connectServer())
        {
            System.out.println("Cannot connect.");
            return;
        }
        if (retreiveInfo()==null)
        {
            System.out.println("No account info.");
            return;
        }
        System.out.printf("Paid %.2f via PayPal account %s%n", amount, email);
    }

    private boolean connectServer() {
        return email !=null && !email.trim().isEmpty();
    }

    private String retreiveInfo() {
        return "PayPalAccount(" +email+ ")";
    }
}



class Transaction implements Comparable<Transaction> {
    private final LocalDateTime transactionDate;
    private final String transactionType;
    private final double amount;

    public Transaction(String transactionType, double amount) {
        this.transactionDate=LocalDateTime.now();
        this.transactionType=transactionType;
        this.amount=amount;
    }

    public LocalDateTime getTransactionDate()
    {
        return transactionDate;
        
    }

    @Override
    public int compareTo(Transaction other) {
        return this.transactionDate.compareTo(other.transactionDate);
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %.2f", transactionDate, transactionType, amount);
    }
}



abstract class Account {
    protected final String accountNumber;
    protected double balance;
    protected final TreeSet<Transaction> transactions = new TreeSet<>();

    public Account(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }

    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit must be positive.");
        balance += amount;
        transactions.add(new Transaction("DEPOSIT", amount));
    }

    public abstract boolean withdraw(double amount);

    protected void record(String type, double amount) {
        transactions.add(new Transaction(type, amount));
    }

    public void printTransactionHistory() {
        System.out.println("Transactions for " + accountNumber + ":");
        for (Transaction t : transactions) System.out.println(t);
    }

    @Override
    public String toString() {
        return String.format("%s | Balance: %.2f", accountNumber, balance);
    }
}


class CheckingAccount extends Account {
    private final double overdraftLimit;

    public CheckingAccount(String accountNumber, double initialBalance, double overdraftLimit) {
        super(accountNumber, initialBalance);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public boolean withdraw(double amount)
    {
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal must be positive.");
        double allowed = balance + overdraftLimit;
        if (amount <= allowed) {
            balance -= amount;
            record("WITHDRAWAL", amount);
            System.out.printf("Withdrew %.2f from %s. New balance: %.2f%n", amount, accountNumber, balance);
            return true;
        } else {
            System.out.printf("Withdrawal %.2f denied for %s. Exceeds overdraft.%n", amount, accountNumber);
            return false;
        }
    }
}


class SavingsAccount extends Account {
    private final double interestRate;

    public SavingsAccount(String accountNumber, double initialBalance, double interestRate) {
        super(accountNumber, initialBalance);
        this.interestRate = interestRate;
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal must be positive.");
        if (amount <= balance) {
            balance -= amount;
            record("WITHDRAWAL", amount);
            System.out.printf("Withdrew %.2f from %s. New balance: %.2f%n", amount, accountNumber, balance);
            return true;
        } else {
            System.out.printf("Withdrawal %.2f denied for %s. Insufficient funds.%n", amount, accountNumber);
            return false;
        }
    }

    public void applyInterest() {
        double interest = balance * interestRate;
        deposit(interest);
        System.out.printf("Applied interest %.2f to %s. New balance: %.2f%n", interest, accountNumber, balance);
    }
}


 abstract class Customer {
    protected final String customerId;
    protected final String name;
    protected final List<Account> accounts =new ArrayList<>();
    protected PaymentMethod paymentMethod;

    public Customer(String customerId, String name) {
        this.customerId = customerId;
        this.name = name;
    }

    public void addAccount(Account account)
    {
        accounts.add(account);
        
    }
    public void setPaymentMethod(PaymentMethod method)
    { 
        this.paymentMethod =method;
        
    }

    public void makePayment(double amount)
    {
        if (paymentMethod == null)
        {
            System.out.println("No payment method set for"+ name);
            return;
        }
        System.out.printf("%s (fee %.2f) making payment: ", name, getTransactionFee());
        paymentMethod.pay(amount);
    }

    public abstract double getTransactionFee();
}
 class RegularCustomer extends Customer {
    
    public RegularCustomer(String customerId, String name)
    {
        super(customerId,name);
        
    }
    
    @Override
    public double getTransactionFee()
    {
        return 2.5;
    }
}

 class PremiumCustomer extends Customer {
    
    public PremiumCustomer(String customerId, String name)
    {
        super(customerId,name);
    }
    
    @Override
    public double getTransactionFee()
    {
        return 1.0;
    }
}

class Bank {
    private final List<Customer> customers=new ArrayList<>();
    private final java.util.HashMap<String, Account>accountsMap =new java.util.HashMap<>();

    public void addCustomer(Customer customer)
    {
        customers.add(customer);
        
    }

    public boolean openAccountForCustomer(String customerId, Account account)
    {
        Customer c=findCustomer(customerId);
        if (c == null) return false;
        c.addAccount(account);
        accountsMap.put(account.getAccountNumber(), account);
        return true;
    }

    public Account findAccount(String accountNumber)
    {
        return accountsMap.get(accountNumber);
        
    }

    public Customer findCustomer(String customerId)
    {
        for (Customer c :customers) if (c.customerId.equals(customerId)) return c;
        return null;
    }
}
