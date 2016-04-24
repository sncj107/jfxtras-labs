package jfxtras.labs.icalendarfx.component;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import jfxtras.labs.icalendarfx.components.DaylightSavingTime;
import jfxtras.labs.icalendarfx.components.StandardTime;
import jfxtras.labs.icalendarfx.components.VComponentRepeatable;
import jfxtras.labs.icalendarfx.components.VEventNew;
import jfxtras.labs.icalendarfx.components.VJournal;
import jfxtras.labs.icalendarfx.components.VTodo;
import jfxtras.labs.icalendarfx.properties.component.recurrence.RecurrenceRule;
import jfxtras.labs.icalendarfx.properties.component.recurrence.Recurrences;
import jfxtras.labs.icalendarfx.properties.component.recurrence.rrule.RecurrenceRuleParameter;
import jfxtras.labs.icalendarfx.properties.component.recurrence.rrule.byxxx.ByDay;
import jfxtras.labs.icalendarfx.properties.component.recurrence.rrule.byxxx.ByDay.ByDayPair;
import jfxtras.labs.icalendarfx.properties.component.recurrence.rrule.byxxx.ByMonth;
import jfxtras.labs.icalendarfx.properties.component.recurrence.rrule.byxxx.ByMonthDay;
import jfxtras.labs.icalendarfx.properties.component.recurrence.rrule.byxxx.ByWeekNumber;
import jfxtras.labs.icalendarfx.properties.component.recurrence.rrule.frequency.Daily;
import jfxtras.labs.icalendarfx.properties.component.recurrence.rrule.frequency.Monthly;
import jfxtras.labs.icalendarfx.properties.component.recurrence.rrule.frequency.Weekly;
import jfxtras.labs.icalendarfx.properties.component.recurrence.rrule.frequency.Yearly;

/**
 * Test following components:
 * @see VEventNew
 * @see VTodo
 * @see VJournal
 * @see StandardTime
 * @see DaylightSavingTime
 * 
 * for the following properties:
 * @see Recurrences
 * @see RecurrenceRule
 * 
 * @author David Bal
 *
 */
public class RepeatableTest //extends Application
{
//    // Below Application code inserted as an attempt to catch listener-thrown exceptions - not successful
//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        // noop
//    }
//
//    @BeforeClass
//    public static void initJFX() {
//        Thread t = new Thread("JavaFX Init Thread")
//        {
//            @Override
//            public void run() {
//                Application.launch(RepeatableTest.class, new String[0]);
//            }
//        };
//        t.setDaemon(true);
//        t.start();
//    }


    @Test
    public void canBuildRepeatable() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
    {
        List<VComponentRepeatable<?>> components = Arrays.asList(
                new VEventNew()
                    .withRecurrences("RDATE;VALUE=DATE:20160504,20160508,20160509")
                    .withRecurrences(LocalDate.of(2016, 4, 15), LocalDate.of(2016, 4, 16), LocalDate.of(2016, 4, 17))
                    .withRecurrenceRule(new RecurrenceRuleParameter()
                        .withFrequency(new Daily()
                                .withInterval(4))),
                new VTodo()
                    .withRecurrences("RDATE;VALUE=DATE:20160504,20160508,20160509")
                    .withRecurrences(LocalDate.of(2016, 4, 15), LocalDate.of(2016, 4, 16), LocalDate.of(2016, 4, 17))
                    .withRecurrenceRule(new RecurrenceRuleParameter()
                        .withFrequency(new Daily()
                                .withInterval(4))),
                new VJournal()
                    .withRecurrences("RDATE;VALUE=DATE:20160504,20160508,20160509")
                    .withRecurrences(LocalDate.of(2016, 4, 15), LocalDate.of(2016, 4, 16), LocalDate.of(2016, 4, 17))
                    .withRecurrenceRule(new RecurrenceRuleParameter()
                        .withFrequency(new Daily()
                                .withInterval(4))),
                new DaylightSavingTime()
                    .withRecurrences("RDATE;VALUE=DATE:20160504,20160508,20160509")
                    .withRecurrences(LocalDate.of(2016, 4, 15), LocalDate.of(2016, 4, 16), LocalDate.of(2016, 4, 17))
                    .withRecurrenceRule(new RecurrenceRuleParameter()
                        .withFrequency(new Daily()
                                .withInterval(4))),
                new StandardTime()
                    .withRecurrences("RDATE;VALUE=DATE:20160504,20160508,20160509")
                    .withRecurrences(LocalDate.of(2016, 4, 15), LocalDate.of(2016, 4, 16), LocalDate.of(2016, 4, 17))
                    .withRecurrenceRule(new RecurrenceRuleParameter()
                        .withFrequency(new Daily()
                                .withInterval(4)))
                );
        
        List<LocalDate> expectedDates = new ArrayList<LocalDate>(Arrays.asList(
                LocalDate.of(2016, 4, 13) // DTSTART
              , LocalDate.of(2016, 4, 15) // 2nd RDATE
              , LocalDate.of(2016, 4, 16) // 2nd RDATE
              , LocalDate.of(2016, 4, 17) // 2nd RDATE and RRULE
              , LocalDate.of(2016, 4, 21) // RRULE
              , LocalDate.of(2016, 4, 25) // RRULE
              , LocalDate.of(2016, 4, 29) // RRULE
              , LocalDate.of(2016, 5, 3) // RRULE
              , LocalDate.of(2016, 5, 4) // 1st RDATE
              , LocalDate.of(2016, 5, 7) // RRULE
              , LocalDate.of(2016, 5, 8) // 1st RDATE
              , LocalDate.of(2016, 5, 9) // 1st RDATE
                ));
        
        for (VComponentRepeatable<?> builtComponent : components)
        {
            String componentName = builtComponent.componentType().toString();            
            String expectedContent = "BEGIN:" + componentName + System.lineSeparator() +
                    "RDATE;VALUE=DATE:20160504,20160508,20160509" + System.lineSeparator() +
                    "RDATE;VALUE=DATE:20160415,20160416,20160417" + System.lineSeparator() +
                    "RRULE:FREQ=DAILY;INTERVAL=4" + System.lineSeparator() +
                    "END:" + componentName;
                    
            VComponentRepeatable<?> parsedComponent = builtComponent
                    .getClass()
                    .getConstructor(String.class)
                    .newInstance(expectedContent);
            assertEquals(parsedComponent, builtComponent);
            assertEquals(expectedContent, builtComponent.toContentLines());
            
            builtComponent.setDateTimeStart(LocalDate.of(2016, 4, 13));
            List<Temporal> madeDates = builtComponent                    
                    .streamRecurrences(builtComponent.getDateTimeStart().getValue())
                    .limit(12)
                    .collect(Collectors.toList());
            assertEquals(expectedDates, madeDates);
        }
    }

