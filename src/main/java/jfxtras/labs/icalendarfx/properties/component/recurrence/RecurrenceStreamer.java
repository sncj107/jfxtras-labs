package jfxtras.labs.icalendarfx.properties.component.recurrence;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import jfxtras.labs.icalendarfx.components.VComponentDisplayable;
import jfxtras.labs.icalendarfx.components.VComponentRepeatable;
import jfxtras.labs.icalendarfx.properties.component.recurrence.rrule.RecurrenceRule3;
import jfxtras.labs.icalendarfx.utilities.DateTimeUtilities;

/**
 * Produces a stream of Temporal objects representing the recurrence set as defined 
 * RFC 5545 3.8.5.2, page 121
 * The recurrence set is the complete set of recurrence instances for a calendar component.
 * 
 * @author David Bal
 *
 */
public class RecurrenceStreamer
{    
    // Variables for start date or date/time cache used as starting Temporal for stream
    private static final int CACHE_RANGE = 51; // number of values in cache
    private static final int CACHE_SKIP = 21; // store every nth value in the cache
    private int skipCounter = 0; // counter that increments up to CACHE_SKIP, indicates time to record a value, then resets to 0
    public Temporal[] temporalCache; // the start date or date/time cache
    private Temporal dateTimeStartLast; // last dateTimeStart, when changes indicates clearing the cache is necessary
    private RecurrenceRule3 rRuleLast; // last rRule, when changes indicates clearing the cache is necessary
    public int cacheStart = 0; // start index where cache values are stored (starts in middle)
    public int cacheEnd = 0; // end index where cache values are stored
    private VComponentRepeatable<?> component; // the VComponent
    
    public RecurrenceStreamer(VComponentRepeatable<?> component)
    {
        this.component = component;
    }

    /**
     * finds previous value in recurrence set before input parameter value
     * 
     * @param value - start value
     * @return - previous recurrence instance
     */
    public Temporal previousValue(Temporal value)
    {
        final Temporal start; 
        if (cacheEnd == 0)
        {
            start = component.getDateTimeStart().getValue();
        } else
        { // try to get start from cache
            Temporal m  = null;
            for (int i=cacheEnd; i>cacheStart; i--)
            {
                if (DateTimeUtilities.isBefore(temporalCache[i], value))
                {
                    m = temporalCache[i];
                    break;
                }
            }
            start = (m != null) ? m : component.getDateTimeStart().getValue();
        }
        Iterator<Temporal> i = streamNoCache(start).iterator();
        Temporal lastT = null;
        while (i.hasNext())
        {
            Temporal t = i.next();
            if (! DateTimeUtilities.isBefore(t, value)) break;
            lastT = t;
        }
        return lastT;
    }
    
