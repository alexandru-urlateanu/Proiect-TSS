package pachet1;
import java.util.ArrayList;
import java.util.List;


public class BankAccount {
    private double balance;
    private String accountNumber;
    private String username;
    public boolean isAuthenticated = false;
    public boolean isLocked = false;
    int failedLoginAttempts = 0;
    private List<String> transactionLog = new ArrayList<>();

    private TransferValidator transferValidator;

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }
    public BankAccount(String accountNumber, String username, TransferValidator transferValidator) {
        this.accountNumber = accountNumber;
        this.username = username;
        this.transferValidator = transferValidator;
        this.balance = 0.0;
    }

    public void login(String username, String password) {
        if (isLocked) {
            transactionLog.add("Login attempted - Account is locked");
            return;
        }

        if ("user".equals(username) && "secret".equals(password)) {
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
    public void deposit(double amount, String source) {
        if (isAuthenticated && amount > 0) {
            balance += amount;
            transactionLog.add("Deposited: " + amount + " from " + source);
        }
    }

    public void withdraw(double amount, String reason) {
        if (isAuthenticated && amount > 0 && balance >= amount) {
            balance -= amount;
            transactionLog.add("Withdrew: " + amount + " for " + reason);
        } else {
            transactionLog.add("Failed withdrawal attempt");
        }
    }

    public void transfer(BankAccount toAccount, double amount) {
        if (isAuthenticated && amount > 0 && balance >= amount &&
                transferValidator.validateTransfer(amount, this, toAccount)) {
            this.balance -= amount;
            toAccount.balance += amount;
            transactionLog.add("Withdrew: " + amount + " for Transfer to " + toAccount.accountNumber);
            toAccount.transactionLog.add("Deposited: " + amount + " from Transfer from " + this.accountNumber);
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
