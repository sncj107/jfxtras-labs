package jfxtras.labs.icalendarfx.components;

import java.time.temporal.Temporal;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import jfxtras.labs.icalendarfx.properties.PropertyEnum;
import jfxtras.labs.icalendarfx.properties.component.change.DateTimeCreated;
import jfxtras.labs.icalendarfx.properties.component.change.LastModified;
import jfxtras.labs.icalendarfx.properties.component.change.Sequence;
import jfxtras.labs.icalendarfx.properties.component.descriptive.Attachment;
import jfxtras.labs.icalendarfx.properties.component.descriptive.Categories;
import jfxtras.labs.icalendarfx.properties.component.descriptive.Classification;
import jfxtras.labs.icalendarfx.properties.component.descriptive.Summary;
import jfxtras.labs.icalendarfx.properties.component.recurrence.Exceptions;
import jfxtras.labs.icalendarfx.properties.component.recurrence.RecurrenceRule;
import jfxtras.labs.icalendarfx.properties.component.recurrence.Recurrences;
import jfxtras.labs.icalendarfx.properties.component.relationship.Contact;
import jfxtras.labs.icalendarfx.properties.component.relationship.RecurrenceId;
import jfxtras.labs.icalendarfx.properties.component.relationship.RelatedTo;

public abstract class VComponentDisplayableBase<T, I> extends VComponentPersonalBase<T> implements VComponentDisplayable<T,I>, VComponentRepeatable<T>, VComponentDescribable<T>
{
    // TODO - ADD LISTENERS TO SYNCH TYPES FOR ALL DATE-TIME TYPES TO DTSTART
    
    /**
     * ATTACH
     * Attachment
     * RFC 5545, 3.8.1.1, page 80
     * 
     * This property provides the capability to associate a document object with a calendar component.
     * 
     * Examples:
     * ATTACH:CID:jsmith.part3.960817T083000.xyzMail@example.com
     * ATTACH;FMTTYPE=application/postscript:ftp://example.com/pub/
     *  reports/r-960812.ps
     * */
    @Override
    public ObservableList<Attachment<?>> getAttachments()
    {
        return attachments;
    }
    private ObservableList<Attachment<?>> attachments;
    @Override
    public void setAttachments(ObservableList<Attachment<?>> attachments) { this.attachments = attachments; }
    
    /**
     * CATEGORIES:
     * RFC 5545 iCalendar 3.8.1.12. page 81
     * This property defines the categories for a calendar component.
     * Example:
     * CATEGORIES:APPOINTMENT,EDUCATION
     * CATEGORIES:MEETING
     */
    @Override
    public ObservableList<Categories> getCategories() { return categories; }
    private ObservableList<Categories> categories;
    @Override
    public void setCategories(ObservableList<Categories> categories) { this.categories = categories; }
    
    /**
     * CLASS
     * Classification
     * RFC 5545, 3.8.1.3, page 82
     * 
     * This property defines the access classification for a calendar component.
     * 
     * Example:
     * CLASS:PUBLIC
     */
    @Override
    public ObjectProperty<Classification> classificationProperty()
    {
        if (classification == null)
        {
            classification = new SimpleObjectProperty<>(this, PropertyEnum.CLASSIFICATION.toString());
        }
        return classification;
    }
    private ObjectProperty<Classification> classification;
    
    /**
     * CONTACT:
     * RFC 5545 iCalendar 3.8.4.2. page 109
     * This property is used to represent contact information or
     * alternately a reference to contact information associated with the
     * calendar component.
     * 
     * Example:
     * CONTACT;ALTREP="ldap://example.com:6666/o=ABC%20Industries\,
     *  c=US???(cn=Jim%20Dolittle)":Jim Dolittle\, ABC Industries\,
     *  +1-919-555-1234
     */
    @Override
    public ObservableList<Contact> getContact() { return contact; }
    private ObservableList<Contact> contact;
    @Override
    public void setContact(ObservableList<Contact> contacts) { this.contact = contacts; }
    
    /**
     * CREATED: Date-Time Created
     * RFC 5545 iCalendar 3.8.7.1 page 136
     * This property specifies the date and time that the calendar information was created.
     * This is analogous to the creation date and time for a file in the file system.
     * The value MUST be specified in the UTC time format.
     * 
     * Example:
     * CREATED:19960329T133000Z
     */
    @Override
    public ObjectProperty<DateTimeCreated> dateTimeCreatedProperty()
    {
        if (dateTimeCreated == null)
        {
            dateTimeCreated = new SimpleObjectProperty<>(this, PropertyEnum.DATE_TIME_CREATED.toString());
        }
        return dateTimeCreated;
    }
    private ObjectProperty<DateTimeCreated> dateTimeCreated;
    
   /** 
    * EXDATE
    * Exception Date-Times
    * RFC 5545 iCalendar 3.8.5.1, page 117.
    * 
    * This property defines the list of DATE-TIME exceptions for
    * recurring events, to-dos, journal entries, or time zone definitions.
    */ 
    @Override
    public ObservableList<Exceptions<? extends Temporal>> getExceptions()
    {
        return exceptions;
    }
    private ObservableList<Exceptions<? extends Temporal>> exceptions;
    @Override
    public void setExceptions(ObservableList<Exceptions<? extends Temporal>> exceptions)
    {
        this.exceptions = exceptions;
        exceptions.addListener(VComponentRepeatable.RECURRENCE_LISTENER);
    }
    
