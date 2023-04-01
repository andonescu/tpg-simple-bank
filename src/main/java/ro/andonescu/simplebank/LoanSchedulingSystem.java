package ro.andonescu.simplebank;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

    public int compareTo(Money totalPayment) {
        return value.compareTo(totalPayment.value);
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

public class LoanSchedulingSystem {

    public LoanAccount createLoanAccount(Loan loan, AmortizationStrategy strategy, LocalDate startDate) {
        List<AmortizationScheduleEntry> schedule = generateAmortizationSchedule(loan, strategy, startDate);
        return new LoanAccount(loan, schedule);
    }

    public void makeRepayment(LoanAccount loanAccount, Money repaymentAmount, LocalDate repaymentDate) {
        // Check if the repayment date is on or after the due date of the current entry
        int index = loanAccount.getCurrentRepaymentIndex();
        AmortizationScheduleEntry currentEntry = loanAccount.getAmortizationSchedule().get(index);

        if (repaymentDate.isBefore(currentEntry.getDueDate())) {
            // Handle early repayment, if necessary
            return;
        }

        Money principalPayment = currentEntry.getPrincipalPayment();
        Money interestPayment = currentEntry.getInterestPayment();
        Money totalPayment = principalPayment.add(interestPayment);

        if (repaymentAmount.compareTo(totalPayment) >= 0) {
            loanAccount.setCurrentRepaymentIndex(index + 1);
        } else {
            // Recalculate the remaining schedule if necessary
            Loan remainingLoan = new Loan(currentEntry.getRemainingBalance(),
                    loanAccount.getLoan().getAnnualInterestRate(),
                    loanAccount.getLoan().getTermInMonths() - (index + 1));
            List<AmortizationScheduleEntry> newSchedule = generateAmortizationSchedule(
                    remainingLoan, new EqualMonthlyPaymentsStrategy(), repaymentDate);
            loanAccount.getAmortizationSchedule().subList(index + 1, loanAccount.getAmortizationSchedule().size()).clear();
            loanAccount.getAmortizationSchedule().addAll(newSchedule);
        }
    }


    public static List<AmortizationScheduleEntry> generateAmortizationSchedule(Loan loan, AmortizationStrategy strategy, LocalDate startDate) {
        List<AmortizationScheduleEntry> schedule = new ArrayList<>();
        Money remainingBalance = loan.getPrincipal();

        for (int month = 1; month <= loan.getTermInMonths(); month++) {
            LocalDate dueDate = startDate.plus(month, ChronoUnit.MONTHS);
            AmortizationScheduleEntry entry = strategy.calculateEntry(month, loan, remainingBalance, dueDate);
            schedule.add(entry);
            remainingBalance = entry.getRemainingBalance();
        }

        return schedule;
    }


    public static void main(String[] args) {
        Loan loan = new Loan(new Money(new BigDecimal("10000")), new BigDecimal("5"), 2);
        List<AmortizationScheduleEntry> equalMonthlyPaymentsSchedule = generateAmortizationSchedule(loan, new EqualMonthlyPaymentsStrategy(), LocalDate.now());
        List<AmortizationScheduleEntry> equalInterestSchedule = generateAmortizationSchedule(loan, new EqualInterestStrategy(), LocalDate.now());

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
