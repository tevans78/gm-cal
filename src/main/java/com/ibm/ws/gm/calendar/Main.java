package com.ibm.ws.gm.calendar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
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

	private static final String DATE_FORMAT = "MMM-dd-yyyy";
	private static final SimpleDateFormat SDF = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
	private static final TimeZone TZ = TimeZone.getTimeZone("UTC");
	static {
		SDF.setTimeZone(TZ);
	}

	private static class Release {
		private String name;
		private Date featureComplete;
		private Date gm;
		private Date ga;

		Release(String name, String featureComplete, String gm, String ga) {
			this.name = name;
			try {
				this.featureComplete = new Date(SDF.parse(featureComplete));
				this.gm = new Date(SDF.parse(gm));
				this.ga = new Date(SDF.parse(ga));
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static final Release[] RELEASES = {
			new Release("24.0.0.1", "Jan-3-2024", "Jan-16-2024", "Jan-30-2024"),
			new Release("24.0.0.2", "Jan-31-2024", "Feb-13-2024", "Feb-27-2024"),
			new Release("24.0.0.3", "Feb-28-2024", "Mar-12-2024", "Mar-26-2024"),
			new Release("24.0.0.4", "Mar-27-2024", "Apr-9-2024", "Apr-23-2024"),
			new Release("24.0.0.5", "Apr-24-2024", "May-7-2024", "May-21-2024"),
			new Release("24.0.0.6", "May-22-2024", "Jun-4-2024", "Jun-18-2024"),
			new Release("24.0.0.7", "Jun-19-2024", "Jul-2-2024", "Jul-16-2024"),
			new Release("24.0.0.8", "Jul-17-2024", "Jul-30-2024", "Aug-13-2024"),
			new Release("24.0.0.9", "Aug-14-2024", "Aug-27-2024", "Sep-10-2024"),
			new Release("24.0.0.10", "Sep-11-2024", "Sep-24-2024", "Oct-8-2024"),
			new Release("24.0.0.11", "Oct-9-2024", "Oct-22-2024", "Nov-5-2024"),
			new Release("24.0.0.12", "Nov-6-2024", "Nov-19-2024", "Dec-3-2024") };

	public static void main(String[] args) throws ParseException, ValidationException, IOException {
		setUp();
		Calendar calendar = new Calendar();
		calendar.getProperties().add(new ProdId("-//Tom Evans//iCal4j 1.0//EN"));
		calendar.getProperties().add(Version.VERSION_2_0);
		calendar.getProperties().add(CalScale.GREGORIAN);

		//TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
		//VTimeZone tz = registry.getTimeZone(TZ.getID()).getVTimeZone();
		//TzId tzParam = new TzId(tz.getProperty(Property.TZID).getValue());
		//calendar.getComponents().add(tz);

		for (Release release : RELEASES) {
			VEvent fc = new VEvent(new Date(release.featureComplete), release.name + " Feature Complete");
			//fc.getProperty(Property.DTSTART).getParameters().replace(tzParam);
			fc.getProperties().add(Transp.TRANSPARENT);
			calendar.getComponents().add(fc);

			VEvent gm = new VEvent(new Date(release.gm), release.name + " GM");
			//gm.getProperty(Property.DTSTART).getParameters().replace(tzParam);
			gm.getProperties().add(Transp.TRANSPARENT);
			calendar.getComponents().add(gm);

			VEvent ga = new VEvent(new Date(release.ga), release.name + " eGA");
			//ga.getProperty(Property.DTSTART).getParameters().replace(tzParam);
			ga.getProperties().add(Transp.TRANSPARENT);
			calendar.getComponents().add(ga);
		}

		System.out.println(calendar.toString());

		CalendarOutputter outputter = new CalendarOutputter(false, FoldingWriter.REDUCED_FOLD_LENGTH);
		FileOutputStream fos = new FileOutputStream(new File("RELEASES_2024.ics"));

		outputter.setValidating(false);
		outputter.output(calendar, fos);
	}

	protected final static void setUp() {
		CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
		CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY, true);
		CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);

		// uncomment for testing invalid calendars in relaxed parsing mode..
//        CompatibilityHints.setHintEnabled(
//                CompatibilityHints.KEY_RELAXED_PARSING, true);
	}

}
