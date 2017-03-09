package lab_3;

import lab_3.Odometer;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.Audio;
import lejos.hardware.Sound;
import lejos.hardware.sensor.EV3UltrasonicSensor;


public class navigation extends Thread {
	
	//constants 
	private double x,y,theta;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 100;
	private static final int CONSTANT_WHEEL_SPEED = 300;

	
	private final int FILTER_OUT = 20;
	
	
	private int filterControl;
	private Odometer odometer;
	private double distance;
	@SuppressWarnings("unused")
	private EV3UltrasonicSensor USSensor;
	private EV3LargeRegulatedMotor USSensorMotor;
	private int path;
	private boolean navigating=false;
	
	//default constructor
	public navigation(EV3LargeRegulatedMotor leftMotor,EV3LargeRegulatedMotor rightMotor,EV3LargeRegulatedMotor USSensorMotor,double leftRadius, double rightRadius, double width, Odometer odometer,EV3UltrasonicSensor USSensor,int path) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.path=path;
		this.USSensorMotor=USSensorMotor;
		this.odometer = odometer;
		this.x = odometer.getX();
		this.USSensor = USSensor;
		this.y = odometer.getY();
		this.theta = odometer.getTheta();
		for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] { leftMotor, rightMotor }) {
			motor.stop();
			motor.setAcceleration(3000);
		}
	}
		public void run(){
		// wait 5 seconds
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
		if(path==1){//travel first course (no obstacles present)
			travelTo(60,30);
			travelTo(30,30);
			travelTo(30,60);
			travelTo(60,0);
		}else{//travel second course (obstacles present)
			travelTo(0,60);
			travelTo(60,0);
		}
		//stop motors when they have completed the course
		leftMotor.stop();
		rightMotor.stop();
		}
	
	//First calls the turnTo method to get the robot pointing in direction of destination
	// then it moves robot forward continuously checking for both obstacles and if the
	// robot has reached its destination. If it encounters an obstacle it adjusts robots angle
	// of robot and ultrasonic sensor then calls avoid. Once avoid is complete the angle of the 
	// ultrasonic sensor is returned to straight ahead and travelTo is called again. If it reaches 
	// its destination it finishes
	public void travelTo(double x, double y){
		navigating=true;
		//Calculate angle to destination
		double newTheta = Math.atan2(x- odometer.getX(), y- odometer.getY())*360/(2*Math.PI) - odometer.getTheta()*360/(Math.PI*2); 
		// rotates the robot to the proper angle 
		turnTo(newTheta); 
		navigating=true;
		while(navigating){
			float[] sample={0};
			USSensor.fetchSample(sample, 0);
			//if obstacle then avoid for time then call travel to again
			if(sample[0]*100<22){
				//Stop forward motion
				rightMotor.stop();
				leftMotor.stop();
				//rotate ultrasonic sensor 45 degrees to the left so it's in position for wall follower
				USSensorMotor.setSpeed(ROTATE_SPEED);
				USSensorMotor.rotate(-45);
				//rotate robot clockwise so that it is in position for wall follower
				rightMotor.setSpeed(ROTATE_SPEED);
				leftMotor.setSpeed(ROTATE_SPEED);
				leftMotor.rotate(convertAngle(lab_3.WHEEL_RADIUS, lab_3.TRACK, 90), true);
				rightMotor.rotate(-convertAngle(lab_3.WHEEL_RADIUS, lab_3.TRACK, 90), false);
				//calls p-type wall follower
				avoid();
				//rotate ultrasonic sensor back to straight
				USSensorMotor.rotate(45);

				//makes robot travel in the correct direction again
				travelTo(x,y);
			}
			//if no obstacle is detected continue forward and check to see if destination has been reached 
			distance =  Math.sqrt(Math.pow(x - odometer.getX(), 2) + Math.pow(y - odometer.getY(), 2)); //calculates the distance needed to travel  

			if(distance<3){//if destination is reached
				try {
					Thread.sleep(100);
					navigating=false;
					//Thread.sleep(327);// 327 milli seconds corresponds linear distance of 3cm traveled
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}else{// continue towards destination
				// moves the robot forward until it has traveled the required distance
				leftMotor.setSpeed(FORWARD_SPEED);  
				rightMotor.setSpeed(FORWARD_SPEED);
				leftMotor.forward();
				rightMotor.forward();
			}
		}
	}

	// tells the robot how much each motor should rotate and the whether it should turn clockwise or anti-clockwise  
	public void turnTo (double theta){
		navigating=true;
		// the robot turns anti-clockwise
				if(theta < -180 || theta > 180){
				leftMotor.setSpeed(ROTATE_SPEED);
				rightMotor.setSpeed(ROTATE_SPEED);
				leftMotor.rotate(-convertAngle(lab_3.WHEEL_RADIUS, lab_3.TRACK, theta), true);
				rightMotor.rotate(convertAngle(lab_3.WHEEL_RADIUS, lab_3.TRACK, theta), false);
				}
				else{ //turn clockwise 
				leftMotor.setSpeed(ROTATE_SPEED);
				rightMotor.setSpeed(ROTATE_SPEED);
				leftMotor.rotate(convertAngle(lab_3.WHEEL_RADIUS, lab_3.TRACK, theta), true);
				rightMotor.rotate(-convertAngle(lab_3.WHEEL_RADIUS, lab_3.TRACK, theta), false);
				}
		navigating=false;
	}
	
	public void avoid(){
		float[] sample={0};

		int count = 0;
		//navigates around the obstacle (p-type controller, wall follower) 
		//breaks loop after certain time 
		while(true){
			count++;
			if(count>12000){break;}
			USSensor.fetchSample(sample, 0);

			// rudimentary filter - toss out invalid samples corresponding to null
			// signal.
			// (n.b. this was not included in the Bang-bang controller, but easily
			// could have).
			//
			if (sample[0]*100 >= 255 && filterControl < FILTER_OUT) {
				// bad value, do not set the distance var, however do increment the
				// filter value
				filterControl++;
			} else if (sample[0]*100 >= 255) {
				// We have repeated large values, so there must actually be nothing
				// there: leave the distance alone
			} else {
				// distance went below 255: reset filter and leave
				// distance alone.
				filterControl = 0;
			}
			
			
			if (sample[0]*100 <= 22 ){ //if the distance is under 22 cm, rotate clockwise
				int speed = (int) (-1/20 * sample[0]*100 -300); //inversely proportionate speed function 
				leftMotor.setSpeed(speed);
				rightMotor.setSpeed(CONSTANT_WHEEL_SPEED);
				leftMotor.forward();
				rightMotor.backward();
			}else{ 
			if (sample[0]*100 >= 40) { //if the distance read is great, set the max distance 40 
				sample[0] = (float) 0.40;
			}
			int speed = (int) (20 *sample[0]*100 - 300); // function for speed if distance is above 22 cm (function generated from two
											// point, 0 speed at 15 cm and 300 speed at 30cm (set Band Center)
		leftMotor.setSpeed(CONSTANT_WHEEL_SPEED);        
			rightMotor.setSpeed(speed);
			leftMotor.forward();
			rightMotor.forward();
			}
		}
		leftMotor.stop();
		rightMotor.stop();
	}
	//returns true if another thread has called travelTo() or turnTo(), false otherwise
	public boolean isNavigating(){
		return navigating;
	}
	//converts amount of rotation based on the wheel radius
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
	//calculates the rotations need to get to a certain angle 
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}
