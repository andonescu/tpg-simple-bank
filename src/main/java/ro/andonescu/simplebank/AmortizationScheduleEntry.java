package ro.andonescu.simplebank;

import java.time.LocalDate;

public class AmortizationScheduleEntry {
    private final int month;
    private final Money principalPayment;
    private final Money interestPayment;
    private final Money remainingBalance;
    private final LocalDate dueDate;

    public AmortizationScheduleEntry(int month, Money principalPayment, Money interestPayment, Money remainingBalance, LocalDate dueDate) {
        this.month = month;
        this.principalPayment = principalPayment;
        this.interestPayment = interestPayment;
        this.remainingBalance = remainingBalance;
        this.dueDate = dueDate;
    }

    public Money getRemainingBalance() {
        return remainingBalance;
    }

    public Money getPrincipalPayment() {
        return principalPayment;
    }

    public Money getInterestPayment() {
        return interestPayment;
    }

    public int getMonth() {
        return month;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Money getPayment() {
        return new Money(interestPayment.getValue().add(principalPayment.getValue()));
    }

    @Override
    public String toString() {
        return "AmortizationScheduleEntry{" +
                "month=" + month +
                ", payment=" + getPayment() +
                ", principalPayment=" + principalPayment +
                ", interestPayment=" + interestPayment +
                ", remainingBalance=" + remainingBalance +
                ", dueDate=" + dueDate +
                '}';
    }
}
