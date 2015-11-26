package jfxtras.labs.repeatagenda.scene.control.repeatagenda.icalendar;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

/**
 * Parent calendar component, VEVENT
 * Defined in RFC 5545 iCalendar 3.6.1, page 52.
 * 
 * The status of following component properties from RFC 5545:
 * 
       3.8.1.  Descriptive Component Properties  . . . . . . . . . .  81
         3.8.1.1.  Attachment  . . . . . . . . . . . . . . . . . . .  81 - NO (from VComponent)
         3.8.1.2.  Categories  . . . . . . . . . . . . . . . . . . .  82 - Yes (from VComponent)
         3.8.1.3.  Classification  . . . . . . . . . . . . . . . . .  83 - TODO (from VComponent)
         3.8.1.4.  Comment . . . . . . . . . . . . . . . . . . . . .  84 - Yes (from VComponent)
         3.8.1.5.  Description . . . . . . . . . . . . . . . . . . .  85 - Yes
         3.8.1.6.  Geographic Position . . . . . . . . . . . . . . .  87 - NO
         3.8.1.7.  Location  . . . . . . . . . . . . . . . . . . . .  88 - Yes
         3.8.1.8.  Percent Complete  . . . . . . . . . . . . . . . .  89 - NO
         3.8.1.9.  Priority  . . . . . . . . . . . . . . . . . . . .  90 - NO
         3.8.1.10. Resources . . . . . . . . . . . . . . . . . . . .  92 - NO (from VComponent)
         3.8.1.11. Status  . . . . . . . . . . . . . . . . . . . . .  93 - TODO (from VComponent)
         3.8.1.12. Summary . . . . . . . . . . . . . . . . . . . . .  94 - Yes (from VComponent)
       3.8.2.  Date and Time Component Properties  . . . . . . . . .  95
         3.8.2.1.  Date-Time Completed . . . . . . . . . . . . . . .  95 - NO
         3.8.2.2.  Date-Time End . . . . . . . . . . . . . . . . . .  96 - Yes
         3.8.2.3.  Date-Time Due . . . . . . . . . . . . . . . . . .  97 - NO
         3.8.2.4.  Date-Time Start . . . . . . . . . . . . . . . . .  99 - Yes (from VComponent)
         3.8.2.5.  Duration  . . . . . . . . . . . . . . . . . . . . 100 - Yes
         3.8.2.6.  Free/Busy Time  . . . . . . . . . . . . . . . . . 101 - NO
         3.8.2.7.  Time Transparency . . . . . . . . . . . . . . . . 102 - NO
       3.8.3.  Time Zone Component Properties  . . . . . . . . . . . 103 - NO
         3.8.3.1.  Time Zone Identifier  . . . . . . . . . . . . . . 103 - NO
         3.8.3.2.  Time Zone Name  . . . . . . . . . . . . . . . . . 105 - NO
         3.8.3.3.  Time Zone Offset From . . . . . . . . . . . . . . 106 - NO
         3.8.3.4.  Time Zone Offset To . . . . . . . . . . . . . . . 106 - NO
         3.8.3.5.  Time Zone URL . . . . . . . . . . . . . . . . . . 107 - NO
       3.8.4.  Relationship Component Properties . . . . . . . . . . 108
         3.8.4.1.  Attendee  . . . . . . . . . . . . . . . . . . . . 108 - NO (from VComponent)
         3.8.4.2.  Contact . . . . . . . . . . . . . . . . . . . . . 111 - TODO (from VComponent)
         3.8.4.3.  Organizer . . . . . . . . . . . . . . . . . . . . 113 - TODO (from VComponent)
         3.8.4.4.  Recurrence ID . . . . . . . . . . . . . . . . . . 114 - TODO (from VComponent)
         3.8.4.5.  Related To  . . . . . . . . . . . . . . . . . . . 117 - NO (from VComponent)
         3.8.4.6.  Uniform Resource Locator  . . . . . . . . . . . . 118 - NO (from VComponent)
         3.8.4.7.  Unique Identifier . . . . . . . . . . . . . . . . 119 - Yes (from VComponent)
       3.8.5.  Recurrence Component Properties . . . . . . . . . . . 120
         3.8.5.1.  Exception Date-Times  . . . . . . . . . . . . . . 120 - Yes, in EXDate class
         3.8.5.2.  Recurrence Date-Times . . . . . . . . . . . . . . 122 - TODO, in RDate class
         3.8.5.3.  Recurrence Rule . . . . . . . . . . . . . . . . . 124 - TODO, in RRule class
       3.8.6.  Alarm Component Properties  . . . . . . . . . . . . . 134
         3.8.6.1.  Action  . . . . . . . . . . . . . . . . . . . . . 134 - NO
         3.8.6.2.  Repeat Count  . . . . . . . . . . . . . . . . . . 135 - NO
         3.8.6.3.  Trigger . . . . . . . . . . . . . . . . . . . . . 135 - NO
       3.8.7.  Change Management Component Properties  . . . . . . . 138
         3.8.7.1.  Date-Time Created . . . . . . . . . . . . . . . . 138 - TODO (from VComponent)
         3.8.7.2.  Date-Time Stamp . . . . . . . . . . . . . . . . . 139 - TODO (from VComponent)
         3.8.7.3.  Last Modified . . . . . . . . . . . . . . . . . . 140 - TODO (from VComponent)
         3.8.7.4.  Sequence Number . . . . . . . . . . . . . . . . . 141 - TODO (from VComponent)
       3.8.8.  Miscellaneous Component Properties  . . . . . . . . . 142
         3.8.8.1.  IANA Properties . . . . . . . . . . . . . . . . . 142 - NO (from VComponent)
         3.8.8.2.  Non-Standard Properties . . . . . . . . . . . . . 142 - TODO (from VComponent, some X-properties may be defined here too)
         3.8.8.3.  Request Status  . . . . . . . . . . . . . . . . . 144 - NO (from VComponent)
 * @param <T>
 *
 */
