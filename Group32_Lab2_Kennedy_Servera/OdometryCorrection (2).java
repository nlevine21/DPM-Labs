/* 
 * OdometryCorrection.java
 */
package lab_2;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.hardware.Audio;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.SensorPort;



public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	private EV3ColorSensor lightSensor;
	//Arrays that contain the RGB values of light 
	private float[] color={0,0,0};
	private float[] colorsIniti={0,0,0};  //for use with alternate line detection code
	private double x =0;
	private double y =0;
	private int count;
	

	// constructor
	public OdometryCorrection(Odometer odometer,EV3ColorSensor lightSensor) {
		this.odometer = odometer;
		this.lightSensor=lightSensor;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;
		//lightSensor.setFloodlight(lejos.robotics.Color.WHITE);
		
		//Get's initial colors so the current color can be compared 
		//to it to determine if there's a line. Not currently implemented
		//lightSensor.getRGBMode().fetchSample(colorsIniti, 0);
		
		
		count =0;
		while (true) {
			correctionStart = System.currentTimeMillis();
			//Gets current colors and compares them to the initial colors 
			//to determine if there's a black line or not. Not currently implemented 
			// (alternative method for sensing black lines)
			/*lightSensor.getRGBMode().fetchSample(color, 0);
			if(color[0]<(colorsIniti[0]-0.02)&&color[1]<colorsIniti[1]&&color[2]<(colorsIniti[2]-0.001)){
				count++;
			}*/
			// Correction code 
			//Gets values of RGB and puts it into array for later analysis
			lightSensor.getRGBMode().fetchSample(color, 0);
			//If colors are within threshold typically caused by a black line increment count
			// and at certain count values the odometer will be corrected to it's expected values
			if(color[0]<=0.075 && color[1] <= 0.06 && color[2] <= 0.04){
				try {
					//Delay reaction to line to compensate for difference in sensor 
					//location to point directly between wheel axis
					Thread.sleep(320);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//Increment count
				++count;
				
				Sound.beep();
				//Store x and y distances of robot from lines and adjust odometer's later 
				//values corresponding to initial values and how many squares it has covered
				switch(count){
				case(1): y= odometer.getY();
				break;
				case(3): odometer.setY(60.96+y);
				break;
				case(4): x= odometer.getX();
				break; 
				case(6): odometer.setX(60.96+x);
				break;
				case(7): odometer.setY(60.96+y);
				break;
				case(9): odometer.setY(y);
				break;
				case(10): odometer.setX(60.96+x);
				break;
				case(12): odometer.setX(x);
				break;
				}
			}
				
			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
}