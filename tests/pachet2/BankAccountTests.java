package pachet2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pachet1.BankAccount;
import pachet1.TransferValidator;

public class BankAccountTests {

    private BankAccount bankAccount;
    @Mock
    private TransferValidator transferValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bankAccount = new BankAccount("123456789", "user", transferValidator);
    }

    @Test
    void deposit_ValidAmount_IncreasesBalance() {
        bankAccount.login("user", "secret");
        bankAccount.deposit(100.0, "ATM");
        assertEquals(100.0, bankAccount.getBalance());
    }

    @Test
    void withdraw_ValidAmount_DecreasesBalance() {
        bankAccount.login("user", "secret");
        bankAccount.deposit(200.0, "ATM");
        bankAccount.withdraw(50.0, "Supermarket");
        assertEquals(150.0, bankAccount.getBalance());
    }

    @Test
    void transfer_ValidAmountAndBalance_TransfersSuccessfully() {
        BankAccount toAccount = new BankAccount("987654321", "receiver", transferValidator);
        bankAccount.login("user", "secret");
        toAccount.login("receiver", "password");
        bankAccount.deposit(200.0, "ATM");
        bankAccount.transfer(toAccount, 100.0);
        assertEquals(100.0, bankAccount.getBalance());
        assertEquals(100.0, toAccount.getBalance());
    }

    @Test
    void transfer_InsufficientBalance_Fails() {
        BankAccount toAccount = new BankAccount("987654321", "receiver", transferValidator);
        bankAccount.login("user", "secret");
        toAccount.login("receiver", "password");
        bankAccount.deposit(50.0, "ATM");
        bankAccount.transfer(toAccount, 100.0);
        assertEquals(50.0, bankAccount.getBalance());
        assertEquals(0.0, toAccount.getBalance());
    }

    @Test
    void transfer_InvalidCredentials_Fails() {
        BankAccount toAccount = new BankAccount("987654321", "receiver", transferValidator);
        bankAccount.login("user", "wrongPassword");
        toAccount.login("receiver", "password");
        bankAccount.deposit(100.0, "ATM");
        bankAccount.transfer(toAccount, 50.0);
        assertEquals(100.0, bankAccount.getBalance());
        assertEquals(0.0, toAccount.getBalance());
    }

    @Test
    void login_Successful_LoginFlagTrue() {
        bankAccount.login("user", "secret");
        assertTrue(bankAccount.isAuthenticated());
    }

    @Test
    void login_UnsuccessfulAfterThreeAttempts_AccountLocked() {
        bankAccount.login("user", "wrongPassword");
        bankAccount.login("user", "wrongPassword");
        bankAccount.login("user", "wrongPassword");
        assertTrue(bankAccount.isLocked());
    }

    @Test
    void logout_AfterSuccessfulLogin_IsAuthenticatedFalse() {
        bankAccount.login("user", "secret");
        bankAccount.logout();
        assertFalse(bankAccount.isAuthenticated());
    }

    @Test
    void logout_AfterUnsuccessfulLogin_IsAuthenticatedFalse() {
        bankAccount.login("user", "wrongPassword");
        bankAccount.logoutBefore();
        assertFalse(bankAccount.isAuthenticated());
    }

    @Test
    void transactionLog_DepositRecordedInTransactionLog() {
        bankAccount.login("user", "secret");
        bankAccount.deposit(100.0, "ATM");
        assertEquals("Deposited: 100.0 from ATM", bankAccount.getTransactionLog().get(0));
    }

    @Test
    void transactionLog_WithdrawalRecordedInTransactionLog() {
        bankAccount.login("user", "secret");
        bankAccount.deposit(200.0, "ATM");
        bankAccount.withdraw(50.0, "Supermarket");
        assertEquals("Withdrew: 50.0 for Supermarket", bankAccount.getTransactionLog().get(1));
    }

    @Test
    void transactionLog_TransferRecordedInTransactionLog() {
        BankAccount toAccount = new BankAccount("987654321", "receiver", transferValidator);
        bankAccount.login("user", "secret");
        toAccount.login("receiver", "password");
        bankAccount.deposit(200.0, "ATM");
        bankAccount.transfer(toAccount, 100.0);
        assertEquals("Withdrew: 100.0 for Transfer to 987654321", bankAccount.getTransactionLog().get(1));
        assertEquals("Deposited: 100.0 from Transfer from 123456789", toAccount.getTransactionLog().get(0));
    }
}