    @Test
    public void canStreamRecurrences1()
    {
        VEventNew component = new VEventNew()
                .withRecurrenceRule("RRULE:FREQ=DAILY")
                .withDateTimeStart(LocalDate.of(2016, 4, 22));
        component.streamRecurrences(LocalDate.of(2016, 4, 22)).limit(10).forEach(System.out::println);
    }

    
    @Test //(expected = DateTimeException.class)
    @Ignore // can't catch exception in listener
    public void canHandleDTStartTypeChange()
    {
        VEventNew component = new VEventNew()
            .withDateTimeStart(LocalDate.of(1997, 3, 1))
            .withRecurrences("RDATE;VALUE=DATE:19970304,19970504,19970704,19970904");
//        Platform.runLater(() -> component.setDateTimeStart("20160302T223316Z"));      
        component.setDateTimeStart("20160302T223316Z"); // invalid
    }

    @Test (expected = DateTimeException.class)
    public void canCatchWrongDateType()
    {
        VEventNew component = new VEventNew()
                .withDateTimeStart(LocalDate.of(1997, 3, 1));
        ObservableList<Recurrences<? extends Temporal>> recurrences = FXCollections.observableArrayList();
        recurrences.add(new Recurrences<LocalDateTime>("20160228T093000"));
        component.setRecurrences(recurrences); // invalid        
    }

    @Test //(expected = DateTimeException.class)
    @Ignore // JUnit won't recognize exception - exception is thrown in listener is cause
    public void canCatchDifferentRepeatableTypes()
    {
        VEventNew builtComponent = new VEventNew()
                .withRecurrences("RDATE;VALUE=DATE:19970304,19970504,19970704,19970904");
        ObservableSet<ZonedDateTime> expectedValues = FXCollections.observableSet(
                ZonedDateTime.of(LocalDateTime.of(1996, 4, 4, 1, 0), ZoneId.of("Z")) );        
        builtComponent.getRecurrences().add(new Recurrences<ZonedDateTime>(expectedValues));
    }
    
    /*
     * STREAM RECURRENCES TESTS
     */
    
    /** tests converting ISO.8601.2004 date-time string to LocalDateTime */
    /** Tests daily stream with FREQ=YEARLY */
    @Test
    public void yearlyStreamTest1()
    {
        VEventNew e = new VEventNew()
                .withDateTimeStart(LocalDateTime.of(2015, 11, 9, 10, 0))
                .withRecurrenceRule(new RecurrenceRuleParameter()
                        .withFrequency(new Yearly()));
        List<Temporal> madeDates = e
                .streamRecurrences(e.getDateTimeStart().getValue())
                .limit(5)
                .collect(Collectors.toList());
        List<LocalDateTime> expectedDates = new ArrayList<LocalDateTime>(Arrays.asList(
                LocalDateTime.of(2015, 11, 9, 10, 0)
              , LocalDateTime.of(2016, 11, 9, 10, 0)
              , LocalDateTime.of(2017, 11, 9, 10, 0)
              , LocalDateTime.of(2018, 11, 9, 10, 0)
              , LocalDateTime.of(2019, 11, 9, 10, 0)
                ));
        assertEquals(expectedDates, madeDates);
        String expectedContent = "RRULE:FREQ=YEARLY";
        assertEquals(expectedContent, e.getRecurrenceRule().toContentLine());
    }
    
