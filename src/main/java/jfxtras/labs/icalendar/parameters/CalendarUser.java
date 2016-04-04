package jfxtras.labs.icalendar.parameters;

import jfxtras.labs.icalendar.parameters.CalendarUser.CalendarUserType;
import jfxtras.labs.icalendar.properties.component.relationship.Attendee;

/**
 * CUTYPE
 * Calendar User Type
 * RFC 5545, 3.2.3, page 16
 * 
 * To identify the type of calendar user specified by the property.
 * 
 * Example:
 * ATTENDEE;CUTYPE=GROUP:mailto:ietf-calsch@example.org
 * 
 * @author David Bal
 * @see Attendee
 */
public class CalendarUser extends ParameterBase<CalendarUser, CalendarUserType>
{
    public CalendarUser()
    {
        super(CalendarUserType.INDIVIDUAL);
    }

    public CalendarUser(CalendarUserType type)
    {
        super(type);
    }
    
    public CalendarUser(String content)
    {
        super(CalendarUserType.valueOf2(content));
    }

    public CalendarUser(CalendarUser source)
    {
        super(source);
    }
    
    public enum CalendarUserType
    {
        INDIVIDUAL, // default is INDIVIDUAL
        GROUP,
        RESOURCE,
        ROOM,
        UNKNOWN;
        
        static CalendarUserType valueOf2(String value)
        {
            CalendarUserType match;
            try
            {
                match = valueOf(value);
            } catch (Exception e)
            {
                match = UNKNOWN;
            }
            return match;
        }

    }
}
