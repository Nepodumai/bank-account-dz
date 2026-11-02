public class BankAccount {

    private final String accountNumber;
    private final String ownerName;
    private Balance balance;
    private long withdrawLimit;   // лимит списания за одну операцию (в минимальных единицах валюты счёта)
    private boolean frozen;       // заблокированный счёт не участвует в операциях

    public BankAccount(final String accountNumber,
                       final String ownerName,
                       final Currency currency,
                       final long withdrawLimit) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Номер счёта не может быть пустым");
        }
        if (ownerName == null || ownerName.isBlank()) {
            throw new IllegalArgumentException("Имя владельца не может быть пустым");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Валюта не может быть null");
        }
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = new Balance(0, currency);
        setWithdrawLimit(withdrawLimit);
        this.frozen = false;
    }

    public void deposit(final long amount) {
        checkNotFrozen();
        balance = balance.add(amount);
    }

    public void withdraw(final long amount) {
        checkNotFrozen();
        checkWithdrawLimit(amount);
        balance = balance.subtract(amount);
    }

    /**
     * Перевод amount (в валюте ЭТОГО счёта) на счёт target.
     * Если валюты разные, сумма конвертируется.
     * Сначала всё проверяем и считаем, потом меняем счета,
     * поэтому при ошибке оба счёта остаются прежними.
     */
    public void transfer(final BankAccount target, final long amount) {
        if (target == null) {
            throw new IllegalArgumentException("Счёт получателя не может быть null");
        }
        if (target == this) {
            throw new IllegalArgumentException("Нельзя переводить деньги на тот же счёт");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма перевода должна быть положительной: " + amount);
        }
        checkNotFrozen();
        target.checkNotFrozen();
        checkWithdrawLimit(amount);

        Balance newSourceBalance = balance.subtract(amount);
        long received = new Balance(amount, balance.getCurrency())
                .convertTo(target.balance.getCurrency())
                .getAmount();
        Balance newTargetBalance = target.balance.add(received);

        balance = newSourceBalance;
        target.balance = newTargetBalance;
    }

    public void freeze() {
        frozen = true;
    }

    public void unfreeze() {
        frozen = false;
    }

    public void setWithdrawLimit(final long withdrawLimit) {
        if (withdrawLimit <= 0) {
            throw new IllegalArgumentException("Лимит должен быть положительным: " + withdrawLimit);
        }
        this.withdrawLimit = withdrawLimit;
    }

    private void checkWithdrawLimit(final long amount) {
        if (amount > withdrawLimit) {
            throw new IllegalStateException("Превышен лимит списания: " + amount + " > " + withdrawLimit);
        }
    }

    private void checkNotFrozen() {
        if (frozen) {
            throw new IllegalStateException("Счёт " + accountNumber + " заблокирован");
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Balance getBalance() {
        return balance;
    }

    public long getWithdrawLimit() {
        return withdrawLimit;
    }

    public boolean isFrozen() {
        return frozen;
    }

    @Override
    public String toString() {
        return "BankAccount{" + accountNumber + ", " + ownerName + ", balance=" + balance
                + ", limit=" + withdrawLimit + ", frozen=" + frozen + "}";
    }
}
