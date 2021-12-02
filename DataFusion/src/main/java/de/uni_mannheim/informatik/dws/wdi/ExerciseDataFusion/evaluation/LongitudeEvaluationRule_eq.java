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
import de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.model.Helper;
import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;


import net.sf.geographiclib.*;


/**
 * {@link EvaluationRule} for the directors of {@link Earthquake}s. The rule simply
 * compares the director of two {@link Earthquake}s and returns true, in case they
 * are identical.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class LongitudeEvaluationRule_eq extends EvaluationRule<Earthquake, Attribute> {

	@Override
	public boolean isEqual(Earthquake record1, Earthquake record2, Attribute schemaElement) {
		
		double rec1_lat=record1.getLatitude();
		double rec2_lat=record2.getLatitude();
		double rec1_long=record1.getLongitude();
		double rec2_long=record2.getLongitude();
		
		Helper helper = new Helper();
		
		if(rec1_long == -10000 && rec2_long == -10000)
			return true;
		else 
			if(rec1_long == -10000 ^ rec2_long == -10000)
		return false;
		
		else 
		return (helper.calculateGeoDistance(rec1_long, rec1_lat, rec2_long, rec2_lat, 15000));
			//return rec1 == rec2;//alternatively might also be rounded, but should be close
			//return Math.round(record1.getLatitude()) == Math.round(record2.getLatitude());
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
