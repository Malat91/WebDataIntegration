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

import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.date.YearSimilarity;
import de.uni_mannheim.informatik.dws.winter.similarity.numeric.AbsoluteDifferenceSimilarity;
import de.uni_mannheim.informatik.dws.winter.similarity.string.LevenshteinSimilarity;

import java.time.LocalTime;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Earthquake;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Movie;

/**
 * {@link Comparator} for {@link Earthquake}s based on the {@link Earthquakee#getTime()}
 * value and the absolute difference similarity of their minutes value.
 * 
 *   
 */
public class EarthquakeTimeComparatorMinutes implements Comparator<Earthquake, Attribute> {

	private static final long serialVersionUID = 1L;
	private AbsoluteDifferenceSimilarity sim = new AbsoluteDifferenceSimilarity(15);
	
	private ComparatorLogger comparisonLog;

	@Override
	public double compare(
			Earthquake record1,
			Earthquake record2,
			Correspondence<Attribute, Matchable> schemaCorrespondences) {
		
		
		LocalTime t1 = record1.getTime();
		LocalTime t2 = record2.getTime();
		
		double similarity;
		double min1 = -1;
		double min2 = -1;
		
		// return 0.5 if one or both has no time value
		if (t1 == null || t2 == null) {
			similarity =  0.5; 
		} else {
    	
			min1 = t1.getMinute();
			min2 = t2.getMinute();
			
			similarity = sim.calculate(min1, min2);
			
		}
		
		if(this.comparisonLog != null){
			this.comparisonLog.setComparatorName(getClass().getName());
		
			this.comparisonLog.setRecord1Value(Double.toString(min1));
			this.comparisonLog.setRecord2Value(Double.toString(min2));
		
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