    /** Tests daily stream with FREQ=YEARLY;BYDAY=FR */
    @Test
    public void yearlyStreamTest2()
    {
        VEventNew e = new VEventNew()
                .withDateTimeStart(LocalDateTime.of(2015, 11, 6, 10, 0))
                .withRecurrenceRule(new RecurrenceRuleParameter()
                        .withFrequency(new Yearly()
                                .withByRules(new ByDay(DayOfWeek.FRIDAY))));
        List<Temporal> madeDates = e
                .streamRecurrences(e.getDateTimeStart().getValue())
                .limit(5)
                .collect(Collectors.toList());
        List<LocalDateTime> expectedDates = new ArrayList<LocalDateTime>(Arrays.asList(
                LocalDateTime.of(2015, 11, 6, 10, 0)
              , LocalDateTime.of(2015, 11, 13, 10, 0)
              , LocalDateTime.of(2015, 11, 20, 10, 0)
              , LocalDateTime.of(2015, 11, 27, 10, 0)
              , LocalDateTime.of(2015, 12, 4, 10, 0)
                ));
        assertEquals(expectedDates, madeDates);
        String expectedContent = "RRULE:FREQ=YEARLY;BYDAY=FR";
        assertEquals(expectedContent, e.getRecurrenceRule().toContentLine());
    }
    
    /** FREQ=YEARLY;BYDAY=TH;BYMONTH=6,7,8 */
    @Test
    public void yearlyStreamTest3()
    {
        VEventNew e = new VEventNew()
                .withDateTimeStart(LocalDateTime.of(1997, 6, 5, 9, 0))
                .withRecurrenceRule(new RecurrenceRuleParameter()
                        .withFrequency(new Yearly()
                                .withByRules(new ByDay(DayOfWeek.THURSDAY)
                                           , new ByMonth(Month.JUNE, Month.JULY, Month.AUGUST))));
        List<Temporal> madeDates = e
                .streamRecurrences(e.getDateTimeStart().getValue())
                .limit(20)
                .collect(Collectors.toList());
        List<LocalDateTime> expectedDates = new ArrayList<LocalDateTime>(Arrays.asList(
                LocalDateTime.of(1997, 6, 5, 9, 0)
              , LocalDateTime.of(1997, 6, 12, 9, 0)
              , LocalDateTime.of(1997, 6, 19, 9, 0)
              , LocalDateTime.of(1997, 6, 26, 9, 0)
              , LocalDateTime.of(1997, 7, 3, 9, 0)
              , LocalDateTime.of(1997, 7, 10, 9, 0)
              , LocalDateTime.of(1997, 7, 17, 9, 0)
              , LocalDateTime.of(1997, 7, 24, 9, 0)
              , LocalDateTime.of(1997, 7, 31, 9, 0)
              , LocalDateTime.of(1997, 8, 7, 9, 0)
              , LocalDateTime.of(1997, 8, 14, 9, 0)
              , LocalDateTime.of(1997, 8, 21, 9, 0)
              , LocalDateTime.of(1997, 8, 28, 9, 0)
              , LocalDateTime.of(1998, 6, 4, 9, 0)
              , LocalDateTime.of(1998, 6, 11, 9, 0)
              , LocalDateTime.of(1998, 6, 18, 9, 0)
              , LocalDateTime.of(1998, 6, 25, 9, 0)
              , LocalDateTime.of(1998, 7, 2, 9, 0)
              , LocalDateTime.of(1998, 7, 9, 9, 0)
              , LocalDateTime.of(1998, 7, 16, 9, 0)
                ));
        assertEquals(expectedDates, madeDates);
        String expectedContent = "RRULE:FREQ=YEARLY;BYMONTH=6,7,8;BYDAY=TH";
        assertEquals(expectedContent, e.getRecurrenceRule().toContentLine());
    }
    
    /** FREQ=YEARLY;BYMONTH=1,2 */
    @Test
    public void yearlyStreamTest4()
    {
        VEventNew e = new VEventNew()
                .withDateTimeStart(LocalDateTime.of(2015, 1, 6, 10, 0))
                .withRecurrenceRule(new RecurrenceRuleParameter()
                        .withFrequency(new Yearly()
                                .withByRules(new ByMonth(Month.JANUARY, Month.FEBRUARY))));
        List<Temporal> madeDates = e
                .streamRecurrences(e.getDateTimeStart().getValue())
                .limit(5)
                .collect(Collectors.toList());
        List<LocalDateTime> expectedDates = new ArrayList<LocalDateTime>(Arrays.asList(
                LocalDateTime.of(2015, 1, 6, 10, 0)
              , LocalDateTime.of(2015, 2, 6, 10, 0)
              , LocalDateTime.of(2016, 1, 6, 10, 0)
              , LocalDateTime.of(2016, 2, 6, 10, 0)
              , LocalDateTime.of(2017, 1, 6, 10, 0)
                ));
        assertEquals(expectedDates, madeDates);
        String expectedContent = "RRULE:FREQ=YEARLY;BYMONTH=1,2";
        assertEquals(expectedContent, e.getRecurrenceRule().toContentLine());
    }
    
