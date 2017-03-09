package WallFollower;
import lejos.hardware.motor.*;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwidth;
	private final int motorLow, motorHigh, motorNormal;
	private int distance;
	private int error;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	
	public BangBangController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
							  int bandCenter, int bandwidth, int motorLow, int motorHigh, int motorNormal) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		this.motorNormal = motorNormal;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		leftMotor.setSpeed(motorNormal);				// Start robot moving forward
		rightMotor.setSpeed(motorNormal);
		leftMotor.forward();
		rightMotor.forward();
	}
	
	@Override
	public void processUSData (int distance) {
		this.distance = distance;
		error = distance - bandCenter; // setting an error value
		double speed = 1.5;  //speed multiplier (set under 3 which the motor limit) 
		int minDistance = (bandCenter-20);
		
		if(distance < minDistance){ // if the robot is too close to the wall rotate clockwise
			leftMotor.setSpeed((int) (200*speed)); 
			rightMotor.setSpeed((int) (200*speed));
			leftMotor.forward();
			rightMotor.backward();
		}else{
		if (Math.abs(error) <= bandwidth ){  //if the error is within bandwidth margin, go straight 
			leftMotor.setSpeed((int) (motorNormal*speed));
			rightMotor.setSpeed((int) (motorNormal*speed));
			leftMotor.forward();
			rightMotor.forward();
		}else if (error < (-1 * bandwidth) ){    //if its close but not too close that it reverses, turn right 
			leftMotor.setSpeed((int) (motorHigh*speed)); 
			rightMotor.setSpeed((int) (motorLow*speed));
			leftMotor.forward();
			rightMotor.forward();
		}else if (error > bandwidth )    // if the robot is too far from the wall turn left 
			leftMotor.setSpeed((int) (140*speed));
			rightMotor.setSpeed((int) (240*speed));
			leftMotor.forward();
			rightMotor.forward();
		}
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}