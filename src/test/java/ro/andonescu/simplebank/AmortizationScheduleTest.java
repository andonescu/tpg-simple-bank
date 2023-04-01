package ro.andonescu.simplebank;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AmortizationScheduleTest {

    @Test
    void testGenerateAmortizationScheduleEqualMonthlyPayments() {
        Loan loan = new Loan(new Money(new BigDecimal("10000")), new BigDecimal("5"), 24);
        AmortizationStrategy strategy = new EqualMonthlyPaymentsStrategy();
        List<AmortizationScheduleEntry> schedule = LoanSchedulingSystem.generateAmortizationSchedule(loan, strategy);

        assertNotNull(schedule);
        assertEquals(24, schedule.size());
        assertEquals(new Money(BigDecimal.ZERO), schedule.get(schedule.size() - 1).getRemainingBalance());
    }

    @Test
    void testGenerateAmortizationScheduleEqualInterest() {
        Loan loan = new Loan(new Money(new BigDecimal("10000")), new BigDecimal("5"), 24);
        AmortizationStrategy strategy = new EqualInterestStrategy();
        List<AmortizationScheduleEntry> schedule = LoanSchedulingSystem.generateAmortizationSchedule(loan, strategy);

        assertNotNull(schedule);
        assertEquals(24, schedule.size());
        assertEquals(new Money(BigDecimal.ZERO), schedule.get(schedule.size() - 1).getRemainingBalance());
    }
}