package pachet2;

import pachet1.BankAccount;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pachet1.TransferValidator;

import static org.mockito.Mockito.*;

public class BankAccountTests {
    private BankAccount account;
    @Mock private TransferValidator mockValidator;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        account = new BankAccount("123456", "user", mockValidator);
        account.logoutBefore();
    }

    //Authentication and account locking tests
    @Test
    public void testLoginSuccess() {
        account.login("user", "secret"); // Authentication with correct username and password
        assertTrue(account.isAuthenticated());
        assertEquals(List.of("Login successful"), account.getTransactionLog());
    }

    @Test
    public void testLoginFailure() {
        account.login("testUsername", "wrongpassword"); // Authentication with incorrect password
        assertFalse(account.isAuthenticated());
        assertEquals(1, account.getFailedLoginAttempts());

    }

    @Test
    public void testAccountLockAfterThreeFailedAttempts() {
        account.login("testUsername", "wrong"); // First failed authentication
        account.login("testUsername", "wrong"); // Second failed authentication
        account.login("testUsername", "wrong"); // Third failed authentication
        assertFalse(account.isAuthenticated());
        assertTrue(account.isLocked());
        assertEquals(List.of("Login attempted", "Login attempted",
                "Login attempted", "Account locked"), account.getTransactionLog());
    }

    ///Transactions tests
    @Test
    public void testDepositWhenAuthenticated() {
        account.login("user", "secret");
        double amount = 100;
        account.deposit(amount, "Salary"); // Add reason for deposit
        assertEquals(amount, account.getBalance(), 0.01);
        assertEquals(List.of("Login successful", "Deposited: " + amount + " from Salary"),
                account.getTransactionLog());
    }
    @Test
    public void testDepositWhenNotAuthenticated() {
        double amount = 100;
        // Authentication not happening before deposit
        account.deposit(amount, "Salary");
        assertEquals(0, account.getBalance(), 0.01);
        assertEquals(List.of(), account.getTransactionLog());
    }

    @Test
    public void testWithdrawalWithSufficientFunds() {
        account.login("user", "secret");
        double amountDeposit = 200;
        double amountWithdraw = 150;
        account.deposit(amountDeposit, "Salary");
        account.withdraw(amountWithdraw, "Groceries");
        assertEquals(50, account.getBalance(), 0.01);
        assertEquals(List.of("Login successful", "Deposited: " + amountDeposit +
                " from Salary", "Withdrew: " + amountWithdraw + " for Groceries"), account.getTransactionLog());
    }

    @Test
    public void testFailedWithdrawalAttempt() {
        account.login("user", "secret");
        account.withdraw(100, "Groceries"); // Add reason for withdraw
        assertEquals(0, account.getBalance(), 0.01);
        assertTrue(account.getTransactionLog().contains("Failed withdrawal attempt"));
    }



    //Boundary values tests
    @Test
    public void testDepositBoundary() {
        account.login("user", "secret");
        account.deposit(0.01, "Salary"); // Add deposit source
        assertEquals(0.01, account.getBalance(), 0.001);
        account.deposit(-0.01, "Salary"); // Deposit of negative balance, should not modify balance
        assertEquals(0.01, account.getBalance(), 0.001);
    }

    @Test
    public void testWithdrawBoundary() {
        account.login("user", "secret");
        account.deposit(100, "Salary"); // Add deposit source
        account.withdraw(100, "Groceries"); // Add withdraw reason
        assertEquals(0, account.getBalance(), 0.01);
        account.withdraw(0.01, "Rent"); // Add withdraw reason
        assertEquals(0, account.getBalance(), 0.01); // Balance should not be negative
    }

    @Test
    public void testMultipleConditions() {
        account.login("user", "secret");
        account.deposit(50, "Salary"); // Add deposit source
        account.withdraw(25, "Groceries"); // Enough balance condition
        assertEquals(25, account.getBalance(), 0.01);
        account.withdraw(30, "Shopping"); // Not enough balance condition
        assertEquals(25, account.getBalance(), 0.01);
    }

    @Test
    public void testTransactionLogAfterMultipleActions() {
        account.login("user", "secret");
        double amountDeposit = 50;
        double amountWithdraw = 30;
        account.deposit(amountDeposit, "Salary"); // Add deposit source
        account.withdraw(amountWithdraw, "Groceries"); // Add withdraw reason
        account.logout();
        assertEquals(List.of("Login successful", "Deposited: " + amountDeposit +
                        " from Salary", "Withdrew: " + amountWithdraw + " for Groceries", "Logout"),
                account.getTransactionLog());
    }

    @Test
    public void testTransferWhenValidatorAllows() {
        when(mockValidator.validateTransfer(anyDouble(), any(BankAccount.class), any(BankAccount.class)))
                .thenReturn(true);
        account.login("user", "secret");
        BankAccount anotherAccount = new BankAccount("654321", "anotherUser", mockValidator);
        anotherAccount.login("anotherUser", "secret");

        account.deposit(200.0, "Salary");
        account.transfer(anotherAccount, 100);
        assertEquals(100, account.getBalance(), 0.01);
        assertEquals(100, anotherAccount.getBalance(), 0.01);
        verify(mockValidator).validateTransfer(100, account, anotherAccount);
    }



    @Test
    public void testTransferWhenValidatorDenies() {
        when(mockValidator.validateTransfer(anyDouble(), any(BankAccount.class), any(BankAccount.class)))
                .thenReturn(false);
        account.login("user", "secret");
        BankAccount anotherAccount = new BankAccount("654321", "anotherUser", mockValidator);
        anotherAccount.login("anotherUser", "secret");
        account.deposit(200.0, "Initial deposit");
        account.transfer(anotherAccount, 100);
        assertEquals(200.0, account.getBalance(), 0.01);
        assertEquals(0, anotherAccount.getBalance(), 0.01);
        verify(mockValidator).validateTransfer(100, account, anotherAccount);
    }

}

//specificatie+aplicare cure 1
// cfg curs 2 + de verificat setul de teste sturcturale e minimal curs 2
//mutatie instalere pi +imbunatarie scor
//interpretare rezultate tool integraf couvrage si pit
//in clip iniante de teste de mutatie si dupa si ss cu ele in doc si prezentare
//comparare cu ait