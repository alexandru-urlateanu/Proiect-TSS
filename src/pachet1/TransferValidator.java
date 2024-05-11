package pachet1;

import pachet1.BankAccount;

public interface TransferValidator {
    boolean validateTransfer(double amount, BankAccount fromAccount, BankAccount toAccount);
}