    /** FREQ=YEARLY;BYMONTH=11;BYMONTHDAY=10 */
    @Test
    public void yearlyStreamTest5()
    {
        VEventNew e = new VEventNew()
                .withDateTimeStart(LocalDateTime.of(2015, 11, 10, 0, 0))
                .withRecurrenceRule(new RecurrenceRuleParameter()
                        .withFrequency(new Yearly()
                                .withByRules(new ByMonth(Month.NOVEMBER)
                                           , new ByMonthDay(10))));
        List<Temporal> madeDates = e
                .streamRecurrences(e.getDateTimeStart().getValue())
                .limit(5)
                .collect(Collectors.toList());
        List<LocalDateTime> expectedDates = new ArrayList<LocalDateTime>(Arrays.asList(
                LocalDateTime.of(2015, 11, 10, 0, 0)
              , LocalDateTime.of(2016, 11, 10, 0, 0)
              , LocalDateTime.of(2017, 11, 10, 0, 0)
              , LocalDateTime.of(2018, 11, 10, 0, 0)
              , LocalDateTime.of(2019, 11, 10, 0, 0)
                ));
        assertEquals(expectedDates, madeDates);
        String expectedContent = "RRULE:FREQ=YEARLY;BYMONTH=11;BYMONTHDAY=10";
        assertEquals(expectedContent, e.getRecurrenceRule().toContentLine());

    }
    
    /** FREQ=YEARLY;INTERVAL=4;BYMONTH=11;BYMONTHDAY=2,3,4,5,6,7,8;BYDAY=TU
     * (U.S. Presidential Election day) */
    @Test
    public void yearlyStreamTest6()
    {
        VEventNew e = new VEventNew()
                .withDateTimeStart(LocalDateTime.of(1996, 11, 5, 0, 0))
                .withRecurrenceRule(new RecurrenceRuleParameter()
                        .withFrequency(new Yearly()
                                .withInterval(4)
                                .withByRules(new ByMonth(Month.NOVEMBER)
                                           , new ByDay(DayOfWeek.TUESDAY)
                                           , new ByMonthDay(2,3,4,5,6,7,8))));
        List<Temporal> madeDates = e
                .streamRecurrences(e.getDateTimeStart().getValue())
                .limit(6)
                .collect(Collectors.toList());
        List<LocalDateTime> expectedDates = new ArrayList<LocalDateTime>(Arrays.asList(
                LocalDateTime.of(1996, 11, 5, 0, 0)
              , LocalDateTime.of(2000, 11, 7, 0, 0)
              , LocalDateTime.of(2004, 11, 2, 0, 0)
              , LocalDateTime.of(2008, 11, 4, 0, 0)
              , LocalDateTime.of(2012, 11, 6, 0, 0)
              , LocalDateTime.of(2016, 11, 8, 0, 0)
                ));
        assertEquals(expectedDates, madeDates);
        String expectedContent = "RRULE:FREQ=YEARLY;INTERVAL=4;BYMONTH=11;BYMONTHDAY=2,3,4,5,6,7,8;BYDAY=TU";
        assertEquals(expectedContent, e.getRecurrenceRule().toContentLine());
    }
    
    /** FREQ=YEARLY;BYDAY=20MO */
    @Test
    public void yearlyStreamTest7()
    {
        VEventNew e = new VEventNew()
                .withDateTimeStart(LocalDateTime.of(1997, 5, 19, 10, 0))
                .withRecurrenceRule(new RecurrenceRuleParameter()
                        .withFrequency(new Yearly()
                                .withByRules(new ByDay(new ByDayPair(DayOfWeek.MONDAY, 20)))));
        List<Temporal> madeDates = e
                .streamRecurrences(e.getDateTimeStart().getValue())
                .limit(3)
                .collect(Collectors.toList());
        List<LocalDateTime> expectedDates = new ArrayList<LocalDateTime>(Arrays.asList(
                LocalDateTime.of(1997, 5, 19, 10, 0)
              , LocalDateTime.of(1998, 5, 18, 10, 0)
              , LocalDateTime.of(1999, 5, 17, 10, 0)
                ));
        assertEquals(expectedDates, madeDates);
        String expectedContent = "RRULE:FREQ=YEARLY;BYDAY=20MO";
        assertEquals(expectedContent, e.getRecurrenceRule().toContentLine());
    }
    
    /** FREQ=YEARLY;BYWEEKNO=20;BYDAY=MO */
    @Test
    public void yearlyStreamTest8()
    {
//        Locale oldLocale = Locale.getDefault();
//        Locale.setDefault(Locale.FRANCE); // has Monday as first day of week system.  US is Sunday which causes an error.
        VEventNew e = new VEventNew()
                .withDateTimeStart(LocalDateTime.of(1997, 5, 12, 10, 0))
                .withRecurrenceRule(new RecurrenceRuleParameter()
                        .withFrequency(new Yearly()
                                .withByRules(new ByWeekNumber(20).withWeekStart(DayOfWeek.MONDAY)
                                           , new ByDay(DayOfWeek.MONDAY))));
        List<Temporal> madeDates = e
                .streamRecurrences(e.getDateTimeStart().getValue())
                .limit(5)
                .collect(Collectors.toList());
        List<LocalDateTime> expectedDates = new ArrayList<LocalDateTime>(Arrays.asList(
                LocalDateTime.of(1997, 5, 12, 10, 0)
              , LocalDateTime.of(1998, 5, 11, 10, 0)
              , LocalDateTime.of(1999, 5, 17, 10, 0)
              , LocalDateTime.of(2000, 5, 15, 10, 0)
              , LocalDateTime.of(2001, 5, 14, 10, 0)
                ));
        assertEquals(expectedDates, madeDates);
        String expectedContent = "RRULE:FREQ=YEARLY;BYWEEKNO=20;BYDAY=MO";
        assertEquals(expectedContent, e.getRecurrenceRule().toContentLine());
//        Locale.setDefault(oldLocale);
    }
    
