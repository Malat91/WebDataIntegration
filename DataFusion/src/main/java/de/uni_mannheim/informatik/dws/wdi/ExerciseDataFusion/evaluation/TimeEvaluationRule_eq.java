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
package de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.evaluation;

import de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.model.Earthquake;
import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

/**
 * {@link EvaluationRule} for the date of {@link Earthquake}s. The rule simply
 * compares the year of the dates of two {@link Earthquake}s.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class TimeEvaluationRule_eq extends EvaluationRule<Earthquake, Attribute> {

	@Override
	public boolean isEqual(Earthquake record1, Earthquake record2, Attribute schemaElement) {
		if(record1.getTime()==null && record2.getTime()==null)
			return true;
		else if(record1.getTime()==null ^ record2.getTime()==null)
			return false;
		else
			//return record1.getTime() == record2.getTime();
			return record1.getTime().getHour() == record2.getTime().getHour(); //Exact time match not as important. But hours should be roughly the same
	}

	/* (non-Javadoc)
	 * @see de.uni_mannheim.informatik.wdi.datafusion.EvaluationRule#isEqual(java.lang.Object, java.lang.Object, de.uni_mannheim.informatik.wdi.model.Correspondence)
	 */
	@Override
	public boolean isEqual(Earthquake record1, Earthquake record2,
			Correspondence<Attribute, Matchable> schemaCorrespondence) {
		return isEqual(record1, record2, (Attribute)null);
	}
	
}
