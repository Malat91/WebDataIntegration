/*
 * Copyright (c) 2017 Data and Web Science Group, University of Mannheim, Germany (http://dws.informatik.uni-mannheim.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Node;

import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.XMLMatchableReader;

/**
 * A {@link XMLMatchableReader} for {@link Earthquake}s.
 * 
 * 
 */
public class EarthquakeXMLReader extends XMLMatchableReader<Earthquake, Attribute>  {

	/* (non-Javadoc)
	 * @see de.uni_mannheim.informatik.wdi.model.io.XMLMatchableReader#initialiseDataset(de.uni_mannheim.informatik.wdi.model.DataSet)
	 */
	@Override
	protected void initialiseDataset(DataSet<Earthquake, Attribute> dataset) {
		super.initialiseDataset(dataset);
		
	}
	
	@Override
	public Earthquake createModelFromElement(Node node, String provenanceInfo) {
		String id = getValueFromChildElement(node, "id");

		// create the object with id and provenance information
		Earthquake earthquake = new Earthquake(id, provenanceInfo);

		// fill the attributes
		earthquake.setCountry(getValueFromChildElement(node,"location/country"));
		earthquake.setLatitude(Double.parseDouble(getValueFromChildElement(node,"location/latitude")));
		earthquake.setLongitude(Double.parseDouble(getValueFromChildElement(node,"location/longitude")));
		
		earthquake.setMagnitude(Double.parseDouble(getValueFromChildElement(node,"magnitude")));
		earthquake.setDepth(Double.parseDouble(getValueFromChildElement(node,"depth")));
		
		earthquake.setDeaths(Integer.parseInt(getValueFromChildElement(node,"consequences/deaths")));
		earthquake.setTotalDamages(Double.parseDouble(getValueFromChildElement(node,"consequences/totalDamages")));


		// convert the date string into a Date object
		String date = getValueFromChildElement(node, "date");
		LocalDate dt = LocalDate.parse(date);
		earthquake.setDate(dt);
		
		String time = getValueFromChildElement(node, "time");
		LocalTime lt = LocalTime.parse(time);
		earthquake.setTime(lt);
			
		return earthquake;
	}

}