public abstract class VEvent extends VComponent
{   
    /**
     * DESCRIPTION: RFC 5545 iCalendar 3.8.1.12. page 84
     * This property provides a more complete description of the
     * calendar component than that provided by the "SUMMARY" property.
     * Example:
     * DESCRIPTION:Meeting to provide technical review for "Phoenix"
     *  design.\nHappy Face Conference Room. Phoenix design team
     *  MUST attend this meeting.\nRSVP to team leader.
     */
    public StringProperty descriptionProperty() { return description; }
    final private StringProperty description = new SimpleStringProperty(this, "DESCRIPTION");
    public String getDescription() { return description.getValue(); }
    public void setDescription(String value) { description.setValue(value); }
//    public T withDescription(String value) { setDescription(value); return (T)this; } 
    
    /** 
     * DURATION from RFC 5545 iCalendar 3.8.2.5 page 99, 3.3.6 page 34
     * Internally stored a seconds.  Can be set an an integer of seconds or a string as defined by iCalendar which is
     * converted to seconds.  This value is used exclusively internally.  Any specified DTEND is converted to 
     * durationInSeconds,
     * */
    final private SimpleLongProperty durationInSeconds = new SimpleLongProperty(this, "DURATION");
    public SimpleLongProperty durationInSecondsProperty() { return durationInSeconds; }
    public Long getDurationInSeconds() { return durationInSeconds.getValue(); }
    public void setDurationInSeconds(Long value)
    {
        durationInSeconds.setValue(value);
        LocalDateTime newDateTimeEnd = getDateTimeStart().plusSeconds(getDurationInSeconds());
        setDateTimeEnd(newDateTimeEnd);
    }
    public void setDurationInSeconds(String value)
    { // parse ISO.8601.2004 period string into period of seconds (no support for Y (years) or M (months).
        long seconds = 0;
        Pattern p = Pattern.compile("([0-9]+)|([A-Z])");
        Matcher m = p.matcher(value);
        List<String> tokens = new ArrayList<String>();
        while (m.find())
        {
            String token = m.group(0);
            tokens.add(token);
        }
        Iterator<String> tokenIterator = tokens.iterator();
        String firstString = tokenIterator.next();
        if (! tokenIterator.hasNext() || (! firstString.equals("P"))) throw new InvalidParameterException("Invalid DURATION string (" + value + "). Must begin with a P");
        boolean timeFlag = false;
        while (tokenIterator.hasNext())
        {
            String token = tokenIterator.next();
            if (token.matches("\\d+"))
            { // first value is a number means I got a data element
                int n = Integer.parseInt(token);
                String time = tokenIterator.next();
                if (time.equals("W"))
                { // weeks
                    seconds += n * 7 * 24 * 60 * 60;
                } else if (time.equals("D"))
                { // days
                    seconds += n * 24 * 60 * 60;
                } else if (timeFlag && time.equals("H"))
                { // hours
                    seconds += n * 60 * 60;                    
                } else if (timeFlag && time.equals("M"))
                { // minutes
                    seconds += n * 60;                                        
                } else if (timeFlag && time.equals("S"))
                { // seconds
                    seconds += n;                    
                } else
                {
                    throw new InvalidParameterException("Invalid DURATION string time element (" + time + "). Must begin with a P");
                }
            } else if (token.equals("T")) timeFlag = true; // proceeding elements will be hour, minute or second
        }
        durationInSeconds.setValue(seconds);
    }
//    public T withDurationInSeconds(Integer value) { setDurationInSeconds(value); return (T)this; } 
//    public T withDurationInSeconds(String value) { setDurationInSeconds(value); return (T)this; } 
    
