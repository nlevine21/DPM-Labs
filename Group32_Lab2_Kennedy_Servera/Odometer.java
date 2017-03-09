/*
 * Odometer.java
 */

package lab_2;

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
					double leftDistance, rightDistance, deltaDistance, deltaTheta, dx, dy;
					//Gets tachometer values of left and right wheels 
					int newLeftTach = leftMotor.getTachoCount();
					int newRightTach = rightMotor.getTachoCount();
					//Calculate distance traveled by left and right wheels
					leftDistance = Math.PI*Lab2.WHEEL_RADIUS*(newLeftTach-leftMotorTachoCount)/180;	
					rightDistance = Math.PI*Lab2.WHEEL_RADIUS*(newRightTach- rightMotorTachoCount)/180;
					//Overrides old tachometer values with new ones 
					leftMotorTachoCount= newLeftTach;
					rightMotorTachoCount= newRightTach;
					//Calculates magnitude of the displacement vector
					deltaDistance = 0.5*(leftDistance + rightDistance);	
					//Calculates change in direction 
					deltaTheta = (leftDistance-rightDistance)/Lab2.TRACK;
					//Overrides old theta value
					theta += deltaTheta;		
					//Calculates the change in x and y values of displacement vector
				    dx = deltaDistance * Math.sin(theta);					
					dy = deltaDistance * Math.cos(theta);		
					//Overrides old x and y values with new ones
					x = x + dx;											
					y = y + dy;	
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
			if (update[2]){
				//Change theta from radians to degrees 
				if ((360*theta/(2*Math.PI))%360 >= 0){//When theta is positive take integer remainder of 360 degrees
				position[2] = (360*theta/(2*Math.PI))%360;
				}
				else if ((360*theta/(2*Math.PI))%360 < 0){//When theta is negative wrap around to 360 degrees
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
			if (update[2]){
				//Change theta from radians to degrees 
				if ((360*theta/(2*Math.PI))%360 >= 0){//When theta is positive take integer remainder of 360 degrees
				position[2] = (360*theta/(2*Math.PI))%360;
				}
				else if ((360*theta/(2*Math.PI))%360 < 0){//When theta is negative wrap around to 360 degrees
				position[2] = (360*theta/(2*Math.PI))%360 + 360;	
				}
			}
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