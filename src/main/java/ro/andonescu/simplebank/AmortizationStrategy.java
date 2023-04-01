package ro.andonescu.simplebank;

import java.time.LocalDate;

public interface AmortizationStrategy {
    AmortizationScheduleEntry calculateEntry(int month, Loan loan, Money remainingBalance, LocalDate dueDate);
}