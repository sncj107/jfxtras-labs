package jfxtras.labs.icalendarfx.properties.component.alarm;

import jfxtras.labs.icalendarfx.components.VAlarmInt;
import jfxtras.labs.icalendarfx.properties.PropertyBase;

/**
 * REPEAT
 * Repeat Count
 * RFC 5545, 3.8.6.2, page 133
 * 
 * This property defines the number of times the alarm should
 * be repeated, after the initial trigger.
 * 
 * If the alarm triggers more than once, then this property MUST be specified
 * along with the "DURATION" property.
 * 
 * Examples:
 * REPEAT:4
 * DURATION:PT5M
 * 
 * @author David Bal
 * 
 * The property can be specified in following components:
 * @see VAlarmInt
 */
public class RepeatCount extends PropertyBase<Integer, RepeatCount>
{
    public RepeatCount(CharSequence contentLine)
    {
        super(contentLine);
    }

    public RepeatCount(Integer value)
    {
        super(value);
    }
    
    public RepeatCount(RepeatCount source)
    {
        super(source);
    }
    
    public RepeatCount()
    {
        super(0); // default is 0
    }
    
    @Override
    public void setValue(Integer value)
    {
        if (value >= 0)
        {
            super.setValue(value);
        } else
        {
            throw new IllegalArgumentException(propertyType() + " must be greater than or equal to zero");
        }
    }
}
