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

    public enum AmortizationType {
        EQUAL_MONTHLY_PAYMENTS,
        EQUAL_INTEREST
    }

    public static List<AmortizationScheduleEntry> generateAmortizationSchedule(Loan loan, AmortizationType amortizationType) {
        List<AmortizationScheduleEntry> schedule = new ArrayList<>();

        BigDecimal monthlyInterestRate = loan.getAnnualInterestRate().divide(new BigDecimal(1200), 10, RoundingMode.HALF_UP);
        BigDecimal divisor = BigDecimal.ONE.subtract(BigDecimal.ONE.divide((BigDecimal.ONE.add(monthlyInterestRate)).pow(loan.getTermInMonths()), 10, RoundingMode.HALF_UP));
        Money payment = new Money(loan.getPrincipal().getValue().multiply(monthlyInterestRate).divide(divisor, 2, RoundingMode.HALF_UP));

        Money remainingBalance = loan.getPrincipal();
        for (int month = 1; month <= loan.getTermInMonths(); month++) {
            Money interestPayment = new Money(remainingBalance.getValue().multiply(monthlyInterestRate).setScale(2, RoundingMode.HALF_UP));
            Money principalPayment;
            Money newPayment;

            if (amortizationType == AmortizationType.EQUAL_INTEREST) {
                principalPayment = new Money(loan.getPrincipal().getValue().divide(new BigDecimal(loan.getTermInMonths()), 2, RoundingMode.HALF_UP));
                newPayment = new Money(interestPayment.getValue().add(principalPayment.getValue()));
            } else {
                principalPayment = new Money(payment.getValue().subtract(interestPayment.getValue()).setScale(2, RoundingMode.HALF_UP));
                newPayment = payment;
            }

            remainingBalance = new Money(remainingBalance.getValue().subtract(principalPayment.getValue()).setScale(2, RoundingMode.HALF_UP));

            AmortizationScheduleEntry entry = new AmortizationScheduleEntry(month, newPayment, principalPayment, interestPayment, remainingBalance);
            schedule.add(entry);
        }

        return schedule;
    }

    public static void main(String[] args) {
        Loan loan = new Loan(new Money(new BigDecimal("10000")), new BigDecimal("5"), 2);
        List<AmortizationScheduleEntry> equalMonthlyPaymentsSchedule = generateAmortizationSchedule(loan, AmortizationType.EQUAL_MONTHLY_PAYMENTS);
        List<AmortizationScheduleEntry> equalInterestSchedule = generateAmortizationSchedule(loan, AmortizationType.EQUAL_INTEREST);

        System.out.println("Equal Monthly Payments:");
        for (AmortizationScheduleEntry entry : equalMonthlyPaymentsSchedule) {
            System.out.println(entry.toString());
        }

        System.out.println("\nEqual Interest:");
        for (AmortizationScheduleEntry entry : equalInterestSchedule) {
            System.out.println(entry.toString());
        }
    }
}
