import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class BankAccountTests {
    private BankAccount account;

    @Before
    public void setUp() {
        account = new BankAccount("123456");
        // Reset account state for each test
        account.logoutBefore();
    }

    ///Teste pentru autentificare și blocare cont
    @Test
    public void testLoginSuccess() {
        account.login("secret");
        assertTrue(account.isAuthenticated());
        assertEquals(List.of("Login successful"), account.getTransactionLog());
    }

    @Test
    public void testLoginFailure() {
        account.login("wrongpassword");
        assertFalse(account.isAuthenticated());
        assertEquals(1, account.failedLoginAttempts);
    }

    @Test
    public void testAccountLockAfterThreeFailedAttempts() {

        account.login("wrong");
        account.login("wrong");
        account.login("wrong");
        assertFalse(account.isAuthenticated());
        assertTrue(account.isLocked());
        assertEquals(List.of("Login attempted", "Login attempted", "Login attempted", "Account locked"), account.getTransactionLog());
    }

    ///Teste pentru tranzacții
    @Test
    public void testDepositWhenAuthenticated() {
        account.login("secret");
        double amount =100;
        account.deposit(amount);
        assertEquals(amount, account.getBalance(), 0.01);
        assertEquals(List.of("Login successful", "Deposited: "+amount), account.getTransactionLog());
    }

    @Test
    public void testDepositWhenNotAuthenticated() {
        double amount =100;
        account.deposit(amount);
        assertEquals(0, account.getBalance(), 0.01);
        assertEquals(List.of(), account.getTransactionLog());
    }

    @Test
    public void testWithdrawalWithSufficientFunds() {
        account.login("secret");
        double amountDeposit =200;
        double amountWithdraw =150;
        account.deposit(amountDeposit);
        account.withdraw(amountWithdraw);
        assertEquals(50, account.getBalance(), 0.01);
        assertEquals(List.of("Login successful", "Deposited: "+amountDeposit, "Withdrew: " + amountWithdraw), account.getTransactionLog());
    }

    @Test
    public void testFailedWithdrawalAttempt() {
        account.login("secret");
        account.withdraw(100);
        assertEquals(0, account.getBalance(), 0.01);
        assertTrue(account.getTransactionLog().contains("Failed withdrawal attempt"));
    }

    @Test
    public void testTransferBetweenAccounts() {
        String accountNumber = "654321";
        BankAccount anotherAccount = new BankAccount(accountNumber);
        double amountDeposit =200;
        double amountWithdraw =100;
        account.login("secret");
        anotherAccount.login("secret");
        account.deposit(amountDeposit);
        account.transfer(anotherAccount, amountWithdraw);
        assertEquals(100, account.getBalance(), 0.01);
        assertEquals(100, anotherAccount.getBalance(), 0.01);
        assertEquals(List.of("Login successful", "Deposited: "+amountDeposit, "Withdrew: "+amountWithdraw, "Transferred "+amountWithdraw+ " to "+ accountNumber), account.getTransactionLog());
    }

    ///Teste pentru boundry values
    @Test
    public void testDepositBoundary() {
        account.login("secret");
        account.deposit(0.01);
        assertEquals(0.01, account.getBalance(), 0.001);
        account.deposit(-0.01);
        assertEquals(0.01, account.getBalance(), 0.001); // Verificăm că nu s-a schimbat
    }

    @Test
    public void testWithdrawBoundary() {
        account.login("secret");
        account.deposit(100);
        account.withdraw(100);
        assertEquals(0, account.getBalance(), 0.01);
        account.withdraw(0.01);
        assertEquals(0, account.getBalance(), 0.01); // Soldul nu ar trebui să fie negativ
    }

    @Test
    public void testMultipleConditions() {
        account.login("secret");
        account.deposit(50);
        account.withdraw(25); // Condiție cu sold suficient
        assertEquals(25, account.getBalance(), 0.01);
        account.withdraw(30); // Condiție cu sold insuficient
        assertEquals(25, account.getBalance(), 0.01);
    }

    ///Teste pentru logarea tranzacțiilor
    @Test
    public void testTransactionLogAfterMultipleActions() {
        account.login("secret");
        double amountDeposit = 50;
        double amountWithdraw = 30;
        account.deposit(amountDeposit);
        account.withdraw(amountWithdraw);
        account.logout();
        assertEquals(List.of("Login successful", "Deposited: "+amountDeposit, "Withdrew: "+amountWithdraw, "Logout"), account.getTransactionLog());

    }
}