    /** Tests daily stream with FREQ=MONTHLY */
    @Test
    public void monthlyStreamTest()
    {
        VEventNew e = new VEventNew()
                .withDateTimeStart(LocalDateTime.of(2015, 11, 9, 10, 0))
                .withRecurrenceRule(new RecurrenceRuleParameter()
                        .withFrequency(new Monthly()));
        List<Temporal> madeDates = e
                .streamRecurrences(e.getDateTimeStart().getValue())
                .limit(5)
                .collect(Collectors.toList());
        List<LocalDateTime> expectedDates = new ArrayList<LocalDateTime>(Arrays.asList(
                LocalDateTime.of(2015, 11, 9, 10, 0)
              , LocalDateTime.of(2015, 12, 9, 10, 0)
              , LocalDateTime.of(2016, 1, 9, 10, 0)
              , LocalDateTime.of(2016, 2, 9, 10, 0)
              , LocalDateTime.of(2016, 3, 9, 10, 0)
                ));
        assertEquals(expectedDates, madeDates);
        String expectedContent = "RRULE:FREQ=MONTHLY";
        assertEquals(expectedContent, e.getRecurrenceRule().toContentLine());
    }
    
    /** Tests daily stream with FREQ=MONTHLY;BYMONTHDAY=-2 */
    @Test
    public void monthlyStreamTest2()
    {
        VEventNew e = new VEventNew()
                .withDateTimeStart(LocalDateTime.of(2015, 11, 29, 10, 0))
                .withRecurrenceRule(new RecurrenceRuleParameter()
                        .withFrequency(new Monthly()
                                .withByRules(new ByMonthDay()
                                        .withDaysOfMonth(-2)))); // repeats 2nd to last day of month
        List<Temporal> madeDates = e
                .streamRecurrences(e.getDateTimeStart().getValue())
                .limit(5)
                .collect(Collectors.toList());
        List<LocalDateTime> expectedDates = new ArrayList<LocalDateTime>(Arrays.asList(
                LocalDateTime.of(2015, 11, 29, 10, 0)
              , LocalDateTime.of(2015, 12, 30, 10, 0)
              , LocalDateTime.of(2016, 1, 30, 10, 0)
              , LocalDateTime.of(2016, 2, 28, 10, 0)
              , LocalDateTime.of(2016, 3, 30, 10, 0)
                ));
        assertEquals(expectedDates, madeDates);
        String expectedContent = "RRULE:FREQ=MONTHLY;BYMONTHDAY=-2";
        assertEquals(expectedContent, e.getRecurrenceRule().toContentLine());
    }
    
    /** Tests daily stream with FREQ=MONTHLY;BYDAY=TU,WE,FR */
    @Test
    public void monthlyStreamTest3()
    {
        VEventNew e = new VEventNew()
                .withDateTimeStart(LocalDateTime.of(2015, 11, 9, 10, 0))
                .withRecurrenceRule(new RecurrenceRuleParameter()
                        .withFrequency(new Monthly()
                                .withByRules(new ByDay(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY))));
        List<Temporal> madeDates = e
                .streamRecurrences(e.getDateTimeStart().getValue())
                .limit(10)
                .collect(Collectors.toList());
        List<LocalDateTime> expectedDates = new ArrayList<LocalDateTime>(Arrays.asList(
                LocalDateTime.of(2015, 11, 10, 10, 0)
              , LocalDateTime.of(2015, 11, 11, 10, 0)
              , LocalDateTime.of(2015, 11, 13, 10, 0)
              , LocalDateTime.of(2015, 11, 17, 10, 0)
              , LocalDateTime.of(2015, 11, 18, 10, 0)
              , LocalDateTime.of(2015, 11, 20, 10, 0)
              , LocalDateTime.of(2015, 11, 24, 10, 0)
              , LocalDateTime.of(2015, 11, 25, 10, 0)
              , LocalDateTime.of(2015, 11, 27, 10, 0)
              , LocalDateTime.of(2015, 12, 1, 10, 0)
                ));
        assertEquals(expectedDates, madeDates);
        String expectedContent = "RRULE:FREQ=MONTHLY;BYDAY=TU,WE,FR";
        assertEquals(expectedContent, e.getRecurrenceRule().toContentLine());
    }
    