    public Temporal getStartFromCache(Temporal start)
    {
        final Temporal match;
        final Temporal dateTimeStart;
        final RecurrenceRule3 recurrenceRule;
        
        dateTimeStart = component.getDateTimeStart().getValue();
        
        recurrenceRule = (component.getRecurrenceRule() != null) ? component.getRecurrenceRule().getValue() : null;

        // adjust start to ensure its not before dateTimeStart
        final Temporal start2 = (DateTimeUtilities.isBefore(start, dateTimeStart)) ? dateTimeStart : start;
        final Temporal latestCacheValue;

        
        if (recurrenceRule == null)
        { // if individual event
            return null;
        }

        // check if cache needs to be cleared (changes to RRULE or DTSTART)
        if ((dateTimeStartLast != null) && (rRuleLast != null))
        {
            boolean startChanged = ! dateTimeStart.equals(dateTimeStartLast);
            boolean rRuleChanged = ! recurrenceRule.equals(rRuleLast);
            if (startChanged || rRuleChanged)
            {
                temporalCache = null;
                cacheStart = 0;
                cacheEnd = 0;
                skipCounter = 0;
                dateTimeStartLast = dateTimeStart;
                rRuleLast = recurrenceRule;
            }
        } else
        { // save current DTSTART and RRULE for next time
            dateTimeStartLast = dateTimeStart;
            rRuleLast = recurrenceRule;
        }
        
        // use cache if available to find matching start date or date/time
        if (temporalCache != null)
        {
            // Reorder cache to maintain centered and sorted
            final int len = temporalCache.length;
            final Temporal[] p1;
            final Temporal[] p2;
            if (cacheEnd < cacheStart) // high values have wrapped from end to beginning
            {
                p1 = Arrays.copyOfRange(temporalCache, cacheStart, len);
                p2 = Arrays.copyOfRange(temporalCache, 0, Math.min(cacheEnd+1,len));
            } else if (cacheEnd > cacheStart) // low values have wrapped from beginning to end
            {
                p2 = Arrays.copyOfRange(temporalCache, cacheStart, len);
                p1 = Arrays.copyOfRange(temporalCache, 0, Math.min(cacheEnd+1,len));
            } else
            {
                p1 = null;
                p2 = null;
            }
            if (p1 != null)
            { // copy elements to accommodate wrap and restore sort order
                int p1Index = 0;
                int p2Index = 0;
                for (int i=0; i<len; i++)
                {
                    if (p1Index < p1.length)
                    {
                        temporalCache[i] = p1[p1Index];
                        p1Index++;
                    } else if (p2Index < p2.length)
                    {
                        temporalCache[i] = p2[p2Index];
                        p2Index++;
                    } else
                    {
                        cacheEnd = i;
                        break;
                    }
                }
            }

            // Find match in cache
            latestCacheValue = temporalCache[cacheEnd];
            if ((! DateTimeUtilities.isBefore(start2, temporalCache[cacheStart])))
            {
                Temporal m = latestCacheValue;
                for (int i=cacheStart; i<cacheEnd+1; i++)
                {
                    if (DateTimeUtilities.isAfter(temporalCache[i], start2))
                    {
                        m = temporalCache[i-1];
                        break;
                    }
                }
                match = m;
            } else
            { // all cached values too late - start over
                cacheStart = 0;
                cacheEnd = 0;
                temporalCache[cacheStart] = dateTimeStart;
                match = dateTimeStart;
            }
//            earliestCacheValue = temporalCache[cacheStart];
        } else
        { // no previous cache.  initialize new array with dateTimeStart as first value.
            temporalCache = new Temporal[CACHE_RANGE];
            temporalCache[cacheStart] = dateTimeStart;
            match = dateTimeStart;
//            earliestCacheValue = dateTimeStart;
//            latestCacheValue = dateTimeStart;
        }
        return match;
    }
    
