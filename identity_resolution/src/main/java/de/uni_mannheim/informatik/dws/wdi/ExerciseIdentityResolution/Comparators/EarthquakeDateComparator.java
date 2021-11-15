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
import de.uni_mannheim.informatik.dws.winter.similarity.date.DaySimilarity;
import de.uni_mannheim.informatik.dws.winter.similarity.date.YearSimilarity;

import java.time.LocalDateTime;
import java.time.LocalTime;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Earthquake;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Movie;

/**
 * {@link Comparator} for {@link Earthquake}s based on the {@link Earthquake#getDate()}
 * value, with a maximal difference of 5 days.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class EarthquakeDateComparator implements Comparator<Earthquake, Attribute> {

	private static final long serialVersionUID = 1L;
	private DaySimilarity sim = new DaySimilarity(5);
	
	private ComparatorLogger comparisonLog;

	@Override
	public double compare(
			Earthquake record1,
			Earthquake record2,
			Correspondence<Attribute, Matchable> schemaCorrespondences) {
		
		// convert LocalDate to LocalDateTime for comparison purpose
		LocalTime t1 = record1.getTime();
		LocalTime t2 = record2.getTime();
		
		LocalDateTime ldt1;
		LocalDateTime ldt2;
		
		// preprocessing
		if (t1 != null) {
			ldt1 = record1.getDate().atTime(t1.getHour(),t1.getMinute());
		} else {
			ldt1 = record1.getDate().atStartOfDay();
		}
		
		if (t2 != null) {
			ldt2 = record2.getDate().atTime(t2.getHour(),t2.getMinute());
		} else {
			ldt2 = record2.getDate().atStartOfDay();
		}
		

    	double similarity = sim.calculate(ldt1, ldt2);
    	
		if(this.comparisonLog != null){
			this.comparisonLog.setComparatorName(getClass().getName());
		
			this.comparisonLog.setRecord1Value(record1.getDate().toString());
			this.comparisonLog.setRecord2Value(record2.getDate().toString());
			
			this.comparisonLog.setRecord2PreprocessedValue(ldt1.toString());
			this.comparisonLog.setRecord2PreprocessedValue(ldt2.toString());
    	
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