    /** Tests daily stream with FREQ=MONTHLY;BYDAY=-1SA */
    @Test
    public void monthlyStreamTest4()
    {
        VEventNew e = new VEventNew()
                .withDateTimeStart(LocalDateTime.of(2015, 11, 9, 10, 0))
                .withRecurrenceRule(new RecurrenceRuleParameter()
                        .withFrequency(new Monthly()
                                .withByRules(new ByDay(new ByDay.ByDayPair(DayOfWeek.SATURDAY, -1))))); // last Saturday in month
        List<Temporal> madeDates = e
                .streamRecurrences(e.getDateTimeStart().getValue())
                .limit(5)
                .collect(Collectors.toList());
        List<LocalDateTime> expectedDates = new ArrayList<LocalDateTime>(Arrays.asList(
                LocalDateTime.of(2015, 11, 28, 10, 0)
              , LocalDateTime.of(2015, 12, 26, 10, 0)
              , LocalDateTime.of(2016, 1, 30, 10, 0)
              , LocalDateTime.of(2016, 2, 27, 10, 0)
              , LocalDateTime.of(2016, 3, 26, 10, 0)
                ));
        assertEquals(expectedDates, madeDates);
        String expectedContent = "RRULE:FREQ=MONTHLY;BYDAY=-1SA";
        assertEquals(expectedContent, e.getRecurrenceRule().toContentLine());
    }
    
    /** FREQ=MONTHLY;BYDAY=FR;BYMONTHDAY=13 Every Friday the 13th, forever: */
    @Test
    public void monthlyStreamTest5()
    {
        VEventNew e = new VEventNew()
                .withDateTimeStart(LocalDateTime.of(1997, 6, 13, 10, 0))
                .withRecurrenceRule(new RecurrenceRuleParameter()
                        .withFrequency(new Monthly()
                                .withByRules(new ByDay(DayOfWeek.FRIDAY), new ByMonthDay(13))));
        List<Temporal> madeDates = e
                .streamRecurrences(e.getDateTimeStart().getValue())
                .limit(6)
                .collect(Collectors.toList());
        List<LocalDateTime> expectedDates = new ArrayList<LocalDateTime>(Arrays.asList(
                LocalDateTime.of(1997, 6, 13, 10, 0)
              , LocalDateTime.of(1998, 2, 13, 10, 0)
              , LocalDateTime.of(1998, 3, 13, 10, 0)
              , LocalDateTime.of(1998, 11, 13, 10, 0)
              , LocalDateTime.of(1999, 8, 13, 10, 0)
              , LocalDateTime.of(2000, 10, 13, 10, 0)
                ));
        assertEquals(expectedDates, madeDates);
        String expectedContent = "RRULE:FREQ=MONTHLY;BYMONTHDAY=13;BYDAY=FR";
        assertEquals(expectedContent, e.getRecurrenceRule().toContentLine());
        RecurrenceRuleParameter r = new RecurrenceRuleParameter("FREQ=MONTHLY;BYDAY=FR;BYMONTHDAY=13");
        assertEquals(r, e.getRecurrenceRule().getValue()); // verify order of parameters doesn't matter
    }
    
    /** Tests daily stream with FREQ=MONTHLY;BYMONTH=11,12;BYDAY=TU,WE,FR */
    @Test
    public void monthlyStreamTest6()
    {
        VEventNew e = new VEventNew()
                .withDateTimeStart(LocalDateTime.of(2015, 11, 3, 10, 0))
                .withRecurrenceRule(new RecurrenceRuleParameter()
                        .withFrequency(new Monthly()
                                .withByRules(new ByMonth(Month.NOVEMBER, Month.DECEMBER)
                                           , new ByDay(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY))));
        List<Temporal> madeDates = e
                .streamRecurrences(e.getDateTimeStart().getValue())
                .limit(13)
                .collect(Collectors.toList());
        List<LocalDateTime> expectedDates = new ArrayList<LocalDateTime>(Arrays.asList(
                LocalDateTime.of(2015, 11, 3, 10, 0)
              , LocalDateTime.of(2015, 11, 4, 10, 0)
              , LocalDateTime.of(2015, 11, 6, 10, 0)
              , LocalDateTime.of(2015, 11, 10, 10, 0)
              , LocalDateTime.of(2015, 11, 11, 10, 0)
              , LocalDateTime.of(2015, 11, 13, 10, 0)
              , LocalDateTime.of(2015, 11, 17, 10, 0)
              , LocalDateTime.of(2015, 11, 18, 10, 0)
              , LocalDateTime.of(2015, 11, 20, 10, 0)
              , LocalDateTime.of(2015, 11, 24, 10, 0)
              , LocalDateTime.of(2015, 11, 25, 10, 0)
              , LocalDateTime.of(2015, 11, 27, 10, 0)
              , LocalDateTime.of(2015, 12, 1, 10, 0)
                ));
        assertEquals(expectedDates, madeDates);
        String expectedContent = "RRULE:FREQ=MONTHLY;BYMONTH=11,12;BYDAY=TU,WE,FR";
        assertEquals(expectedContent, e.getRecurrenceRule().toContentLine());
    }
    
    /** FREQ=WEEKLY */
    @Test
    public void weeklyStreamTest1()
    {
        VEventNew e = new VEventNew()
                .withDateTimeStart(LocalDateTime.of(2015, 11, 9, 10, 0))
                .withRecurrenceRule(new RecurrenceRuleParameter()
                        .withFrequency(new Weekly()));
        List<Temporal> madeDates = e
                .streamRecurrences(e.getDateTimeStart().getValue())
                .limit(5)
                .collect(Collectors.toList());
        List<LocalDateTime> expectedDates = new ArrayList<LocalDateTime>(Arrays.asList(
                LocalDateTime.of(2015, 11, 9, 10, 0)
              , LocalDateTime.of(2015, 11, 16, 10, 0)
              , LocalDateTime.of(2015, 11, 23, 10, 0)
              , LocalDateTime.of(2015, 11, 30, 10, 0)
              , LocalDateTime.of(2015, 12, 7, 10, 0)
                ));
        assertEquals(expectedDates, madeDates);
        String expectedContent = "RRULE:FREQ=WEEKLY";
        assertEquals(expectedContent, e.getRecurrenceRule().toContentLine());
    }
    
