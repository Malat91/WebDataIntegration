package de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.model;

import net.sf.geographiclib.*;

public class Helper {
	
	
	public boolean numeric_perc (double value1, double value2, double pc_max) {
		if (value1 == 0 && value2 == 0) {
			return true;
		}
		
		double pc = abs(value1 - value2)/max(value1, value2);
		return (1 - pc/pc_max) >= 0;
	}
	
	public boolean numeric_abs (double value1, double value2, double d_max) {
		
		return (1 - (abs(value1 - value2)/d_max)) >= 0;
	}
	




    public boolean calculateGeoDistance(
            double lon1, double lat1, double lon2, double lat2, double max) {
        GeodesicData g = Geodesic.WGS84.Inverse(lat1, lon1, lat2, lon2);
        return g.s12 <= max;  // distance in metres
    }

	
	
	public double abs(double value) {
		double return_value = value;
		if (value < 0) {
			return_value = value*-1;
		}
		return return_value;
	}
	
	
	public double max(double value1, double value2) {
		double max = value1;
		if (value2 > value1) {
			max = value2;
		}
		return max;
	}
	
	
	
	

}
