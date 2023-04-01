package ro.andonescu.simplebank;

import java.util.List;

public class LoanAccount {
    private final Loan loan;
    private final List<AmortizationScheduleEntry> amortizationSchedule;
    private int currentRepaymentIndex;

    public LoanAccount(Loan loan, List<AmortizationScheduleEntry> amortizationSchedule) {
        this.loan = loan;
        this.amortizationSchedule = amortizationSchedule;
        this.currentRepaymentIndex = 0;
    }

    public Loan getLoan() {
        return loan;
    }

    public List<AmortizationScheduleEntry> getAmortizationSchedule() {
        return amortizationSchedule;
    }

    public int getCurrentRepaymentIndex() {
        return currentRepaymentIndex;
    }

    public void setCurrentRepaymentIndex(int currentRepaymentIndex) {
        this.currentRepaymentIndex = currentRepaymentIndex;
    }
}