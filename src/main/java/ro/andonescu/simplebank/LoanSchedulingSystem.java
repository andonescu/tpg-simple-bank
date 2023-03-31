package ro.andonescu.simplebank;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

class Loan {
    BigDecimal principal;
    BigDecimal annualInterestRate;
    int termInMonths;

    public Loan(BigDecimal principal, BigDecimal annualInterestRate, int termInYears) {
        this.principal = principal;
        this.annualInterestRate = annualInterestRate;
        this.termInMonths = termInYears * 12;
    }
}

class AmortizationScheduleEntry {
    int month;
    BigDecimal payment;
    BigDecimal principalPayment;
    BigDecimal interestPayment;
    BigDecimal remainingBalance;

    public AmortizationScheduleEntry(int month, BigDecimal payment, BigDecimal principalPayment,
                                     BigDecimal interestPayment, BigDecimal remainingBalance) {
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

        BigDecimal monthlyInterestRate = loan.annualInterestRate.divide(new BigDecimal(1200), 10, RoundingMode.HALF_UP);
        BigDecimal payment = loan.principal.multiply(monthlyInterestRate)
                .divide(new BigDecimal(1).subtract(monthlyInterestRate.add(BigDecimal.ONE).pow(-loan.termInMonths)), 2, RoundingMode.HALF_UP);

        BigDecimal remainingBalance = loan.principal;
        for (int month = 1; month <= loan.termInMonths; month++) {
            BigDecimal interestPayment = remainingBalance.multiply(monthlyInterestRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalPayment = payment.subtract(interestPayment).setScale(2, RoundingMode.HALF_UP);
            remainingBalance = remainingBalance.subtract(principalPayment).setScale(2, RoundingMode.HALF_UP);

            AmortizationScheduleEntry entry = new AmortizationScheduleEntry(month, payment, principalPayment, interestPayment, remainingBalance);
            schedule.add(entry);
        }

        return schedule;
    }

    public static void main(String[] args) {
        Loan loan = new Loan(new BigDecimal("10000"), new BigDecimal("5"), 2);
        List<AmortizationScheduleEntry> schedule = generateAmortizationSchedule(loan);

        for (AmortizationScheduleEntry entry : schedule) {
            System.out.println(entry.toString());
        }
    }
}