    /** FREQ=WEEKLY;INTERVAL=2;BYDAY=MO,WE,FR */
    @Test
    public void weeklyStreamTest2()
    {
        VEventNew e = new VEventNew()
                .withDateTimeStart(LocalDateTime.of(2015, 11, 11, 10, 0))
                .withRecurrenceRule(new RecurrenceRuleParameter()
                        .withFrequency(new Weekly()
                                .withInterval(2)
                                .withByRules(new ByDay(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY))));
        List<Temporal> madeDates = e
                .streamRecurrences(e.getDateTimeStart().getValue())
                .limit(10)
                .collect(Collectors.toList());
        List<LocalDateTime> expectedDates = new ArrayList<LocalDateTime>(Arrays.asList(
                LocalDateTime.of(2015, 11, 11, 10, 0)
              , LocalDateTime.of(2015, 11, 13, 10, 0)
              , LocalDateTime.of(2015, 11, 23, 10, 0)
              , LocalDateTime.of(2015, 11, 25, 10, 0)
              , LocalDateTime.of(2015, 11, 27, 10, 0)
              , LocalDateTime.of(2015, 12, 7, 10, 0)
              , LocalDateTime.of(2015, 12, 9, 10, 0)
              , LocalDateTime.of(2015, 12, 11, 10, 0)
              , LocalDateTime.of(2015, 12, 21, 10, 0)
              , LocalDateTime.of(2015, 12, 23, 10, 0)
                ));
        assertEquals(expectedDates, madeDates);
        String expectedContent = "RRULE:FREQ=WEEKLY;INTERVAL=2;BYDAY=MO,WE,FR";
        assertEquals(expectedContent, e.getRecurrenceRule().toContentLine());
    }

    /** FREQ=WEEKLY;BYDAY=MO,WE,FR */
    @Test
    public void weeklyStreamTest3()
    {
        VEventNew e = new VEventNew()
            .withDateTimeStart(LocalDateTime.of(2015, 11, 7, 10, 0))
            .withRecurrenceRule(new RecurrenceRuleParameter()
                    .withFrequency(new Weekly()
                            .withByRules(new ByDay(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY))));
        List<Temporal> madeDates = e
                .streamRecurrences(e.getDateTimeStart().getValue())
                .limit(5)
                .collect(Collectors.toList());
        List<LocalDateTime> expectedDates = new ArrayList<LocalDateTime>(Arrays.asList(
                LocalDateTime.of(2015, 11, 9, 10, 0)
              , LocalDateTime.of(2015, 11, 11, 10, 0)
              , LocalDateTime.of(2015, 11, 13, 10, 0)
              , LocalDateTime.of(2015, 11, 16, 10, 0)
              , LocalDateTime.of(2015, 11, 18, 10, 0)
                ));
        assertEquals(expectedDates, madeDates);
        String expectedContent = "RRULE:FREQ=WEEKLY;BYDAY=MO,WE,FR";
        assertEquals(expectedContent, e.getRecurrenceRule().toContentLine());
    }
    
    /** FREQ=WEEKLY;INTERVAL=2;COUNT=11;BYDAY=MO,WE,FR */
    @Test
    public void canStreamWeekly4()
    {
        VEventNew e = new VEventNew()
                .withDateTimeStart(LocalDateTime.of(2015, 11, 11, 10, 0))
                .withRecurrenceRule(new RecurrenceRuleParameter()
                        .withFrequency(new Weekly()
                                .withInterval(2)
                                .withByRules(new ByDay(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)))
                        .withCount(11));
        List<Temporal> madeDates = e
                .streamRecurrences(e.getDateTimeStart().getValue())
                .collect(Collectors.toList());
        List<LocalDateTime> expectedDates = new ArrayList<LocalDateTime>(Arrays.asList(
                LocalDateTime.of(2015, 11, 11, 10, 0)
              , LocalDateTime.of(2015, 11, 13, 10, 0)
              , LocalDateTime.of(2015, 11, 23, 10, 0)
              , LocalDateTime.of(2015, 11, 25, 10, 0)
              , LocalDateTime.of(2015, 11, 27, 10, 0)
              , LocalDateTime.of(2015, 12, 7, 10, 0)
              , LocalDateTime.of(2015, 12, 9, 10, 0)
              , LocalDateTime.of(2015, 12, 11, 10, 0)
              , LocalDateTime.of(2015, 12, 21, 10, 0)
              , LocalDateTime.of(2015, 12, 23, 10, 0)
              , LocalDateTime.of(2015, 12, 25, 10, 0)
                ));
        assertEquals(expectedDates, madeDates);
        String expectedContent = "RRULE:FREQ=WEEKLY;INTERVAL=2;COUNT=11;BYDAY=MO,WE,FR";
        assertEquals(expectedContent, e.getRecurrenceRule().toContentLine());
    }
    