    /**
    * LAST-MODIFIED
    * RFC 5545, 3.8.7.3, page 138
    * 
    * This property specifies the date and time that the
    * information associated with the calendar component was last
    * revised in the calendar store.
    *
    * Note: This is analogous to the modification date and time for a
    * file in the file system.
    * 
    * The value MUST be specified as a date with UTC time.
    * 
    * Example:
    * LAST-MODIFIED:19960817T133000Z
    */
    @Override
    public ObjectProperty<LastModified> dateTimeLastModifiedProperty()
    {
        if (lastModified == null)
        {
            lastModified = new SimpleObjectProperty<>(this, PropertyEnum.LAST_MODIFIED.toString());
        }
        return lastModified;
    }
    private ObjectProperty<LastModified> lastModified;
    
    /**
     * RDATE
     * Recurrence Date-Times
     * RFC 5545 iCalendar 3.8.5.2, page 120.
     * 
     * This property defines the list of DATE-TIME values for
     * recurring events, to-dos, journal entries, or time zone definitions.
     * 
     * NOTE: DOESN'T CURRENTLY SUPPORT PERIOD VALUE TYPE
     * */
    @Override
    public ObservableList<Recurrences<? extends Temporal>> getRecurrences() { return recurrences; }
    private ObservableList<Recurrences<? extends Temporal>> recurrences;
    @Override
    public void setRecurrences(ObservableList<Recurrences<? extends Temporal>> recurrences)
    {
        this.recurrences = recurrences;
        recurrences.addListener(VComponentRepeatable.RECURRENCE_LISTENER);
    }

    /**
     * RECURRENCE-ID: Recurrence Identifier
     * RFC 5545 iCalendar 3.8.4.4 page 112
     * The property value is the original value of the "DTSTART" property of the 
     * recurrence instance before an edit that changed the value.
     * 
     * Example:
     * RECURRENCE-ID;VALUE=DATE:19960401
     */
    @Override
    public ObjectProperty<RecurrenceId<? extends Temporal>> recurrenceIdProperty()
    {
        if (recurrenceId == null)
        {
            recurrenceId = new SimpleObjectProperty<>(this, PropertyEnum.RECURRENCE_IDENTIFIER.toString());
        }
        return recurrenceId;
    }
    private ObjectProperty<RecurrenceId<? extends Temporal>> recurrenceId;

    /**
     * RELATED-TO:
     * 3.8.4.5, RFC 5545 iCalendar, page 115
     * This property is used to represent a relationship or reference between
     * one calendar component and another.  By default, the property value points to another
     * calendar component's UID that has a PARENT relationship to the referencing object.
     * This field is null unless the object contains as RECURRENCE-ID value.
     * 
     * Example:
     * RELATED-TO:19960401-080045-4000F192713-0052@example.com
     */
    @Override
    public ObservableList<RelatedTo> getRelatedTo() { return relatedTo; }
    private ObservableList<RelatedTo> relatedTo;
    @Override
    public void setRelatedTo(ObservableList<RelatedTo> relatedTo) { this.relatedTo = relatedTo; }
    
    /**
     * RRULE, Recurrence Rule
     * RFC 5545 iCalendar 3.8.5.3, page 122.
     * This property defines a rule or repeating pattern for recurring events, 
     * to-dos, journal entries, or time zone definitions
     * If component is not repeating the value is null.
     * 
     * Examples:
     * RRULE:FREQ=DAILY;COUNT=10
     * RRULE:FREQ=WEEKLY;UNTIL=19971007T000000Z;WKST=SU;BYDAY=TU,TH
     */
    @Override public ObjectProperty<RecurrenceRule> recurrenceRuleProperty()
    {
        if (recurrenceRule == null)
        {
            recurrenceRule = new SimpleObjectProperty<>(this, PropertyEnum.UNIQUE_IDENTIFIER.toString());
        }
        return recurrenceRule;
    }
    private ObjectProperty<RecurrenceRule> recurrenceRule;
 
    /**
     * SEQUENCE:
     * RFC 5545 iCalendar 3.8.7.4. page 138
     * This property defines the revision sequence number of the calendar component within a sequence of revisions.
     * Example:  The following is an example of this property for a calendar
     * component that was just created by the "Organizer":
     *
     * SEQUENCE:0
     *
     * The following is an example of this property for a calendar
     * component that has been revised two different times by the
     * "Organizer":
     *
     * SEQUENCE:2
     */
    @Override
    public ObjectProperty<Sequence> sequenceProperty()
    {
        if (sequence == null)
        {
            sequence = new SimpleObjectProperty<>(this, PropertyEnum.SEQUENCE.toString());
        }
        return sequence;
    }
    private ObjectProperty<Sequence> sequence;

    /**
     * SUMMARY
     * RFC 5545 iCalendar 3.8.1.12. page 93
     * 
     * This property defines a short summary or subject for the calendar component.
     * 
     * Example:
     * SUMMARY:Department Party
     */
    @Override public ObjectProperty<Summary> summaryProperty()
    {
        if (summary == null)
        {
            summary = new SimpleObjectProperty<>(this, PropertyEnum.SUMMARY.toString());
        }
        return summary;
    }
    private ObjectProperty<Summary> summary;
    
    /*
     * CONSTRUCTORS
     */
    public VComponentDisplayableBase() { }
    
    public VComponentDisplayableBase(String contentLines)
    {
        super(contentLines);
    }
}