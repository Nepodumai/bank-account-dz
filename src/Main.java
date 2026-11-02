public class Main {

    public static void main(String[] args) {
        BankAccount rub = new BankAccount("RU-001", "Иван Иванов", Currency.RUB, 500_000);
        BankAccount usd = new BankAccount("US-001", "Иван Иванов", Currency.USD, 100_000);

        rub.deposit(300_000);
        System.out.println("Пополнили на 3000 руб: " + rub);

        rub.withdraw(50_000);
        System.out.println("Сняли 500 руб: " + rub);

        rub.transfer(usd, 96_000);
        System.out.println("Перевели 960 руб на долларовый счёт:");
        System.out.println("  " + rub);
        System.out.println("  " + usd);

        Balance before = usd.getBalance();
        Balance inEur = before.convertTo(Currency.EUR);
        System.out.println("Конвертация: " + before + " -> " + inEur + " (исходный баланс: " + before + ")");

        System.out.println("\nПроверка некорректных операций:");

        try {
            rub.deposit(-100);
        } catch (IllegalArgumentException e) {
            System.out.println("Отрицательная сумма: " + e.getMessage());
        }

        try {
            rub.withdraw(400_000);
        } catch (IllegalStateException e) {
            System.out.println("Недостаточно средств: " + e.getMessage());
        }

        try {
            rub.transfer(rub, 100);
        } catch (IllegalArgumentException e) {
            System.out.println("Перевод самому себе: " + e.getMessage());
        }

        try {
            rub.transfer(null, 100);
        } catch (IllegalArgumentException e) {
            System.out.println("Получатель null: " + e.getMessage());
        }

        try {
            rub.transfer(usd, 5);
        } catch (IllegalArgumentException e) {
            System.out.println("Слишком маленькая сумма после конвертации: " + e.getMessage());
        }

        rub.setWithdrawLimit(10_000);
        try {
            rub.withdraw(20_000);
        } catch (IllegalStateException e) {
            System.out.println("Превышение лимита: " + e.getMessage());
        }

        usd.freeze();
        try {
            rub.transfer(usd, 1_000);
        } catch (IllegalStateException e) {
            System.out.println("Заблокированный счёт: " + e.getMessage());
        }
        usd.unfreeze();

        try {
            new BankAccount("X-1", "Тест", null, 1_000);
        } catch (IllegalArgumentException e) {
            System.out.println("Валюта null: " + e.getMessage());
        }

        System.out.println("\nИтог:");
        System.out.println("  " + rub);
        System.out.println("  " + usd);
    }
}
