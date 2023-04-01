package ro.andonescu.simplebank;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
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

    public Money add(Money money) {
        return new Money(this.value.add(money.value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Money money = (Money) o;

        return new EqualsBuilder().append(true, value.compareTo(money.value) == 0).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(value).toHashCode();
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

    public Money getRemainingBalance() {
        return remainingBalance;
    }

    public Money getPrincipalPayment() {
        return principalPayment;
    }

    public Money getInterestPayment() {
        return interestPayment;
    }

    @Override
    public String toString() {
        return String.format("Month %d | Payment: %s | Principal: %s | Interest: %s | Remaining Balance: %s",
                month, payment, principalPayment, interestPayment, remainingBalance);
    }
}

public class LoanSchedulingSystem {

    public static List<AmortizationScheduleEntry> generateAmortizationSchedule(Loan loan, AmortizationStrategy strategy) {
        List<AmortizationScheduleEntry> schedule = new ArrayList<>();
        Money remainingBalance = loan.getPrincipal();

        for (int month = 1; month <= loan.getTermInMonths(); month++) {
            AmortizationScheduleEntry entry = strategy.calculateEntry(month, loan, remainingBalance);
            remainingBalance = entry.getRemainingBalance();
            schedule.add(entry);
        }

        return schedule;
    }


    public static void main(String[] args) {
        Loan loan = new Loan(new Money(new BigDecimal("10000")), new BigDecimal("5"), 2);
        List<AmortizationScheduleEntry> equalMonthlyPaymentsSchedule = generateAmortizationSchedule(loan, new EqualMonthlyPaymentsStrategy());
        List<AmortizationScheduleEntry> equalInterestSchedule = generateAmortizationSchedule(loan, new EqualInterestStrategy());

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
