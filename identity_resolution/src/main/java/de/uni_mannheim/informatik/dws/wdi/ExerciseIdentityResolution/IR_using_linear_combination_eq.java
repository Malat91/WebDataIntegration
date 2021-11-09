package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.EarthquakeBlockingKeyByDecadeGenerator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.EarthquakeBlockingKeyByYearGenerator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.EarthquakeBlockingKeyByYearMonthGenerator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.EarthquakeCountryComparatorLowerCaseJaccard;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.EarthquakeDateComparator2Years;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.EarthquakeGeoCoordinatesComparator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Earthquake;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.EarthquakeXMLReader;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.MaximumBipartiteMatchingAlgorithm;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.NoBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.SortedNeighbourhoodBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.Performance;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;

public class IR_using_linear_combination_eq 
{
	/*
	 * Logging Options:
	 * 		default: 	level INFO	- console
	 * 		trace:		level TRACE     - console
	 * 		infoFile:	level INFO	- console/file
	 * 		traceFile:	level TRACE	- console/file
	 *  
	 * To set the log level to trace and write the log to winter.log and console, 
	 * activate the "traceFile" logger as follows:
	 *     private static final Logger logger = WinterLogManager.activateLogger("traceFile");
	 *
	 */

	private static final Logger logger = WinterLogManager.activateLogger("default");
	
    public static void main( String[] args ) throws Exception
    {
		// loading data
		logger.info("*\tLoading datasets\t*");
		
		HashedDataSet<Earthquake, Attribute> dataEarthquakes1 = new HashedDataSet<>();
		new EarthquakeXMLReader().loadFromXML(new File("data/input/target_earthquakes_1_without_duplicates.xml"), "/earthquakes/earthquake", dataEarthquakes1);
		
		HashedDataSet<Earthquake, Attribute> dataEarthquakes2 = new HashedDataSet<>();
		new EarthquakeXMLReader().loadFromXML(new File("data/input/target_earthquakes_2_without_duplicates.xml"), "/earthquakes/earthquake", dataEarthquakes2);
		
		HashedDataSet<Earthquake, Attribute> dataEarthquakes3 = new HashedDataSet<>();
		new EarthquakeXMLReader().loadFromXML(new File("data/input/target_earthquakes_3_without_duplicates.xml"), "/earthquakes/earthquake", dataEarthquakes3);
		

		// load the gold standard (test set)
		logger.info("*\tLoading gold standard\t*");
		MatchingGoldStandard gsTest = new MatchingGoldStandard();
		gsTest.loadFromCSVFile(new File(
				"data/goldstandard/ds1_to_ds2_goldstandard.csv"));

		// create a matching rule
		LinearCombinationMatchingRule<Earthquake, Attribute> matchingRule = new LinearCombinationMatchingRule<>(
				0.7);
		matchingRule.activateDebugReport("data/output/debugResultsMatchingRule.csv", 100000, gsTest);
		
		// add comparators
		matchingRule.addComparator(new EarthquakeGeoCoordinatesComparator(), 0.5);
		matchingRule.addComparator(new EarthquakeCountryComparatorLowerCaseJaccard(), 0.25);
		matchingRule.addComparator(new EarthquakeDateComparator2Years(), 0.25);
		
		// create a blocker (blocking strategy)
		//NoBlocker<Earthquake, Attribute> blocker = new NoBlocker<>();
		StandardRecordBlocker<Earthquake, Attribute> blocker = new StandardRecordBlocker<Earthquake,Attribute>(new EarthquakeBlockingKeyByYearMonthGenerator());
		//SortedNeighbourhoodBlocker<Earthquake, Attribute, Attribute> blocker = new SortedNeighbourhoodBlocker<>(new MovieBlockingKeyByTitleGenerator(), 1);
		blocker.setMeasureBlockSizes(true);
		//Write debug results to file:
		blocker.collectBlockSizeData("data/output/debugResultsBlocking.csv", 100);
		
		// Initialize Matching Engine
		MatchingEngine<Earthquake, Attribute> engine = new MatchingEngine<>();

		// Execute the matching
		logger.info("*\tRunning identity resolution\t*");
		Processable<Correspondence<Earthquake, Attribute>> correspondences = engine.runIdentityResolution(
				dataEarthquakes1, dataEarthquakes2, null, matchingRule,
				blocker);

		// Create a top-1 global matching
//		  correspondences = engine.getTopKInstanceCorrespondences(correspondences, 1, 0.0);

//		 Alternative: Create a maximum-weight, bipartite matching
//		 MaximumBipartiteMatchingAlgorithm<Movie,Attribute> maxWeight = new MaximumBipartiteMatchingAlgorithm<>(correspondences);
//		 maxWeight.run();
//		 correspondences = maxWeight.getResult();

		// write the correspondences to the output file
		new CSVCorrespondenceFormatter().writeCSV(new File("data/output/ds1_to_ds2_correspondences.csv"), correspondences);

		
		
		logger.info("*\tEvaluating result\t*");
		// evaluate your result
		MatchingEvaluator<Earthquake, Attribute> evaluator = new MatchingEvaluator<Earthquake, Attribute>();
		Performance perfTest = evaluator.evaluateMatching(correspondences,
				gsTest);

		// print the evaluation result
		logger.info("DS1 Earthquakes <-> DS2 Earthquakes");
		logger.info(String.format(
				"Precision: %.4f",perfTest.getPrecision()));
		logger.info(String.format(
				"Recall: %.4f",	perfTest.getRecall()));
		logger.info(String.format(
				"F1: %.4f",perfTest.getF1()));
    }
}
