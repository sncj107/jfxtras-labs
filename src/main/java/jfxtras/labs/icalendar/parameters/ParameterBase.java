package jfxtras.labs.icalendar.parameters;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Base class of all iCalendar Parameters
 * Example VALUE=DATE
 * 
 * @author David Bal
 *
 * @param <U> - type of value stored in the Parameter, such as String for text-based, or the enumerated type of the classes based on an enum
 * @param <T> - implemented subclass
 */
public class ParameterBase<T,U> implements Parameter<U>
{
    private final ParameterEnum myParameterEnum;
    ParameterEnum myParameterEnum() { return myParameterEnum; }
    
    @Override
    public U getValue() { return value.get(); }
    @Override
    public ObjectProperty<U> valueProperty() { return value; }
    private ObjectProperty<U> value;
    @Override
    public void setValue(U value) { this.value.set(value); }
    public T withValue(U value) { setValue(value); return (T) this; }

    @Override
    public String toContent()
    {
        final String value;
        if (getValue() instanceof Collection)
        {
            value = ((Collection<?>) getValue()).stream()
                    .map(obj -> addDoubleQuotesIfNecessary(obj.toString()))
                    .collect(Collectors.joining(","));
        } else if (getValue() instanceof Boolean)
        {
            value = getValue().toString().toUpperCase();
        } else
        {
            value = addDoubleQuotesIfNecessary(getValue().toString());
        }
        return (getValue() != null) ? ";" + myParameterEnum().toString() + "=" + value : null;
    }

    @Override
    public String toString()
    {
        return super.toString() + "," + toContent();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if((obj == null) || (obj.getClass() != getClass())) {
            return false;
        }
        ParameterBase<T,U> testObj = (ParameterBase<T,U>) obj;

        return getValue().equals(testObj.getValue());
    }
    
    @Override
    public int hashCode()
    {
        return getValue().hashCode();
    }
    
    @Override // MAY HAVE TO GO TO ENUM
    public void copyTo(Parameter<U> destination)
    {
        destination.setValue(getValue());
    }
    
    @Override
    public int compareTo(Parameter<U> test)
    {
        return toContent().compareTo(test.toContent());
    }
    
    /*
     * CONSTRUCTORS
     */
    ParameterBase()
    {
        myParameterEnum = ParameterEnum.enumFromClass(getClass());
        value = new SimpleObjectProperty<>(this, myParameterEnum.toString());
    }

    ParameterBase(U value)
    {
        this();
        setValue(value);
    }

    
    ParameterBase(ParameterBase<T,U> source)
    {
        this();
        setValue(source.getValue());
    }
    
    /*
     * STATIC UTILITY METHODS
     */

    /**
     * Remove leading and trailing double quotes
     * 
     * @param input - string with or without double quotes at front and end
     * @return - string stripped of leading and trailing double quotes
     */
    static String removeDoubleQuote(String input)
    {
        final char quote = '\"';
        StringBuilder builder = new StringBuilder(input);
        if (builder.charAt(0) == quote)
        {
            builder.deleteCharAt(0);
        }
        if (builder.charAt(builder.length()-1) == quote)
        {
            builder.deleteCharAt(builder.length()-1);
        }
        return builder.toString();
    }
    
    /**
     * Add Double Quotes to front and end of string if text contains \ : ;
     * 
     * @param text
     * @return
     */
    static String addDoubleQuotesIfNecessary(String text)
    {
        boolean hasDQuote = text.contains("\"");
        boolean hasColon = text.contains(":");
        boolean hasSemiColon = text.contains(";");
        if (hasDQuote || hasColon || hasSemiColon)
        {
            return "\"" + text + "\""; // add double quotes
        } else
        {
            return text;
        }
    }
    
    /**
     * Remove parameter name and equals sign, if present, otherwise return input string
     * 
     * @param input - parameter content with or without name and equals sign
     * @param name - name of parameter
     * @return - nameless string
     * 
     * example input:
     * ALTREP="CID:part3.msg.970415T083000@example.com"
     * output:
     * "CID:part3.msg.970415T083000@example.com"
     */
    static String extractValue(String content, String name)
    {
        if (content.substring(0, name.length()).equals(name))
        {
            return content.substring(name.length()+1);
        }
        return content;
//        if (content.charAt(0) != '\"') // don't modify if first character is "
//        {
//            int equalsIndex = content.indexOf('=');
//            return (equalsIndex > 0) ? content.substring(equalsIndex+1) : content;
//        }
//        return content;
            
    }
    
    /**
     * Parse comma-separated list of URIs into a List<URI>
     * 
     */
    static List<URI> makeURIList(String content)
    {
        List<URI> uriList = new ArrayList<>();
        Iterator<String> i = Arrays.stream(content.split(",")).iterator();
        while (i.hasNext())
        {
            uriList.add(makeURI(i.next()));
        }
        return uriList;
    }
    
    // Make URI from content
    static URI makeURI(String content)
    {
        URI uri = null;
        try
        {
            uri = new URI(removeDoubleQuote(content));
        } catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
        return uri;
    }
    
    
//    private static String parseString(String content)
//    {
//        int equalsIndex = content.indexOf('=');
//        return (equalsIndex > 0) ? content.substring(equalsIndex+1) : content;
////        return Parameter.removeDoubleQuote(value);
//    }
}
