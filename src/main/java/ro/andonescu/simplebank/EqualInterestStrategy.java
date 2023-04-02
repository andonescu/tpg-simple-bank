package ro.andonescu.simplebank;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class EqualInterestStrategy implements AmortizationStrategy {
    @Override
    public AmortizationScheduleEntry calculateEntry(int month, Loan loan, Money remainingBalance, LocalDate dueDate) {
        BigDecimal monthlyInterestRate = loan.annualInterestRate().divide(new BigDecimal(1200), 10, RoundingMode.HALF_UP);

        Money interestPayment = new Money(loan.principal().getValue().multiply(monthlyInterestRate).setScale(2, RoundingMode.HALF_UP));
        Money principalPayment = new Money(loan.principal().getValue().divide(new BigDecimal(loan.getTermInMonths()), 2, RoundingMode.HALF_UP));
        Money payment = new Money(interestPayment.getValue().add(principalPayment.getValue()));

        if (month == loan.getTermInMonths()) {
            principalPayment = remainingBalance;
            payment = new Money(interestPayment.getValue().add(principalPayment.getValue()));
            remainingBalance = new Money(BigDecimal.ZERO);
        } else {
            remainingBalance = new Money(remainingBalance.getValue().subtract(principalPayment.getValue()).setScale(2, RoundingMode.HALF_UP));
        }

        return new AmortizationScheduleEntry(month,  principalPayment, interestPayment, remainingBalance, dueDate);
    }
}