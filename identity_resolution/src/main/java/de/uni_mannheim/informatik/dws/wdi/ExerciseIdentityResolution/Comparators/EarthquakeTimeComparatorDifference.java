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

import java.time.Duration;
import java.time.LocalTime;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Earthquake;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Movie;

/**
 * {@link Comparator} for {@link Earthquake}s based on the {@link Earthquakee#getTime()}
 * value and the absolute difference similarity of their minutes value.
 * 
 *   
 */
public class EarthquakeTimeComparatorDifference implements Comparator<Earthquake, Attribute> {

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
		double similarityMin;
		double similarityHour;
		double min1 = -1;
		double min2 = -1;
		
		double hour1 = -1;
		double hour2 = -1;
		
		// return 0.5 if one or both has no time value
		if (t1 == null || t2 == null) {
			similarity =  0.5; 
		} else {
			
			Duration t1SinceMidnight = Duration.between(LocalTime.MIDNIGHT, t1);
			Duration t2SinceMidnight = Duration.between(LocalTime.MIDNIGHT, t2);
			Double t1InMin = Double.valueOf(t1SinceMidnight.getSeconds() / 60);
			Double t2InMin = Double.valueOf(t2SinceMidnight.getSeconds() / 60);
			similarity = sim.calculate(t1InMin,t2InMin);
			
			/*
			Duration diff = Duration.between(t1, t2);
			long diffInSec = diff.getSeconds();
			int diffInMin = (int) diffInSec / 60;
			
			min1 = t1.getMinute();
			min2 = t2.getMinute();
			
			hour1 = t1.getHour();
			hour2 = t2.getHour();
			
			similarityMin = sim.calculate(min1, min2);
			similarityHour = sim.calculate(hour1,hour2);
			
			similarity = 0.5 * similarityMin + 0.5 * similarityHour;*/
			
		}
		
		if(this.comparisonLog != null){
			this.comparisonLog.setComparatorName(getClass().getName());
			
			if (t1 != null) {
				if (t2 != null) {
					this.comparisonLog.setRecord1Value(t1.toString());
					this.comparisonLog.setRecord2Value(t2.toString());
				} else {
					this.comparisonLog.setRecord1Value(t1.toString());
				}
			} else {
				if (t2 != null) {
					this.comparisonLog.setRecord2Value(t2.toString());
				}
				
			}
		
		
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
