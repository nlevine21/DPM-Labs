package Localization;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;
import Localization.Navigation;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static double ROTATION_SPEED = 30;

	private Odometer odo;
	private SampleProvider usSensor;
	private float[] usData;
	private LocalizationType locType;
	private float distance = this.distance;
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	final static int FAST = 200, SLOW = 100, ACCELERATION = 4000;
	final static double DEG_ERR = 3.0, CM_ERR = 1.0;
	
	public USLocalizer(Odometer odo,  SampleProvider usSensor, float[] usData, LocalizationType locType, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) {
		this.odo = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
		this.leftMotor = leftMotor;
		this.rightMotor =rightMotor;
	}
	
	public void doLocalization() {
		double [] pos = new double [3];
		double angleA, angleB;
		leftMotor.setAcceleration(1000);
		rightMotor.setAcceleration(1000);		
		if (locType == LocalizationType.FALLING_EDGE) {
			leftMotor.setSpeed(100);
			rightMotor.setSpeed(100);
			// rotate the robot clockwise
			while (true){
				turnClockwise();
				double A=0;
				double B=0;
				boolean assignedValues = false;
				//wait until robot does not see wall
				if(getFilteredData()*100>40){
					// keep rotating until the robot sees a wall, then record angle as A
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					outerloop:
					while(true){
						if(getFilteredData()*100<40){
							A = odo.getAng();
							// switch direction and sleep so noise from USSensor does not detect same wall again
							turnAntiClockwise();
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// keep rotating until the robot sees a wall, then record theta as B
							while(true){
								if(getFilteredData()*100<40){
									B = odo.getAng();
									//change assigned values to true so it can access next part of code
									assignedValues=true;
									//break outerloop
									break outerloop;									
								}
							}
						}
					}
					}
				//when values have been assigned to A and B calculations can be made to localize
				if(assignedValues){
					Navigation nav=new Navigation(odo);
					double angle = 0;
					//calculate angle of positive x axis
					if(A<B){
						angle=(A+B)/2-45;
						}
					else{
						angle=135+((A+B)/2);
						}
					// safe checks to prevent angle from being silly
					if(angle<0){
						angle=-angle;}
					if(angle>360){
						angle=angle%360;
					}
					//turn robot to face the positive x axis
					nav.turnTo(angle, true);	
					stopMotors();
					break;
					}
			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
						
			// update the odometer position (example to follow:)
			
				}
			odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
			} else {
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall.
			 * This is very similar to the FALLING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */
			leftMotor.setSpeed(100);
			rightMotor.setSpeed(100);
			while (true){
				turnClockwise();
				double A=0;
				double B=0;
				boolean assignedValues = false;
				//wait until robot does not see wall
				if(getFilteredData()*100>40){
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//when robot sees wall record theta as A
					outerLoop:
					while(true){
						if(getFilteredData()*100<40){
							A = odo.getAng();
							//put thread tot sleep to stop noise from causing it to this it can't see wall
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							while(true){
								//when robot can't see wall record theta as B
								if(getFilteredData()*100>40){
									B = odo.getAng();
									//change assigned values to true so it can access next part of code
									assignedValues=true;
									//break outerloop
									break outerLoop;
								}
							}
						}
					}
				}
				//when values have been assigned to A and B calculations can be made to localize
				if(assignedValues){
					Navigation nav=new Navigation(odo);
					double angle = 0;
					//calculate angle of positive x axis
					if(A<B){
						angle=(A+B)/2-45;
						}
					else{
						angle=135+((A+B)/2);
						}
					// safe checks to prevent angle from being silly
					if(angle<0){
						angle=-angle;}
					if(angle>360){
						angle=angle%360;
					}
					//turn robot to face the positive x axis
					nav.turnTo(angle, true);	
					stopMotors();
					break;
				}
			}
			odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
		}
		
	}
	
	//return distance from ultrasonic sensor in meters
	private float getFilteredData() {
		usSensor.fetchSample(usData, 0);
		float distance = usData[0];	
		return distance;
	}
	//rotate robot clockwise
	private void turnClockwise(){
		leftMotor.forward();
		rightMotor.backward();
	}
	//rotate robot anti-clockwise
	private void turnAntiClockwise(){
		leftMotor.backward();
		rightMotor.forward();
	}
	//stop motors from rotating 
	private void stopMotors(){
		leftMotor.stop();
		rightMotor.stop();
	}

}
