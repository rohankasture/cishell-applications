package edu.iu.sci2.visualization.scimaps.rendering.print2012;

import java.awt.Color;
import java.text.ChoiceFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import edu.iu.sci2.visualization.scimaps.MapOfScience;
import edu.iu.sci2.visualization.scimaps.tempvis.GraphicsState;

public class Header {
	String titleSlug;
	String generatedFromSlug;
	String exploreSlug;
	String dateSlug;

	public Header(String title, String generatedFrom,
			MapOfScience mapOfScience) {

		titleSlug = title;
		generatedFromSlug = "Generated from " + generatedFrom;

		exploreSlug = String
				.format("Explore publication activity: %s out of %s publications were mapped to %s subdiciplines and %s diciplines.",
						mapOfScience.prettyCountOfMappedPublications(),
						mapOfScience.prettyCountOfPublications(),
						mapOfScience.prettyCountOfMappedSubdiciplines(),
						mapOfScience.prettyCountOfCategoriesUsed());
		DateTime dateTime = new DateTime();
		DateTimeFormatter formatter = DateTimeFormat
				.forPattern("MMMM dd, yyyy | KK:mm a zzz");
		dateSlug = formatter.print(dateTime);
	}

	public void render(GraphicsState state, float leftMargin, float topMargin) {
		state.save();
		double titleFontSize = 16;
		Color titleColor = new Color(0, 0, 0);
		double slugFontSize = 12;
		Color slugColor = Color.black;
		state.setFontSize(titleFontSize);
		state.current.translate(leftMargin, topMargin);

		// Draw the title
		state.current.setColor(titleColor);
		state.current.drawString(titleSlug, 0, 0);
		state.current.translate(0, titleFontSize);

		// Draw the other slugs
		state.setFontSize(slugFontSize);
		state.current.setColor(slugColor);
		state.current.drawString(generatedFromSlug, 0, 0);
		state.current.translate(0, slugFontSize);
		state.current.drawString(exploreSlug, 0, 0);
		state.current.translate(0, slugFontSize);
		state.current.drawString(dateSlug, 0, 0);
		state.current.translate(0, slugFontSize);
		state.restore();
	}
}