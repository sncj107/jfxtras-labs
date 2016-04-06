package jfxtras.labs.icalendar.parameters;

import java.util.HashMap;
import java.util.Map;

import jfxtras.labs.icalendar.parameters.FreeBusyType.FreeBusyTypeEnum;
import jfxtras.labs.icalendar.properties.component.time.FreeBusyTime;

/**
 * FBTYPE
 * Free/Busy Time Type
 * RFC 5545, 3.2.9, page 20
 * 
 * To specify the free or busy time type.
 * 
 * Example:
 * FREEBUSY;FBTYPE=BUSY:19980415T133000Z/19980415T170000Z
 * 
 * @author David Bal
 * @see FreeBusyTime
 */
public class FreeBusyType extends ParameterBase<FreeBusyType, FreeBusyTypeEnum>
{
    private String unknownValue; // contains exact string for unknown value

    /** set BUSY as default FreeBusy type value */
    public FreeBusyType()
    {
        super(FreeBusyTypeEnum.BUSY); // default value
    }
  
    public FreeBusyType(FreeBusyTypeEnum value)
    {
        super(value);
    }

    public FreeBusyType(String content)
    {
        super(FreeBusyTypeEnum.enumFromName(content));
        if (getValue() == FreeBusyTypeEnum.UNKNOWN)
        {
            unknownValue = content;
        }
    }
    
    public FreeBusyType(FreeBusyType source)
    {
        super(source);
    }
    
    @Override
    public String toContent()
    {
        String value = (getValue() == FreeBusyTypeEnum.UNKNOWN) ? unknownValue : getValue().toString();
        String parameterName = myParameterEnum().toString();
        return ";" + parameterName + "=" + value;
    }
    
    public enum FreeBusyTypeEnum
    {
        FREE ("FREE"), // the time interval is free for scheduling
        BUSY ("BUSY"), // the time interval is busy because one or more events have been scheduled for that interval - THE DEFAULT
        BUSY_UNAVAILABLE ("BUSY-UNAVAILABLE"), // the time interval is busy and that the interval can not be scheduled
        BUSY_TENTATIVE ("BUSY-TENTATIVE"), // the time interval is busy because one or more events have been tentatively scheduled for that interval
        UNKNOWN ("UNKNOWN");

        private static Map<String, FreeBusyTypeEnum> enumFromNameMap = makeEnumFromNameMap();
        private static Map<String, FreeBusyTypeEnum> makeEnumFromNameMap()
        {
            Map<String, FreeBusyTypeEnum> map = new HashMap<>();
            FreeBusyTypeEnum[] values = FreeBusyTypeEnum.values();
            for (int i=0; i<values.length; i++)
            {
                map.put(values[i].toString(), values[i]);
            }
            return map;
        }
        /** get enum from name */
        public static FreeBusyTypeEnum enumFromName(String propertyName)
        {
            FreeBusyTypeEnum type = enumFromNameMap.get(propertyName.toUpperCase());
            return (type == null) ? UNKNOWN : type;
        }
        
        private String name;
        @Override public String toString() { return name; }
        FreeBusyTypeEnum(String name)
        {
            this.name = name;
        }
    }
}