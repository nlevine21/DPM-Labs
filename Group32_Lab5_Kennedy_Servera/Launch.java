package Ball_Launch;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Launch {
	private EV3LargeRegulatedMotor rightMotor;
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor catapultMotor;
	//constants
	private final int RELEASE_ACCELERATION = 10000;
	private final int DAMPENING_FACTOR = 460;
	private final int LOWERING_SPEED = 100;
	private final int CATAPULT_ANGLE = 92;
	private final int ACCELERATION = 100;
	private final double WHEEL_RADIUS = 2.1;
	private final double WHEEL_WIDTH = 15.4;
	private final double ROTATE_ANGLE = 18.44;
	private final int LEFT_BUTTON = 16;
	private final int MIDDLE_BUTTON = 2;
	private final int RIGHT_BUTTON = 8;

	public Launch(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
			EV3LargeRegulatedMotor catapultMotor) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.catapultMotor = catapultMotor;

	}
	// rotate robot depending on target then release catapult, 
	// returning back to original position
	public void release(int target) {
		//set catapult acceleration and speed
		catapultMotor.setAcceleration(RELEASE_ACCELERATION);
		catapultMotor.setSpeed(catapultMotor.getMaxSpeed() - DAMPENING_FACTOR);
		
		// turn to target and release, then return to original orientation
		if (target == LEFT_BUTTON) {// left
			rotate("left");
			catapultMotor.rotate(CATAPULT_ANGLE);
			rotate("right");
		} else if (target == MIDDLE_BUTTON) {// middle
			catapultMotor.rotate(CATAPULT_ANGLE);
		} else if (target == RIGHT_BUTTON) {// right
			rotate("right");
			catapultMotor.rotate(CATAPULT_ANGLE);
			rotate("left");
		}
		// lower catapult 
		if (target == LEFT_BUTTON | target == MIDDLE_BUTTON | target == RIGHT_BUTTON) {
			catapultMotor.setSpeed(LOWERING_SPEED);
			catapultMotor.rotate(-CATAPULT_ANGLE);
		}
	}
	// orient robot to face desired target
	public void rotate(String direction) {
		// set acceleration
		leftMotor.setAcceleration(ACCELERATION);
		rightMotor.setAcceleration(ACCELERATION);
		// calculate angle wheels need to rotate
		int rotationAngle = convertAngle(WHEEL_RADIUS, WHEEL_WIDTH, ROTATE_ANGLE);
		// rotate wheels in determined direction 
		if (direction == "left") {
			leftMotor.rotate(-rotationAngle, true);
			rightMotor.rotate(rotationAngle, false);
		} else if (direction == "right") {
			leftMotor.rotate(rotationAngle, true);
			rightMotor.rotate(-rotationAngle, false);

		}
	}

	// converts amount of rotation based on the wheel radius
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	// calculates the rotations need to get to a certain angle
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}
