public interface TransferValidator {
    boolean validateTransfer(double amount, BankAccount fromAccount, BankAccount toAccount);
}
