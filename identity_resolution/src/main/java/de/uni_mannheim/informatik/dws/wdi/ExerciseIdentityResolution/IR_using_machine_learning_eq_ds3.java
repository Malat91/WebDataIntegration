package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution;

import java.io.File;

import org.slf4j.Logger;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.EarthquakeBlockingKeyByYearGenerator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.EarthquakeBlockingKeyByYearMonthGenerator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.EarthquakeCountryComparatorLowerCaseJaccard;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.EarthquakeCountryComparatorLowerCaseLevenshtein;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.EarthquakeDateComparator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.EarthquakeDateComparator2Years;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.EarthquakeDepthComparator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.EarthquakeGeoCoordinatesComparator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.EarthquakeMagnitudeComparator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.EarthquakeMagnitudeComparatorRounded;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.EarthquakeTimeComparatorDifference;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.EarthquakeTimeComparatorJaccard;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.EarthquakeTimeComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.EarthquakeTimeComparatorMinutes;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Earthquake;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.EarthquakeXMLReader;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.RuleLearner;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.SortedNeighbourhoodBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.WekaMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.Performance;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;

public class IR_using_machine_learning_eq_ds3 {
	
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
		new EarthquakeXMLReader().loadFromXML(new File("data/input/target_earthquakes_3_without_duplicates_replaced_countries_time_normalized.xml"), "/earthquakes/earthquake", dataEarthquakes3);
		
		// load the training set
		logger.info("*\tLoading gold standard training set\t*");
		MatchingGoldStandard gsTraining = new MatchingGoldStandard();
		gsTraining.loadFromCSVFile(new File("data/goldstandard/ds2_to_ds3_gs_train.csv"));
		
		// create a matching rule
		
		// use a logistic regression
		//String options[] = new String[] { "-S" };
		//String modelType = "SimpleLogistic"; 
		
		// use a non-linear model
		String options[] = new String[] { "-U" };
		//String options[] = new String[] { };
		//String modelType = "RandomForest"; 
		//String modelType = "VotedPerceptron";
		String modelType = "J48";  //J48-> decision tree
		WekaMatchingRule<Earthquake, Attribute> matchingRule = new WekaMatchingRule<>(0.7, modelType, options);
		//matchingRule.setForwardSelection(true);
		//matchingRule.setBackwardSelection(true);
		matchingRule.activateDebugReport("data/output/debugResultsMatchingRule.csv", 100000, gsTraining);
		
		// add comparators
		matchingRule.addComparator(new EarthquakeCountryComparatorLowerCaseJaccard());
		//matchingRule.addComparator(new EarthquakeCountryComparatorLowerCaseLevenshtein());
		//matchingRule.addComparator(new EarthquakeDateComparator2Years());
		matchingRule.addComparator(new EarthquakeDateComparator());
		matchingRule.addComparator(new EarthquakeTimeComparatorMinutes());
		matchingRule.addComparator(new EarthquakeTimeComparatorDifference());
		//matchingRule.addComparator(new EarthquakeDepthComparator());
		matchingRule.addComparator(new EarthquakeGeoCoordinatesComparator());
		matchingRule.addComparator(new EarthquakeMagnitudeComparatorRounded());
		//matchingRule.addComparator(new EarthquakeMagnitudeComparator());
		
		
		// train the matching rule's model
		logger.info("*\tLearning matching rule\t*");
		RuleLearner<Earthquake, Attribute> learner = new RuleLearner<>();
		learner.learnMatchingRule(dataEarthquakes2, dataEarthquakes3, null, matchingRule, gsTraining);
		logger.info(String.format("Matching rule is:\n%s", matchingRule.getModelDescription()));
		
		// create a blocker (blocking strategy)
		StandardRecordBlocker<Earthquake, Attribute> blocker = new StandardRecordBlocker<Earthquake, Attribute>(new EarthquakeBlockingKeyByYearMonthGenerator());
		//SortedNeighbourhoodBlocker<Movie, Attribute, Attribute> blocker = new SortedNeighbourhoodBlocker<>(new MovieBlockingKeyByYearGenerator(), 30);

		blocker.collectBlockSizeData("data/output/debugResultsBlocking.csv", 100);
		
		// Initialize Matching Engine
		MatchingEngine<Earthquake, Attribute> engine = new MatchingEngine<>();

		// Execute the matching
		logger.info("*\tRunning identity resolution\t*");
		Processable<Correspondence<Earthquake, Attribute>> correspondences = engine.runIdentityResolution(
				dataEarthquakes2, dataEarthquakes3, null, matchingRule,
				blocker);

		// write the correspondences to the output file
		new CSVCorrespondenceFormatter().writeCSV(new File("data/output/earthquake2_2_earthquake3.csv"), correspondences);

		// load the gold standard (test set)
		logger.info("*\tLoading gold standard\t*");
		MatchingGoldStandard gsTest = new MatchingGoldStandard();
		gsTest.loadFromCSVFile(new File(
				"data/goldstandard/ds2_to_ds3_gs_test.csv"));
		
		// evaluate your result
		logger.info("*\tEvaluating result\t*");
		MatchingEvaluator<Earthquake, Attribute> evaluator = new MatchingEvaluator<Earthquake, Attribute>();
		Performance perfTest = evaluator.evaluateMatching(correspondences,
				gsTest);
		
		// print the evaluation result
		logger.info("Earthquake 2 <-> Earthquake 3");
		logger.info(String.format(
				"Precision: %.4f",perfTest.getPrecision()));
		logger.info(String.format(
				"Recall: %.4f",	perfTest.getRecall()));
		logger.info(String.format(
				"F1: %.4f",perfTest.getF1()));
    }
}
