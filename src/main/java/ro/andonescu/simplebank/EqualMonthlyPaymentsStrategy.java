package ro.andonescu.simplebank;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class EqualMonthlyPaymentsStrategy implements AmortizationStrategy {
    @Override
    public AmortizationScheduleEntry calculateEntry(int month, Loan loan, Money remainingBalance) {
        BigDecimal monthlyInterestRate = loan.getAnnualInterestRate().divide(new BigDecimal(1200), 10, RoundingMode.HALF_UP);
        BigDecimal divisor = BigDecimal.ONE.subtract(BigDecimal.ONE.divide((BigDecimal.ONE.add(monthlyInterestRate)).pow(loan.getTermInMonths()), 10, RoundingMode.HALF_UP));
        Money payment = new Money(loan.getPrincipal().getValue().multiply(monthlyInterestRate).divide(divisor, 2, RoundingMode.HALF_UP));

        Money interestPayment = new Money(remainingBalance.getValue().multiply(monthlyInterestRate).setScale(2, RoundingMode.HALF_UP));
        Money principalPayment = new Money(payment.getValue().subtract(interestPayment.getValue()).setScale(2, RoundingMode.HALF_UP));

        if (month == loan.getTermInMonths()) {
            principalPayment = remainingBalance;
            payment = new Money(interestPayment.getValue().add(principalPayment.getValue()));
            remainingBalance = new Money(BigDecimal.ZERO);
        } else {
            remainingBalance = new Money(remainingBalance.getValue().subtract(principalPayment.getValue()).setScale(2, RoundingMode.HALF_UP));
        }

        return new AmortizationScheduleEntry(month, payment, principalPayment, interestPayment, remainingBalance);
    }
}