package lab_3;

import lab_3.lab_3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Odometer extends Thread {
	// robot position
	private double x, y, theta;
	private int leftMotorTachoCount, rightMotorTachoCount;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;

	// lock object for mutual exclusion
	private Object lock;

	// default constructor
	public Odometer(EV3LargeRegulatedMotor leftMotor,EV3LargeRegulatedMotor rightMotor) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.x = 0.0;
		this.y = 0.0;
		this.theta = 0.0;
		this.leftMotorTachoCount = 0;
		this.rightMotorTachoCount = 0;
		lock = new Object();
	}

	// run method (required for Thread)
	public void run() {
		long updateStart, updateEnd;
		
		while (true) {
			updateStart = System.currentTimeMillis();
			//TODO put (some of) your odometer code here
			
		
				synchronized (lock) {
				/**
				 * Don't use the variables x, y, or theta anywhere but here!
				 * Only update the values of x, y, and theta in this block. 
				 * Do not perform complex math
				 * 
				 */
					double leftDistance, rightDistance, deltaDistance, deltaTheta, dx, dy; // constants
					// get the tachometer values
					int newLeftTach = leftMotor.getTachoCount();
					int newRightTach = rightMotor.getTachoCount();
					// calculates the proper distance each wheel needs to travel 
					leftDistance = Math.PI*lab_3.WHEEL_RADIUS*(newLeftTach-leftMotorTachoCount)/180;	
					rightDistance = Math.PI*lab_3.WHEEL_RADIUS*(newRightTach- rightMotorTachoCount)/180;
					//sets the new values to of the tachometer count to the current ones  
					leftMotorTachoCount= newLeftTach;
					rightMotorTachoCount= newRightTach;
					// calculates change in distance 
					deltaDistance = 0.5*(leftDistance + rightDistance);
					// calculates change in distance 
					deltaTheta = (leftDistance-rightDistance)/lab_3.TRACK;
					theta += deltaTheta; // set the new theta value to the old one 									
				    dx = deltaDistance * Math.sin(theta); //sets change in the x direction 					
					dy = deltaDistance * Math.cos(theta); //sets change in the y direction 
					x = x + dx;	//sets the new x value 
					y = y + dy;	 //sets the new y value
			}

			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	// accessors
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2]){ //set the theta value in degrees 
				if ((360*theta/(2*Math.PI))%360 >= 0){
				position[2] = (360*theta/(2*Math.PI))%360;
				}// if theta is negative make sure that it wraps around
				else if ((360*theta/(2*Math.PI))%360 < 0){
				position[2] = (360*theta/(2*Math.PI))%360 + 360;	
				}
			}
		}
	}

	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}

	// mutators
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}

	/**
	 * @return the leftMotorTachoCount
	 */
	public int getLeftMotorTachoCount() {
		return leftMotorTachoCount;
	}

	/**
	 * @param leftMotorTachoCount the leftMotorTachoCount to set
	 */
	public void setLeftMotorTachoCount(int leftMotorTachoCount) {
		synchronized (lock) {
			this.leftMotorTachoCount = leftMotorTachoCount;	
		}
	}

	/**
	 * @return the rightMotorTachoCount
	 */
	public int getRightMotorTachoCount() {
		return rightMotorTachoCount;
	}

	/**
	 * @param rightMotorTachoCount the rightMotorTachoCount to set
	 */
	public void setRightMotorTachoCount(int rightMotorTachoCount) {
		synchronized (lock) {
			this.rightMotorTachoCount = rightMotorTachoCount;	
		}
	}
}
