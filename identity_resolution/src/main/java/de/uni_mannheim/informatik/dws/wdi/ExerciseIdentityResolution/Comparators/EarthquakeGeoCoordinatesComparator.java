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
package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators;


import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Earthquake;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Movie;
import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.Pair;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.date.YearSimilarity;
import de.uni_mannheim.informatik.dws.winter.similarity.geo.GeoCoordinateSimilarity;

/**
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class EarthquakeGeoCoordinatesComparator implements Comparator<Earthquake, Attribute> {

	private static final long serialVersionUID = 1L;
	private GeoCoordinateSimilarity sim = new GeoCoordinateSimilarity(50);
	
	private ComparatorLogger comparisonLog;

	@Override
	public double compare(
			Earthquake record1,
			Earthquake record2,
			Correspondence<Attribute, Matchable> schemaCorrespondences) {
    	
		double similarity;
		
		Pair<Double, Double> pair1 = new Pair<>(record1.getLatitude(),record1.getLongitude());
		Pair<Double, Double> pair2 = new Pair<>(record2.getLatitude(),record2.getLongitude());
		
		if (pair1.getFirst() != -10000 
				&& pair2.getFirst() != -10000 
				&& pair1.getSecond() != -10000 
				&& pair2.getSecond() != -10000) {
			
	    	similarity = sim.calculate(pair1,pair2);
		} else {
			similarity = 0.5;
		}
		
    	
		if(this.comparisonLog != null){
			this.comparisonLog.setComparatorName(getClass().getName());
		
			this.comparisonLog.setRecord1Value(pair1.toString());
			this.comparisonLog.setRecord2Value(pair2.toString());
    	
			this.comparisonLog.setSimilarity(Double.toString(similarity));
		}
		return similarity;
	}

	@Override
	public ComparatorLogger getComparisonLog() {
		return this.comparisonLog;
	}

	@Override
	public void setComparisonLog(ComparatorLogger comparatorLog) {
		this.comparisonLog = comparatorLog;
	}

	

}
