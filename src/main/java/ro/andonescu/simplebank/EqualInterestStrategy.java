package ro.andonescu.simplebank;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class EqualInterestStrategy implements AmortizationStrategy {
    @Override
    public AmortizationScheduleEntry calculateEntry(int month, Loan loan, Money remainingBalance) {
        BigDecimal monthlyInterestRate = loan.getAnnualInterestRate().divide(new BigDecimal(1200), 10, RoundingMode.HALF_UP);

        Money interestPayment = new Money(loan.getPrincipal().getValue().multiply(monthlyInterestRate).setScale(2, RoundingMode.HALF_UP));
        Money principalPayment = new Money(loan.getPrincipal().getValue().divide(new BigDecimal(loan.getTermInMonths()), 2, RoundingMode.HALF_UP));
        Money payment = new Money(interestPayment.getValue().add(principalPayment.getValue()));
        remainingBalance = new Money(remainingBalance.getValue().subtract(principalPayment.getValue()).setScale(2, RoundingMode.HALF_UP));

        return new AmortizationScheduleEntry(month, payment, principalPayment, interestPayment, remainingBalance);
    }
}