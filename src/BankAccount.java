import java.util.ArrayList;
import java.util.List;
public class BankAccount {
    private double balance;
    private String accountNumber;
    public boolean isAuthenticated = false;
    public boolean isLocked = false;
    int failedLoginAttempts = 0;
    private List<String> transactionLog = new ArrayList<>();

    public BankAccount(String accountNumber) {
        this.accountNumber = accountNumber;
        this.balance = 0.0;
    }

    public void login(String password) {
        if ("secret".equals(password)) {
            isAuthenticated = true;
            transactionLog.add("Login successful");
        } else {
            failedLoginAttempts++;
            transactionLog.add("Login attempted");
            if (failedLoginAttempts >= 3) {
                lockAccount();
            }
        }
    }
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public boolean isLocked() {
        return isLocked;
    }
    public void logoutBefore() {
        isAuthenticated = false;
    }
    public void logout() {
        isAuthenticated = false;
        transactionLog.add("Logout");
    }

    private void lockAccount() {
        isAuthenticated = false;
        isLocked = true;
        transactionLog.add("Account locked");
    }

    public void deposit(double amount) {
        if (isAuthenticated && amount > 0) {
            balance += amount;
            transactionLog.add("Deposited: " + amount);
        }
    }

    public void withdraw(double amount) {
        if (isAuthenticated && amount > 0 && balance >= amount) {
            balance -= amount;
            transactionLog.add("Withdrew: " + amount);
        } else {
            transactionLog.add("Failed withdrawal attempt");
        }
    }

    public void transfer(BankAccount toAccount, double amount) {
        if (isAuthenticated && amount > 0 && balance >= amount) {
            this.withdraw(amount);
            toAccount.deposit(amount);
            transactionLog.add("Transferred " + amount + " to " + toAccount.accountNumber);
        } else {
            transactionLog.add("Failed transfer attempt");
        }
    }

    public double getBalance() {
        return balance;
    }

    public List<String> getTransactionLog() {
        return transactionLog;
    }
}