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
package de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.w3c.dom.Node;

import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleFactory;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.XMLMatchableReader;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;

/**
 * A {@link XMLMatchableReader} for {@link Earthquake}s.
 * 
 * 
 */
public class EarthquakeXMLReader extends XMLMatchableReader<Earthquake, Attribute>  implements
FusibleFactory<Earthquake, Attribute> {
	
	//private static final Logger logger = WinterLogManager.activateLogger("default");

	/* (non-Javadoc)
	 * @see de.uni_mannheim.informatik.wdi.model.io.XMLMatchableReader#initialiseDataset(de.uni_mannheim.informatik.wdi.model.DataSet)
	 */
	@Override
	protected void initialiseDataset(DataSet<Earthquake, Attribute> dataset) {
		super.initialiseDataset(dataset);
		dataset.addAttribute(Earthquake.DATE);
		dataset.addAttribute(Earthquake.TIME);
		dataset.addAttribute(Earthquake.MAGNITUDE);
		dataset.addAttribute(Earthquake.DEPTH);
		dataset.addAttribute(Earthquake.LATITUDE);
		dataset.addAttribute(Earthquake.LONGITUDE);
		dataset.addAttribute(Earthquake.COUNTRY);
		dataset.addAttribute(Earthquake.DEATHS);
		dataset.addAttribute(Earthquake.TOTALDAMAGES);
		
	}
	
	@Override
	public Earthquake createModelFromElement(Node node, String provenanceInfo) {
		String id = getValueFromChildElement(node, "id");

		// create the object with id and provenance information
		Earthquake earthquake = new Earthquake(id, provenanceInfo);

		// fill the attributes of earthquake
		earthquake.setCountry(getValueFromChildElement(node, "country"));
		
		// catch missing values and set attribute values to -1
		try {
			earthquake.setLatitude(Double.parseDouble(getValueFromChildElement(node,"latitude")));
		} catch (Exception e) {
			earthquake.setLatitude(-10000);
		}
		try {
			earthquake.setLongitude(Double.parseDouble(getValueFromChildElement(node,"longitude")));
		} catch (Exception e) {
			earthquake.setLongitude(-10000);
		}
		try {
			earthquake.setMagnitude(Double.parseDouble(getValueFromChildElement(node,"magnitude")));
		} catch (Exception e) {
			earthquake.setMagnitude(-1);
		}
		try {
			// parse depth as absolute value to strip possible negative signs
			earthquake.setDepth(Math.abs(Double.parseDouble(getValueFromChildElement(node,"depth"))));
		} catch (Exception e) {
			earthquake.setDepth(-1);
		}
		try {
			
			earthquake.setDeaths(Integer.parseInt(getValueFromChildElement(node,"deaths")));
		} catch (Exception e) {
			earthquake.setDeaths(-1);
		}
		try {
			earthquake.setTotalDamages(Double.parseDouble(getValueFromChildElement(node,"totalDamages")));
		} catch (Exception e) {
			earthquake.setTotalDamages(-1);
		}
		
		// convert the date string into a Date object
		String date = getValueFromChildElement(node, "date");
		try {
			LocalDate dt = LocalDate.parse(date);
			earthquake.setDate(dt);
		} catch (Exception e) {
			//logger.info("missing [date]");
		}
		
		// convert the time string into a time object
		String time = getValueFromChildElement(node, "time");
		try {
			LocalTime lt = LocalTime.parse(time);
			earthquake.setTime(lt);
		} catch (Exception e) {
			//logger.info("missing [time]");
		}

			
		return earthquake;
	}
	
	@Override
	public Earthquake createInstanceForFusion(RecordGroup<Earthquake, Attribute> cluster) {
	
	List<String> ids = new LinkedList<>();
	
	for (Earthquake eq : cluster.getRecords()) {
		ids.add(eq.getIdentifier());
	}
	
	Collections.sort(ids);
	
	String mergedId = StringUtils.join(ids, '+');
	
	return new Earthquake(mergedId, "fused");
	}

}