    /**
     * Produces the recurrence set
     * 
     * From RDC 5545, 3.8.5.1, page 119:
     * The recurrence set is the complete
     * set of recurrence instances for a calendar component.  The
     * recurrence set is generated by considering the initial "DTSTART"
     * property along with the "RRULE", "RDATE", and "EXDATE" properties
     * contained within the recurring component.  The "DTSTART" property
     * defines the first instance in the recurrence set.  The "DTSTART"
     * property value SHOULD match the pattern of the recurrence rule, if
     * specified.  The recurrence set generated with a "DTSTART" property
     * value that doesn't match the pattern of the rule is undefined.
     * The final recurrence set is generated by gathering all of the
     * start DATE-TIME values generated by any of the specified "RRULE"
     * and "RDATE" properties, and then excluding any start DATE-TIME
     * values specified by "EXDATE" properties.  This implies that start
     * DATE-TIME values specified by "EXDATE" properties take precedence
     * over those specified by inclusion properties (i.e., "RDATE" and
     * "RRULE").  When duplicate instances are generated by the "RRULE"
     * and "RDATE" properties, only one recurrence is considered.
     * Duplicate instances are ignored.
     * 
     * @param start - Temporal representing when to start the recurrence set.  start doesn't
     * have to be DTSTART, but DTSTART is always valid.
     * @return - stream of the recurrence set
     */
    @Deprecated
    public Stream<Temporal> stream(Temporal start)
    {
        final Temporal dateTimeStart;
        final RecurrenceRule3 recurrenceRule;
        
        dateTimeStart = component.getDateTimeStart().getValue();

        final Comparator<Temporal> temporalComparator;
        if (start instanceof LocalDate)
        {
            temporalComparator = (t1, t2) -> ((LocalDate) t1).compareTo((LocalDate) t2);
        } else if (start instanceof LocalDateTime)
        {
            temporalComparator = (t1, t2) -> ((LocalDateTime) t1).compareTo((LocalDateTime) t2);            
        } else if (start instanceof ZonedDateTime)
        {
            temporalComparator = (t1, t2) -> ((ZonedDateTime) t1).compareTo((ZonedDateTime) t2);
        } else
        {
            throw new DateTimeException("Unsupported Temporal type:" + start.getClass().getSimpleName());
        }
        
        recurrenceRule = (component.getRecurrenceRule() != null) ? component.getRecurrenceRule().getValue() : null;

        // adjust start to ensure its not before dateTimeStart
        final Temporal start2 = (DateTimeUtilities.isBefore(start, dateTimeStart)) ? dateTimeStart : start;
        final Stream<Temporal> stream1; // individual or rrule stream
        final Temporal earliestCacheValue;
        final Temporal latestCacheValue;

        
        if (recurrenceRule == null)
        { // if individual event
            stream1 = Arrays.asList(dateTimeStart)
                    .stream()
                    .filter(d -> ! DateTimeUtilities.isBefore(d, start2));
            earliestCacheValue = null;
            latestCacheValue = null;
        } else
        {
            // check if cache needs to be cleared (changes to RRULE or DTSTART)
            if ((dateTimeStartLast != null) && (rRuleLast != null))
            {
                boolean startChanged = ! dateTimeStart.equals(dateTimeStartLast);
                boolean rRuleChanged = ! recurrenceRule.equals(rRuleLast);
                if (startChanged || rRuleChanged)
                {
                    temporalCache = null;
                    cacheStart = 0;
                    cacheEnd = 0;
                    skipCounter = 0;
                    dateTimeStartLast = dateTimeStart;
                    rRuleLast = recurrenceRule;
                }
            } else
            { // save current DTSTART and RRULE for next time
                dateTimeStartLast = dateTimeStart;
                rRuleLast = recurrenceRule;
            }
            
            final Temporal match;
            
            // use cache if available to find matching start date or date/time
            if (temporalCache != null)
            {
                // Reorder cache to maintain centered and sorted
                final int len = temporalCache.length;
                final Temporal[] p1;
                final Temporal[] p2;
                if (cacheEnd < cacheStart) // high values have wrapped from end to beginning
                {
                    p1 = Arrays.copyOfRange(temporalCache, cacheStart, len);
                    p2 = Arrays.copyOfRange(temporalCache, 0, Math.min(cacheEnd+1,len));
                } else if (cacheEnd > cacheStart) // low values have wrapped from beginning to end
                {
                    p2 = Arrays.copyOfRange(temporalCache, cacheStart, len);
                    p1 = Arrays.copyOfRange(temporalCache, 0, Math.min(cacheEnd+1,len));
                } else
                {
                    p1 = null;
                    p2 = null;
                }
                if (p1 != null)
                { // copy elements to accommodate wrap and restore sort order
                    int p1Index = 0;
                    int p2Index = 0;
                    for (int i=0; i<len; i++)
                    {
                        if (p1Index < p1.length)
                        {
                            temporalCache[i] = p1[p1Index];
                            p1Index++;
                        } else if (p2Index < p2.length)
                        {
                            temporalCache[i] = p2[p2Index];
                            p2Index++;
                        } else
                        {
                            cacheEnd = i;
                            break;
                        }
                    }
                }
    
                // Find match in cache
                latestCacheValue = temporalCache[cacheEnd];
                if ((! DateTimeUtilities.isBefore(start2, temporalCache[cacheStart])))
                {
                    Temporal m = latestCacheValue;
                    for (int i=cacheStart; i<cacheEnd+1; i++)
                    {
                        if (DateTimeUtilities.isAfter(temporalCache[i], start2))
                        {
                            m = temporalCache[i-1];
                            break;
                        }
                    }
                    match = m;
                } else
                { // all cached values too late - start over
                    cacheStart = 0;
                    cacheEnd = 0;
                    temporalCache[cacheStart] = dateTimeStart;
                    match = dateTimeStart;
                }
                earliestCacheValue = temporalCache[cacheStart];
            } else
            { // no previous cache.  initialize new array with dateTimeStart as first value.
                temporalCache = new Temporal[CACHE_RANGE];
                temporalCache[cacheStart] = dateTimeStart;
                match = dateTimeStart;
                earliestCacheValue = dateTimeStart;
                latestCacheValue = dateTimeStart;
            }
            stream1 = recurrenceRule.streamRecurrences(match);
        }
        
        // If present, add recurrence list
        final Stream<Temporal> stream2 = (component.getRecurrences() == null) ? stream1 : RecurrenceStreamer.merge(
                stream1,
                component.getRecurrences()
                        .stream()
                        .flatMap(r -> r.getValue().stream())
                        .map(v -> (Temporal) v)
                        .sorted(temporalComparator)
                , temporalComparator);
        
        // If present, remove exceptions
        final Stream<Temporal> stream3;
        if ((component instanceof VComponentDisplayable) && (((VComponentDisplayable<?>) component).getExceptions() != null))
        {
            List<Temporal> exceptions = ((VComponentDisplayable<?>) component).getExceptions()
                    .stream()
                    .flatMap(r -> r.getValue().stream())
                    .map(v -> (Temporal) v)
                    .sorted(temporalComparator)
                    .collect(Collectors.toList());
            stream3 = stream2.filter(d -> ! exceptions.contains(d));
        } else
        {
            stream3 = stream2;
        }

        Stream<Temporal> stream4 = stream3
                .peek(t ->
                { // save new values in cache
                    if (recurrenceRule != null)
                    {
                        if (DateTimeUtilities.isBefore(t, earliestCacheValue))
                        {
                            if (skipCounter == CACHE_SKIP)
                            {
                                cacheStart--;
                                if (cacheStart < 0) cacheStart = CACHE_RANGE - 1;
                                if (cacheStart == cacheEnd) cacheEnd--; // just overwrote oldest value - push cacheEnd down
                                temporalCache[cacheStart] = t;
                                skipCounter = 0;
                            } else skipCounter++;
                        }
                        if (DateTimeUtilities.isAfter(t, latestCacheValue))
                        {
                            if (skipCounter == CACHE_SKIP)
                            {
                                cacheEnd++;
                                if (cacheEnd == CACHE_RANGE) cacheEnd = 0;
                                if (cacheStart == cacheEnd) cacheStart++; // just overwrote oldest value - push cacheStart up
                                temporalCache[cacheEnd] = t;
                                skipCounter = 0;
                            } else skipCounter++;
                        }
                        // check if start or end needs to wrap
                        if (cacheEnd < 0) cacheEnd = CACHE_RANGE - 1;
                        if (cacheStart == CACHE_RANGE) cacheStart = 0;
                        System.out.println("makeCache:" + cacheStart + " " + cacheEnd);
                    }
                })
                .filter(t -> ! DateTimeUtilities.isBefore(t, start2)); // remove too early events;

        return stream4;
    }
    
