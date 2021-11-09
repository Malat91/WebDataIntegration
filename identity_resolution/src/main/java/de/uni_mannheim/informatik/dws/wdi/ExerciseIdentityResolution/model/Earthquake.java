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

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import de.uni_mannheim.informatik.dws.winter.model.AbstractRecord;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;

/**
 * A {@link AbstractRecord} representing an earthquake.
 * 
 */
public class Earthquake implements Matchable {

	/*
	 * example entry <earthquake>
		<id>File3_0001</id>
		<date>1902-04-18</date>
		<time>20:20:00</time>
		<location>
			<latitude>14</latitude>
			<longitude>-91</longitude>
			<country>Guatemala</country>
		</location>
		<magnitude>8</magnitude>
		<depth> 17</depth>
		<consequences>
			<totalDamages>25</totalDamages>
			<deaths>2000</deaths>
		</consequences>
		</earthquake>
	 */

	protected String id;
	protected String provenance;
	private LocalDate date;
	private LocalTime time;
	private double magnitude;
	private double depth;
	private String country;
	private double latitude;
	private double longitude;
	private int deaths;
	private double totalDamages;
	
	

	public Earthquake(String identifier, String provenance) {
		id = identifier;
		this.provenance = provenance;
	}

	@Override
	public String getIdentifier() {
		return id;
	}

	@Override
	public String getProvenance() {
		return provenance;
	}
	
	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public double getMagnitude() {
		return magnitude;
	}

	public void setMagnitude(double magnitude) {
		this.magnitude = magnitude;
	}

	public double getDepth() {
		return depth;
	}

	public void setDepth(double depth) {
		this.depth = depth;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	public double getTotalDamages() {
		return totalDamages;
	}

	public void setTotalDamages(double totalDamages) {
		this.totalDamages = totalDamages;
	}



	@Override
	public String toString() {
		return (new ReflectionToStringBuilder(this) {
	         protected boolean accept(Field f) {
	             return super.accept(f) && !f.getName().equals("password");
	         }
	     }).toString();
		/*StringBuilder result = new StringBuilder();
		  String newLine = System.getProperty("line.separator");

		  result.append( this.getClass().getName() );
		  result.append( " Object {" );
		  result.append(newLine);

		  //determine fields declared in this class only (no fields of superclass)
		  Field[] fields = this.getClass().getDeclaredFields();

		  //print field names paired with their values
		  for ( Field field : fields  ) {
		    result.append("  ");
		    try {
		      result.append( field.getName() );
		      result.append(": ");
		      //requires access to private field:
		      result.append( field.get(this) );
		    } catch ( IllegalAccessException ex ) {
		      System.out.println(ex);
		    }
		    result.append(newLine);
		  }
		  result.append("}");

		  return result.toString();*/
		
		//return String.format("[Earthquake %s]", getIdentifier());
		//return String.format("[Earthquake %s: %s / %s / %s]", getIdentifier(), getDate().toString(),
			//	getTime().toString(), getCountry());
	}

	@Override
	public int hashCode() {
		return getIdentifier().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Earthquake){
			return this.getIdentifier().equals(((Earthquake) obj).getIdentifier());
		}else
			return false;
	}

	
	
	
	
}