    @Test // tests starting on Sunday (1st day of week) with other day of the week
    public void canStreamWeekly5()
    {
        VEventNew e = new VEventNew()
            .withDateTimeStart(LocalDateTime.of(2016, 1, 3, 5, 0))
            .withRecurrenceRule(new RecurrenceRuleParameter()
                    .withFrequency(new Weekly()
                            .withByRules(new ByDay(DayOfWeek.SUNDAY, DayOfWeek.WEDNESDAY)))); 
        List<Temporal> madeDates = e
                .streamRecurrences(e.getDateTimeStart().getValue())
                .limit(10)
                .collect(Collectors.toList());
        List<LocalDateTime> expectedDates = new ArrayList<LocalDateTime>(Arrays.asList(
                LocalDateTime.of(2016, 1, 3, 5, 0)
              , LocalDateTime.of(2016, 1, 6, 5, 0)
              , LocalDateTime.of(2016, 1, 10, 5, 0)
              , LocalDateTime.of(2016, 1, 13, 5, 0)
              , LocalDateTime.of(2016, 1, 17, 5, 0)
              , LocalDateTime.of(2016, 1, 20, 5, 0)
              , LocalDateTime.of(2016, 1, 24, 5, 0)
              , LocalDateTime.of(2016, 1, 27, 5, 0)
              , LocalDateTime.of(2016, 1, 31, 5, 0)
              , LocalDateTime.of(2016, 2, 3, 5, 0)
                ));
        assertEquals(expectedDates, madeDates);
        String expectedContent = "RRULE:FREQ=WEEKLY;BYDAY=SU,WE";
        assertEquals(expectedContent, e.getRecurrenceRule().toContentLine());
    }
    
    @Test
    public void canStreamWeeklyZoned()
    {
        VEventNew e = new VEventNew()
                .withDateTimeStart(ZonedDateTime.of(LocalDateTime.of(2015, 11, 9, 10, 0), ZoneId.of("America/Los_Angeles")))
                .withRecurrenceRule(new RecurrenceRuleParameter()
                        .withFrequency(new Weekly()
                                .withByRules(new ByDay(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY))));
        List<Temporal> madeDates = e
                .streamRecurrences(e.getDateTimeStart().getValue())
                .limit(10)
                .collect(Collectors.toList());
        List<ZonedDateTime> expectedDates = new ArrayList<>(Arrays.asList(
                ZonedDateTime.of(LocalDateTime.of(2015, 11, 9, 10, 0), ZoneId.of("America/Los_Angeles"))
              , ZonedDateTime.of(LocalDateTime.of(2015, 11, 11, 10, 0), ZoneId.of("America/Los_Angeles"))
              , ZonedDateTime.of(LocalDateTime.of(2015, 11, 13, 10, 0), ZoneId.of("America/Los_Angeles"))
              , ZonedDateTime.of(LocalDateTime.of(2015, 11, 16, 10, 0), ZoneId.of("America/Los_Angeles"))
              , ZonedDateTime.of(LocalDateTime.of(2015, 11, 18, 10, 0), ZoneId.of("America/Los_Angeles"))
              , ZonedDateTime.of(LocalDateTime.of(2015, 11, 20, 10, 0), ZoneId.of("America/Los_Angeles"))
              , ZonedDateTime.of(LocalDateTime.of(2015, 11, 23, 10, 0), ZoneId.of("America/Los_Angeles"))
              , ZonedDateTime.of(LocalDateTime.of(2015, 11, 25, 10, 0), ZoneId.of("America/Los_Angeles"))
              , ZonedDateTime.of(LocalDateTime.of(2015, 11, 27, 10, 0), ZoneId.of("America/Los_Angeles"))
              , ZonedDateTime.of(LocalDateTime.of(2015, 11, 30, 10, 0), ZoneId.of("America/Los_Angeles"))
                ));
        assertEquals(expectedDates, madeDates);
        String expectedContent = "RRULE:FREQ=WEEKLY;BYDAY=MO,WE,FR";
        assertEquals(expectedContent, e.getRecurrenceRule().toContentLine());
    }
    
    /** Tests daily stream with FREQ=DAILY */
    @Test
    public void dailyStreamTest1()
    {
        VEventNew e = new VEventNew()
                .withDateTimeStart(LocalDateTime.of(2015, 11, 9, 10, 0))
                .withRecurrenceRule(new RecurrenceRuleParameter()
                        .withFrequency(new Daily()));
        List<Temporal> madeDates = e
                .streamRecurrences(e.getDateTimeStart().getValue())
                .limit(5)
                .collect(Collectors.toList());
        List<LocalDateTime> expectedDates = new ArrayList<LocalDateTime>(Arrays.asList(
                LocalDateTime.of(2015, 11, 9, 10, 0)
              , LocalDateTime.of(2015, 11, 10, 10, 0)
              , LocalDateTime.of(2015, 11, 11, 10, 0)
              , LocalDateTime.of(2015, 11, 12, 10, 0)
              , LocalDateTime.of(2015, 11, 13, 10, 0)
                ));
        assertEquals(expectedDates, madeDates);
        String expectedContent = "RRULE:FREQ=DAILY";
        assertEquals(expectedContent, e.getRecurrenceRule().toContentLine());

    }
}
