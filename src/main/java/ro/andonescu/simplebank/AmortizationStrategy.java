package ro.andonescu.simplebank;

public interface AmortizationStrategy {
    AmortizationScheduleEntry calculateEntry(int month, Loan loan, Money remainingBalance);
}