    /** add to cache while streaming recurrences */
    public Stream<Temporal> makeCache(Stream<Temporal> inStream)
    {
        Temporal earliestCacheValue = temporalCache[cacheStart];
        Temporal latestCacheValue = temporalCache[cacheEnd];
//        System.out.println("makeCache:" + earliestCacheValue + " " + latestCacheValue + " " + component.getRecurrences());
        Stream<Temporal> outStream = inStream
                .peek(t ->
                { // save new values in cache
                    if (component.getRecurrenceRule() != null)
                    {
                        if (DateTimeUtilities.isBefore(t, earliestCacheValue))
                        {
                            if (skipCounter == CACHE_SKIP)
                            {
                                cacheStart--;
                                if (cacheStart < 0) cacheStart = CACHE_RANGE - 1;
                                if (cacheStart == cacheEnd) cacheEnd--; // just overwrote oldest value - push cacheEnd down
                                temporalCache[cacheStart] = t;
                                skipCounter = 0;
                            } else skipCounter++;
                        }
                        if (DateTimeUtilities.isAfter(t, latestCacheValue))
                        {
                            if (skipCounter == CACHE_SKIP)
                            {
                                cacheEnd++;
                                if (cacheEnd == CACHE_RANGE) cacheEnd = 0;
                                if (cacheStart == cacheEnd) cacheStart++; // just overwrote oldest value - push cacheStart up
                                temporalCache[cacheEnd] = t;
                                skipCounter = 0;
                            } else skipCounter++;
                        }
                        // check if start or end needs to wrap
                        if (cacheEnd < 0) cacheEnd = CACHE_RANGE - 1;
                        if (cacheStart == CACHE_RANGE) cacheStart = 0;
//                        System.out.println("makeCache:" + cacheStart + " " + cacheEnd);
                    }
                });

        //                .filter(t -> ! DateTimeUtilities.isBefore(t, start)); // remove too early events;

        return outStream;
    }

    
    /**
     * Produces the recurrence set beginning at DTSTART
     */ 
    @Deprecated
    public Stream<Temporal> stream()
    {
        return stream(component.getDateTimeStart().getValue());
    }
    