    /**
     * DTEND, Date-Time End. from RFC 5545 iCalendar 3.8.2.2 page 95
     * Specifies the date and time that a calendar component ends.
     * If entered this value is used to calculate the durationInSeconds, which is used
     * internally.
     */
    final private ObjectProperty<LocalDateTime> dateTimeEnd = new SimpleObjectProperty<LocalDateTime>(this, "DTEND");
    public ObjectProperty<LocalDateTime> dateTimeEndProperty() { return dateTimeEnd; }
    public void setDateTimeEnd(LocalDateTime dtEnd) { dateTimeEnd.set(dtEnd); }
    public void setDateTimeEnd(String dtEnd)
    {
        LocalDateTime dt = iCalendarDateTimeToLocalDateTime(dtEnd);
        setDateTimeEnd(dt);
    }
    public LocalDateTime getDateTimeEnd() { return dateTimeEnd.get(); }
    private final ChangeListener<? super LocalDateTime> dateTimeEndlistener = (obs, oldSel, newSel) ->
    { // listener to synch dateTimeEnd and durationInSeconds
        if (getDateTimeStart() != null)
        {
            long seconds = ChronoUnit.SECONDS.between(getDateTimeStart(), newSel);
            setDurationInSeconds(seconds);            
        }
    };
    private final ChangeListener<? super LocalDateTime> dateTimeStartlistener = (obs, oldSel, newSel) ->
    { // listener to synch dateTimeStart and durationInSeconds
        if (getDateTimeEnd() != null)
        {
            long seconds = ChronoUnit.SECONDS.between(newSel, getDateTimeEnd());
            setDurationInSeconds(seconds);
        }
    };

    // CONSTRUCTORS
    public VEvent(VEvent vevent)
    {
        super(vevent);
        copy(vevent, this);
        dateTimeEndProperty().addListener(dateTimeEndlistener);
        dateTimeStartProperty().addListener(dateTimeStartlistener);
    }
    
    public VEvent()
    {
        dateTimeEndProperty().addListener(dateTimeEndlistener);
        dateTimeStartProperty().addListener(dateTimeStartlistener);
    }
    
