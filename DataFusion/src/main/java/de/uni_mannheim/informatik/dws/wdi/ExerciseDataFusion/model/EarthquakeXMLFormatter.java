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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.uni_mannheim.informatik.dws.winter.model.io.XMLFormatter;

/**
 * {@link XMLFormatter} for {@link Movie}s.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class EarthquakeXMLFormatter extends XMLFormatter<Earthquake> {

	@Override
	public Element createRootElement(Document doc) {
		return doc.createElement("movies");
	}

	@Override
	public Element createElementFromRecord(Earthquake record, Document doc) {
		Element earthquake = doc.createElement("earthquake");

		earthquake.appendChild(createTextElement("id", record.getIdentifier(), doc));

		earthquake.appendChild(createTextElementWithProvenance("date",
				record.getDate().toString(),
				record.getMergedAttributeProvenance(Earthquake.DATE), doc));
		earthquake.appendChild(createTextElementWithProvenance("time",
				record.getTime().toString(),
				record.getMergedAttributeProvenance(Earthquake.TIME), doc));
		earthquake.appendChild(createTextElementWithProvenance("magnitude",
				Double.toString(record.getMagnitude()), 
				record.getMergedAttributeProvenance(Earthquake.MAGNITUDE), doc));
		earthquake.appendChild(createTextElementWithProvenance("Depth",
				Double.toString(record.getDepth()), 
				record.getMergedAttributeProvenance(Earthquake.DEPTH), doc));
		earthquake.appendChild(createTextElementWithProvenance("Latitude",
				Double.toString(record.getLatitude()), 
				record.getMergedAttributeProvenance(Earthquake.LATITUDE), doc));
		earthquake.appendChild(createTextElementWithProvenance("Longitude",
				Double.toString(record.getLongitude()), 
				record.getMergedAttributeProvenance(Earthquake.LONGITUDE), doc));
		

		return earthquake;
	}

	protected Element createTextElementWithProvenance(String name,
			String value, String provenance, Document doc) {
		Element elem = createTextElement(name, value, doc);
		elem.setAttribute("provenance", provenance);
		return elem;
	}


}
