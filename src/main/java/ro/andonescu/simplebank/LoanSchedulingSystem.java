package ro.andonescu.simplebank;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

class Money {
    private final BigDecimal value;

    public Money(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

class Loan {
    private final Money principal;
    private final BigDecimal annualInterestRate;
    private final int termInMonths;

    public Loan(Money principal, BigDecimal annualInterestRate, int termInYears) {
        this.principal = principal;
        this.annualInterestRate = annualInterestRate;
        this.termInMonths = termInYears * 12;
    }

    public Money getPrincipal() {
        return principal;
    }

    public BigDecimal getAnnualInterestRate() {
        return annualInterestRate;
    }

    public int getTermInMonths() {
        return termInMonths;
    }
}

class AmortizationScheduleEntry {
    private final int month;
    private final Money payment;
    private final Money principalPayment;
    private final Money interestPayment;
    private final Money remainingBalance;

    public AmortizationScheduleEntry(int month, Money payment, Money principalPayment,
                                     Money interestPayment, Money remainingBalance) {
        this.month = month;
        this.payment = payment;
        this.principalPayment = principalPayment;
        this.interestPayment = interestPayment;
        this.remainingBalance = remainingBalance;
    }

    @Override
    public String toString() {
        return String.format("Month %d | Payment: %s | Principal: %s | Interest: %s | Remaining Balance: %s",
                month, payment, principalPayment, interestPayment, remainingBalance);
    }
}

public class LoanSchedulingSystem {

    public static List<AmortizationScheduleEntry> generateAmortizationSchedule(Loan loan) {
        List<AmortizationScheduleEntry> schedule = new ArrayList<>();

        BigDecimal monthlyInterestRate = loan.getAnnualInterestRate().divide(new BigDecimal(1200), 10, RoundingMode.HALF_UP);
        Money payment = new Money(loan.getPrincipal().getValue().multiply(monthlyInterestRate)
                .divide(BigDecimal.ONE.subtract(BigDecimal.ONE.divide(monthlyInterestRate.add(BigDecimal.ONE), loan.getTermInMonths(), RoundingMode.HALF_UP)), 2, RoundingMode.HALF_UP));

        Money remainingBalance = loan.getPrincipal();
        for (int month = 1; month <= loan.getTermInMonths(); month++) {
            Money interestPayment = new Money(remainingBalance.getValue().multiply(monthlyInterestRate).setScale(2, RoundingMode.HALF_UP));
            Money principalPayment = new Money(payment.getValue().subtract(interestPayment.getValue()).setScale(2, RoundingMode.HALF_UP));
            remainingBalance = new Money(remainingBalance.getValue().subtract(principalPayment.getValue()).setScale(2, RoundingMode.HALF_UP));

            AmortizationScheduleEntry entry = new AmortizationScheduleEntry(month, payment, principalPayment, interestPayment, remainingBalance);
            schedule.add(entry);
        }

        return schedule;
    }

    public static void main(String[] args) {
        Loan loan = new Loan(new Money(new BigDecimal("10000")), new BigDecimal("5"), 2);
        List<AmortizationScheduleEntry> schedule = generateAmortizationSchedule(loan);

        for (AmortizationScheduleEntry entry : schedule) {
            System.out.println(entry.toString());
        }
    }
}
