package com.ibm.ws.gm.calendar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.TimeZone;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.FoldingWriter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ValidationException;

public class Main {

	private static class Release{
		private String name;
		private String featureComplete;
		private String gm;
		private String ga;

		Release(String name, String featureComplete, String gm, String ga){
			this.name = name;
			this.featureComplete = featureComplete;
			this.gm = gm;
			this.ga = ga;
		}
	}
	
	private static final String DATE_FORMAT = "MMM-dd-yyyy";
	
	private static final Release[] RELEASES = {
			new Release("22.0.0.1", "Dec-10-2021", "Jan-4-2022", "Jan-18-2022"),
			new Release("22.0.0.2", "Jan-19-2022", "Feb-2-2022", "Feb-15-2022"),
			new Release("22.0.0.3", "Feb-16-2022", "Mar-2-2022", "Mar-15-2022"),
			new Release("22.0.0.4", "Mar-16-2022", "Mar-30-2022", "Apr-12-2022"),
			new Release("22.0.0.5", "Apr-13-2022", "Apr-27-2022", "May-10-2022"),
			new Release("22.0.0.6", "May-11-2022", "May-25-2022", "Jun-7-2022"),
			new Release("22.0.0.7", "Jun-6-2022", "Jun-22-2022", "Jul-5-2022"),
			new Release("22.0.0.8", "Jul-6-2022", "Jul-20-2022", "Aug-2-2022"),
			new Release("22.0.0.9", "Aug-3-2022", "Aug-17-2022", "Aug-30-2022"),
			new Release("22.0.0.10", "Aug-31-2022", "Sep-14-2022", "Sep-27-2022"),
			new Release("22.0.0.11", "Sep-28-2022", "Oct-12-2022", "Oct-25-2022"),
			new Release("22.0.0.12", "Oct-26-2022", "Nov-9-2022", "Nov-22-2022"),
			new Release("22.0.0.13", "Nov-23-2022", "Dec-7-2022", "Dec-20-2022") };
	
	public static void main(String[] args) throws ParseException, ValidationException, IOException {
		setUp();
		Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//Tom Evans//iCal4j 1.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
        
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        VTimeZone tz = registry.getTimeZone(TimeZone.getDefault().getID()).getVTimeZone();
        TzId tzParam = new TzId(tz.getProperty(Property.TZID).getValue());
        calendar.getComponents().add(tz);
        
        for(Release release:RELEASES) {
        	VEvent fc = new VEvent(new Date(release.featureComplete, DATE_FORMAT), release.name+ " Feature Complete");
        	fc.getProperty(Property.DTSTART).getParameters().replace(tzParam);
        	fc.getProperties().add(Transp.TRANSPARENT);
	        calendar.getComponents().add(fc);

        	VEvent gm = new VEvent(new Date(release.gm, DATE_FORMAT), release.name + " GM");
	        gm.getProperty(Property.DTSTART).getParameters().replace(tzParam);
	        gm.getProperties().add(Transp.TRANSPARENT);
	        calendar.getComponents().add(gm);
	        
	        VEvent ga = new VEvent(new Date(release.ga, DATE_FORMAT), release.name + " eGA");
	        ga.getProperty(Property.DTSTART).getParameters().replace(tzParam);
	        ga.getProperties().add(Transp.TRANSPARENT);
	        calendar.getComponents().add(ga);
        }
        
        System.out.println(calendar.toString());
        
        CalendarOutputter outputter = new CalendarOutputter(false, FoldingWriter.REDUCED_FOLD_LENGTH);
        FileOutputStream fos = new FileOutputStream(new File("RELEASES.ics"));
        
        
        outputter.setValidating(false);
        outputter.output(calendar, fos);
	}

	
    protected final static void setUp() {
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_NOTES_COMPATIBILITY, true);
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_RELAXED_VALIDATION, true);

        // uncomment for testing invalid calendars in relaxed parsing mode..
//        CompatibilityHints.setHintEnabled(
//                CompatibilityHints.KEY_RELAXED_PARSING, true);
    }

}
