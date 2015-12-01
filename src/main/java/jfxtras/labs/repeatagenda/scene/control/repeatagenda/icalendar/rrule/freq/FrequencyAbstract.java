package jfxtras.labs.repeatagenda.scene.control.repeatagenda.icalendar.rrule.freq;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import jfxtras.labs.repeatagenda.scene.control.repeatagenda.icalendar.rrule.byxxx.Rule;

public abstract class FrequencyAbstract implements Frequency {
    
    /** INTERVAL: (RFC 5545 iCalendar 3.3.10, page 40) number of frequency periods to pass before new appointment */
    // Uses lazy initialization of property because often interval stays as the default value of 1
    public IntegerProperty intervalProperty()
    {
        if (interval == null) interval = new SimpleIntegerProperty(this, "interval", _interval);
        return interval;
    }
    private IntegerProperty interval;
    @Override public Integer getInterval() { return (interval == null) ? _interval : interval.getValue(); }
    private int _interval = 1;
    @Override public void setInterval(Integer i)
    {
        if (i > 0)
        {
            if (interval == null)
            {
                _interval = i;
            } else
            {
                interval.set(i);
            }
        } else
        {
            throw new InvalidParameterException("INTERVAL can't be less than 1. (" + i + ")");
        }
    }
    @Override public void setInterval(String s)
    {
        Pattern p = Pattern.compile("(?<=INTERVAL=)[0-9]+");
        Matcher m = p.matcher(s);
        int i=0;
        while (m.find())
        {
            String token = m.group();
            if (i==0)
            {
                i = Integer.parseInt(token);
                if (i > 0)
                {
                    setInterval(i);
                } else
                {
                    throw new InvalidParameterException("INTERVAL must be greater than or equal to one");                    
                }
            } else
            {
                throw new InvalidParameterException("INTERVAL can only be specified once");
            }
        }
    }
    public Frequency withInterval(int interval) { setInterval(interval); return this; }

    /** BYxxx Rules 
     * Collection of BYxxx rules that modify frequency rule (see RFC 5545, iCalendar 3.3.10 Page 42)
     * The BYxxx rules must be applied in a specific order and can only be occur once */
    @Override public List<Rule> getRules() { return byRules; }
    private List<Rule> byRules = new ArrayList<Rule>();
    @Override public void addByRule(Rule byRule)
    {
//        if (byRules == null) byRules = new ArrayList<Rule>();
        boolean alreadyPresent = getRules().stream().anyMatch(a -> a.getClass() == byRule.getClass());
        if (alreadyPresent){
            throw new InvalidParameterException("Can't add BYxxx rule (" 
                    + byRule.getClass().getName() + ") more than once.");
        }
        getRules().add(byRule);
    }

    /** Time unit of last rule applied.  It represents the time span to apply future changes to the output stream of date/times
     * For example:
     * following FREQ=WEEKLY it is WEEKS
     * following FREQ=YEARLY it is YEARS
     * following FREQ=YEARLY;BYWEEKNO=20 it is WEEKS
     * following FREQ=YEARLY;BYMONTH=3 it is MONTHS
     * following FREQ=YEARLY;BYMONTH=3;BYDAY=TH it is DAYS
     */
    public ObjectProperty<ChronoUnit> getChronoUnit() { return chronoUnit; };
    private ObjectProperty<ChronoUnit> chronoUnit;
    private final ChronoUnit initialChronoUnit;
    public void setChronoUnit(ObjectProperty<ChronoUnit> chronoUnit)
    {
        switch (chronoUnit.get())
        {
        case DAYS:
        case MONTHS:
        case WEEKS:
        case YEARS:
            this.chronoUnit = chronoUnit;
            break;
        case HOURS:
        case MINUTES:
        case SECONDS:
            throw new RuntimeException("ChronoUnit not implemented: " + chronoUnit);
        default:
            throw new RuntimeException("Invalid ChronoUnit: " + chronoUnit);
        }
    }
    
    
    public FrequencyType getFrequencyType() { return frequencyType; }
    final private FrequencyType frequencyType;

    // CONSTRUCTOR
    public FrequencyAbstract(FrequencyType frequencyType, ObjectProperty<ChronoUnit> chronoUnit)
    {
        this.frequencyType = frequencyType;
        this.initialChronoUnit = chronoUnit.get();
        setChronoUnit(chronoUnit);
    }

    
    /** Resulting stream of start date/times by applying Frequency temporal adjuster and all, if any,
     * Rules.
     * Starts on startDateTime, which must be a valid event date/time, but not necessarily the
     * first date/time (DTSTART) in the sequence. A later startDateTime can be used to more efficiently
     * get to later dates in the stream. */
    public Stream<LocalDateTime> stream(LocalDateTime startDateTime)
    {
        getChronoUnit().set(initialChronoUnit); // start with Frequency ChronoUnit when making a stream
        Stream<LocalDateTime> stream = Stream.iterate(startDateTime, (a) -> { return a.with(getAdjuster()); });
        Iterator<Rule> rulesIterator = getRules().stream().sorted().iterator();
        while (rulesIterator.hasNext())
        {
            System.out.println("chronounit start:"  + getChronoUnit());
            Rule rule = rulesIterator.next();
            stream = rule.stream(stream, getChronoUnit(), startDateTime);
            System.out.println("chronounit end:"  + getChronoUnit());
        }
        return stream;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if((obj == null) || (obj.getClass() != getClass())) {
            return false;
        }
        Frequency testObj = (Frequency) obj;
        
        boolean intervalEquals = (getInterval() == null) ?
                (testObj.getInterval() == null) : getInterval().equals(testObj.getInterval());
//        Iterator<Rule> ruleIterator = getRules().iterator();
        boolean rulesEquals;
        if (getRules().size() == testObj.getRules().size())
        {
            List<Boolean> rulesEqualsArray = new ArrayList<Boolean>();
            for (int i=0; i<getRules().size(); i++)
            {
                boolean e = getRules().get(i).equals(testObj.getRules().get(i));
                rulesEqualsArray.add(e);
            }
            rulesEquals = rulesEqualsArray.stream().allMatch(a -> true );
        } else
        {
            rulesEquals = false;
        }
        System.out.println("frequency " + intervalEquals + " " + rulesEquals);
        return intervalEquals && rulesEquals;
    }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder("FREQ=" + getFrequencyType().toString());
        if (getInterval() > 1) builder.append(";INTERVAL=" + getInterval());
        return builder.toString();
    }
}