import java.util.Objects;

/**
 * Неизменяемый баланс: сумма в минимальных единицах валюты
 * (копейки для RUB, центы для USD и т.д.) и валюта.
 * Любая операция возвращает НОВЫЙ объект Balance.
 */
public final class Balance {

    private final long amount;
    private final Currency currency;

    public Balance(final long amount, final Currency currency) {
        if (currency == null) {
            throw new IllegalArgumentException("Валюта не может быть null");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Баланс не может быть отрицательным: " + amount);
        }
        this.amount = amount;
        this.currency = currency;
    }

    // Прибавить сумму (в минимальных единицах)
    public Balance add(final long value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной: " + value);
        }
        return new Balance(Math.addExact(amount, value), currency);
    }

    // Вычесть сумму (в минимальных единицах)
    public Balance subtract(final long value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной: " + value);
        }
        if (value > amount) {
            throw new IllegalStateException("Недостаточно средств: на балансе " + this);
        }
        return new Balance(amount - value, currency);
    }

    // Конвертация в другую валюту по курсу из Currency (дробная часть отбрасывается)
    public Balance convertTo(final Currency target) {
        if (target == null) {
            throw new IllegalArgumentException("Целевая валюта не может быть null");
        }
        if (target == currency) {
            return this;
        }
        long converted = Math.multiplyExact(amount, currency.getPriceInRublesKopecks())
                / target.getPriceInRublesKopecks();
        return new Balance(converted, target);
    }

    public long getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Balance balance = (Balance) o;
        return amount == balance.amount && currency == balance.currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return String.format("%d.%02d %s", amount / 100, amount % 100, currency.getSymbol());
    }
}
