package de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;

import de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.model.Earthquake;
import de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.model.EarthquakeXMLFormatter;
import de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.model.EarthquakeXMLReader;
import de.uni_mannheim.informatik.dws.winter.datafusion.CorrespondenceSet;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionEngine;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionEvaluator;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionStrategy;
import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleDataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleHashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroupFactory;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;
import org.slf4j.Logger;

public class DataFusion_Main_eq 
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
		// Load the Data into FusibleDataSet
		logger.info("*\tLoading datasets\t*");
		FusibleDataSet<Earthquake, Attribute> dataEarthquakes1 = new FusibleHashedDataSet<>();
		new EarthquakeXMLReader().loadFromXML(new File("data/ds_eq_12/input/target_earthquakes_1_without_duplicates.xml"), "/earthquakes/earthquake", dataEarthquakes1);
		dataEarthquakes1.printDataSetDensityReport();

		FusibleDataSet<Earthquake, Attribute> dataEarthquakes2 = new FusibleHashedDataSet<>();
		new EarthquakeXMLReader().loadFromXML(new File("data/ds_eq_12/input/target_earthquakes_2_without_duplicates.xml"), "/earthquakes/earthquake", dataEarthquakes2);
		dataEarthquakes2.printDataSetDensityReport();
		
		FusibleDataSet<Earthquake, Attribute> dataEarthquakes3 = new FusibleHashedDataSet<>();
		new EarthquakeXMLReader().loadFromXML(new File("data/ds_eq_23/input/target_earthquakes_3_without_duplicates_replaced_countries_time_normalized.xml"), "/earthquakes/earthquake", dataEarthquakes3);
		dataEarthquakes3.printDataSetDensityReport();


		// Maintain Provenance
		// Scores (e.g. from rating)
		dataEarthquakes1.setScore(1.0);
		dataEarthquakes2.setScore(2.0);
		dataEarthquakes3.setScore(1.0);


		// Date (e.g. last update)
		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
		        .appendPattern("yyyy-MM-dd")
		        .parseDefaulting(ChronoField.CLOCK_HOUR_OF_DAY, 0)
		        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
		        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
		        .toFormatter(Locale.ENGLISH);
		
		dataEarthquakes1.setDate(LocalDateTime.parse("2020-06-08", formatter));
		dataEarthquakes2.setDate(LocalDateTime.parse("2017-01-26", formatter));
		dataEarthquakes3.setDate(LocalDateTime.parse("2021-10-07", formatter));

		// load correspondences
		logger.info("*\tLoading correspondences\t*");
		CorrespondenceSet<Earthquake, Attribute> correspondences = new CorrespondenceSet<>();
		correspondences.loadCorrespondences(new File("data/correspondences/ds1_to_ds2_correspondences.csv"),dataEarthquakes1, dataEarthquakes2);
		correspondences.loadCorrespondences(new File("data/correspondences/ds2_to_ds3_correspondences.csv"),dataEarthquakes2, dataEarthquakes3);

		
		// write group size distribution
		correspondences.printGroupSizeDistribution();

		// load the gold standard
		logger.info("*\tEvaluating results\t*");
		DataSet<Earthquake, Attribute> gs = new FusibleHashedDataSet<>();
		new EarthquakeXMLReader().loadFromXML(new File("data/goldstandard/gold_eq.xml"), "/earthquakes/earthquake", gs);

		for(Earthquake m : gs.get()) {
			logger.info(String.format("gs: %s", m.getIdentifier()));
		}

		// define the fusion strategy
		DataFusionStrategy<Earthquake, Attribute> strategy = new DataFusionStrategy<>(new EarthquakeXMLReader());
		// write debug results to file
		strategy.activateDebugReport("data/output/debugResultsDatafusion.csv", -1, gs);
		
		// add attribute fusers
		strategy.addAttributeFuser(Earthquake.DATE, new DummyFuser(),new DummyEvaluationRule());
		strategy.addAttributeFuser(Earthquake.TIME,new DummyFuser(), new DummyEvaluationRule());
		strategy.addAttributeFuser(Earthquake.MAGNITUDE, new DummyFuser(),new DummyEvaluationRule());
		strategy.addAttributeFuser(Earthquake.DEPTH,new DummyFuser(),new DummyEvaluationRule());
		strategy.addAttributeFuser(Earthquake.LATITUDE, new DummyFuser(),new DummyEvaluationRule());
		strategy.addAttributeFuser(Earthquake.LONGITUDE, new DummyFuser(),new DummyEvaluationRule());
		strategy.addAttributeFuser(Earthquake.COUNTRY, new DummyFuser(),new DummyEvaluationRule());
		strategy.addAttributeFuser(Earthquake.DEATHS, new DummyFuser(),new DummyEvaluationRule());
		strategy.addAttributeFuser(Earthquake.TOTALDAMAGES, new DummyFuser(),new DummyEvaluationRule());
		
		// create the fusion engine
		DataFusionEngine<Earthquake, Attribute> engine = new DataFusionEngine<>(strategy);

		// print consistency report
		engine.printClusterConsistencyReport(correspondences, null);
		
		// print record groups sorted by consistency
		engine.writeRecordGroupsByConsistency(new File("data/output/recordGroupConsistencies.csv"), correspondences, null);

		// run the fusion
		logger.info("*\tRunning data fusion\t*");
		FusibleDataSet<Earthquake, Attribute> fusedDataSet = engine.run(correspondences, null);

		// write the result
		new EarthquakeXMLFormatter().writeXML(new File("data/output/fused.xml"), fusedDataSet);

		// evaluate
		DataFusionEvaluator<Earthquake, Attribute> evaluator = new DataFusionEvaluator<>(strategy, new RecordGroupFactory<Earthquake, Attribute>());
		
		double accuracy = evaluator.evaluate(fusedDataSet, gs, null);

		logger.info(String.format("*\tAccuracy: %.2f", accuracy));
    }
}