    /** Deep copy all fields from source to destination */
    private static void copy(VEvent source, VEvent destination)
    {
        destination.setDescription(source.getDescription());
        destination.setDurationInSeconds(source.getDurationInSeconds());
//        if (source.getDateTimeRangeStart() != null) destination.setDateTimeRangeStart(source.getDateTimeRangeStart());
//        if (source.getDateTimeRangeEnd() != null) destination.setDateTimeRangeEnd(source.getDateTimeRangeEnd());
    }

    /** Deep copy all fields from source to destination */
    @Override
    public void copyTo(VComponent destination)
    {
        copy(this, (VEvent) destination);
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if((obj == null) || (obj.getClass() != getClass())) {
            return false;
        }
        VEvent testObj = (VEvent) obj;

        boolean descriptionEquals = (getDescription() == null) ?
                (testObj.getDescription() == null) : getDescription().equals(testObj.getDescription());
        boolean durationEquals = (getDurationInSeconds() == null) ?
                (testObj.getDurationInSeconds() == null) : getDurationInSeconds().equals(testObj.getDurationInSeconds());
        System.out.println("VEvent: " + descriptionEquals + " " + durationEquals);
        // don't need to check getDateTimeEnd because it bound to duration
        return super.equals(obj) && descriptionEquals && durationEquals;
    }
    
    /** Make iCalendar compliant string of VEvent calendar component */
    @Override
    public String toString()
    {
//        List<Property> properties2 = new ArrayList<Property>();
//        properties2.addAll(descriptionProperty(), dateTimeEndProperty())
        Map<Property, String> properties = makePropertiesMap();

//        String propertiesString2 = properties2.stream()
//                .map(p -> p.getName() + ":" + p.getValue().toString())
//                .sorted()
//                .peek(System.out::println)
//                .collect(Collectors.joining());
//System.exit(0);        
        // Make properties string
        String propertiesString = properties.entrySet()
                .stream() 
                .map(p -> p.getKey().getName() + ":" + p.getValue() + System.lineSeparator())
                .sorted()
                .collect(Collectors.joining());
        return "BEGIN:VEVENT" + System.lineSeparator() + propertiesString + "END:VEVENT";
    }

    @Override
    Map<Property, String> makePropertiesMap()
    {
        Map<Property, String> properties = new HashMap<Property, String>();
        properties.putAll(super.makePropertiesMap());
        if (getDescription() != null) properties.put(descriptionProperty(), getDescription());
        properties.put(dateTimeEndProperty(), FORMATTER.format(getDateTimeEnd()));
        return properties;
    }
    
    protected static VEvent parseVEvent(VEvent vEvent, List<String> strings)
    {
        if (! strings.get(0).equals("BEGIN:VEVENT"))
        {
            throw new InvalidParameterException("Invalid calendar component. First element must be BEGIN:VEVENT");
        }
        
        Iterator<String> stringsIterator = strings.iterator();
        stringsIterator.next(); // skip BEGIN:VEVENT
        stringsIterator.remove();
        while (stringsIterator.hasNext())
        {
            String[] property = stringsIterator.next().split(":");
            if (property[0].equals(vEvent.descriptionProperty().getName()))
            { // DESCRIPTION
                vEvent.setDescription(property[1]);
                stringsIterator.remove();
            } else if (property[0].equals(vEvent.durationInSecondsProperty().getName()))
            { // DURATION
                vEvent.setDurationInSeconds(property[1]);
                stringsIterator.remove();
            } else if (property[0].equals(vEvent.dateTimeEndProperty().getName()))
            { // DTEND
                LocalDateTime dateTime = LocalDateTime.parse(property[1],FORMATTER);
                vEvent.setDateTimeEnd(dateTime);
                stringsIterator.remove();
            }           
        }
        return (VEvent) VComponent.parseVComponent(vEvent, strings);
    }
       
}