    /** Stream of date/times that indicate the start of the event(s).
     * For a VEvent without RRULE the stream will contain only one date/time element.
     * A VEvent with a RRULE the stream contains more than one date/time element.  It will be infinite 
     * if COUNT or UNTIL is not present.  The stream has an end when COUNT or UNTIL condition is met.
     * Starts on startDateTime, which must be a valid event date/time, not necessarily the
     * first date/time (DTSTART) in the sequence. 
     * 
     * @param start - starting date or date/time for which occurrence start date or date/time
     * are generated by the returned stream
     * @return stream of starting dates or date/times for occurrences after rangeStart
     */
    @Deprecated
    public Stream<Temporal> streamNoCache(Temporal start)
    {
        final Comparator<Temporal> temporalComparator;
        if (start instanceof LocalDate)
        {
            temporalComparator = (t1, t2) -> ((LocalDate) t1).compareTo((LocalDate) t2);
        } else if (start instanceof LocalDateTime)
        {
            temporalComparator = (t1, t2) -> ((LocalDateTime) t1).compareTo((LocalDateTime) t2);            
        } else if (start instanceof ZonedDateTime)
        {
            temporalComparator = (t1, t2) -> ((ZonedDateTime) t1).compareTo((ZonedDateTime) t2);
        } else
        {
            throw new DateTimeException("Unsupported Temporal type:" + start.getClass().getSimpleName());
        }
        final Stream<Temporal> stream1;
        if (component.getRecurrenceRule() == null)
        { // if individual event
            stream1 = Arrays.asList(component.getDateTimeStart().getValue())
                    .stream()
                    .map(v -> (Temporal) v)
                    .filter(d -> ! DateTimeUtilities.isBefore(d, start));
        } else
        { // if has recurrence rule
            stream1 = component.getRecurrenceRule().getValue().streamRecurrences(component.getDateTimeStart().getValue());
        }
        // If present, add recurrence list
        final Stream<Temporal> stream2;
        if (component.getRecurrences() == null)
        {
            stream2 = stream1;
        } else
        {
            stream2 = RecurrenceStreamer.merge(
                         stream1
                       , component.getRecurrences()
                               .stream()
                               .flatMap(r -> r.getValue().stream())
                               .map(v -> (Temporal) v)
                               .sorted(temporalComparator)
                       , temporalComparator);
        }
        
        final Stream<Temporal> stream3;
        if ((component instanceof VComponentDisplayable) && (((VComponentDisplayable<?>) component).getExceptions() != null))
        {
            /** Remove date/times in exDates set */
            List<Temporal> exceptions = ((VComponentDisplayable<?>) component).getExceptions()
                    .stream()
                    .flatMap(r -> r.getValue().stream())
                    .map(v -> (Temporal) v)
                    .sorted(temporalComparator)
                    .collect(Collectors.toList());
            stream3 = stream2.filter(d -> ! exceptions.contains(d));
        } else
        {
            stream3 = stream2;
        }
        return stream3.filter(t -> ! DateTimeUtilities.isBefore(t, start));
    }
    
    public static <T> Stream<T> merge(Stream<T> stream1, Stream<T> stream2, Comparator<T> comparator)
    {
            Iterator<T> iterator = new MergedIterator<T>(
                    stream1.iterator()
                  , stream2.iterator()
                  , comparator);
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
    }
    
    /*
     * Recommend using with StreamSupport.stream(iteratorStream, false);
     */

    /** Merge two sorted iterators */
    private static class MergedIterator<T> implements Iterator<T>
    {
        private final Iterator<T> iterator1;
        private final Iterator<T> iterator2;
        private final Comparator<T> comparator;
        private T next1;
        private T next2;
        
        public MergedIterator(Iterator<T> iterator1, Iterator<T> iterator2, Comparator<T> comparator)
        {
            this.iterator1 = iterator1;
            this.iterator2 = iterator2;
            this.comparator = comparator;
        }
        
        @Override
        public boolean hasNext()
        {
            return  iterator1.hasNext() || iterator2.hasNext() || (next1 != null) || (next2 != null);
        }

        @Override
        public T next()
        {
            if (iterator1.hasNext() && (next1 == null)) next1 = iterator1.next();
            if (iterator2.hasNext() && (next2 == null)) next2 = iterator2.next();
            T theNext;
            int result = (next1 == null) ? 1 :
                         (next2 == null) ? -1 :
                         comparator.compare(next1, next2);
            if (result > 0)
            {
                theNext = next2;
                next2 = null;
            } else if (result < 0)
            {
                theNext = next1;
                next1 = null;
            } else
            { // same element, return one, advance both
                theNext = next1;
                next1 = null;
                next2 = null;
            }
            return theNext;
        }
    }

    public Temporal getStartFromCache()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
