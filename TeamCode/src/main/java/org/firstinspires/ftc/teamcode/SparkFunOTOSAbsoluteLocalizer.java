package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.FlightRecorder;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.OTOSdriver.SparkFunOTOS;

@Config
public class SparkFunOTOSAbsoluteLocalizer {
    public static Params PARAMS = new Params();
    SparkFunOTOS sensor;
    Telemetry telemetry;
    public SparkFunOTOSAbsoluteLocalizer() {
        sensor = hardwareMap.get(SparkFunOTOS.class, "sensor_otos");

        FlightRecorder.write("OTOS_PARAMS", PARAMS);
    }

    public Pose2d getPosition() {
        SparkFunOTOS.Pose2D OTOSPose = sensor.getPosition();

        return new Pose2d(OTOSPose.x, OTOSPose.y, OTOSPose.h);
    }

    public void setPosition(Pose2d pose) {
        sensor.setPosition(new SparkFunOTOS.Pose2D(pose.position.x, pose.position.y, pose.heading.real));
    }

    public PoseVelocity2d getVelocity() {
        SparkFunOTOS.Pose2D OTOSVelocity = sensor.getVelocity();

        return new PoseVelocity2d(new Vector2d(OTOSVelocity.x, OTOSVelocity.y), OTOSVelocity.h);
    }

    private void configureOtos() {
        sensor.setLinearUnit(SparkFunOTOS.LinearUnit.INCHES);
        sensor.setAngularUnit(SparkFunOTOS.AngularUnit.RADIANS);

        // Assuming you've mounted your sensor to a robot and it's not centered,
        // you can specify the offset for the sensor relative to the center of the
        // robot. The units default to inches and degrees, but if you want to use
        // different units, specify them before setting the offset! Note that as of
        // firmware version 1.0, these values will be lost after a power cycle, so
        // you will need to set them each time you power up the sensor. For example, if
        // the sensor is mounted 5 inches to the left (negative X) and 10 inches
        // forward (positive Y) of the center of the robot, and mounted 90 degrees
        // clockwise (negative rotation) from the robot's orientation, the offset
        // would be {-5, 10, -90}. These can be any value, even the angle can be
        // tweaked slightly to compensate for imperfect mounting (eg. 1.3 degrees).
        SparkFunOTOS.Pose2D offset = new SparkFunOTOS.Pose2D(PARAMS.X_OFFSET_INCHES, PARAMS.Y_OFFSET_INCHES, PARAMS.HEADING_OFFSET_DEGREES);
        sensor.setOffset(offset);

        // Here we can set the linear and angular scalars, which can compensate for
        // scaling issues with the sensor measurements. Note that as of firmware
        // version 1.0, these values will be lost after a power cycle, so you will
        // need to set them each time you power up the sensor. They can be any value
        // from 0.872 to 1.127 in increments of 0.001 (0.1%). It is recommended to
        // first set both scalars to 1.0, then calibrate the angular scalar, then
        // the linear scalar. To calibrate the angular scalar, spin the robot by
        // multiple rotations (eg. 10) to get a precise error, then set the scalar
        // to the inverse of the error. Remember that the angle wraps from -180 to
        // 180 degrees, so for example, if after 10 rotations counterclockwise
        // (positive rotation), the sensor reports -15 degrees, the required scalar
        // would be 3600/3585 = 1.004. To calibrate the linear scalar, move the
        // robot a known distance and measure the error; do this multiple times at
        // multiple speeds to get an average, then set the linear scalar to the
        // inverse of the error. For example, if you move the robot 100 inches and
        // the sensor reports 103 inches, set the linear scalar to 100/103 = 0.971
        sensor.setLinearScalar(1.0);
        sensor.setAngularScalar(1.0);

        // The IMU on the OTOS includes a gyroscope and accelerometer, which could
        // have an offset. Note that as of firmware version 1.0, the calibration
        // will be lost after a power cycle; the OTOS performs a quick calibration
        // when it powers up, but it is recommended to perform a more thorough
        // calibration at the start of all your programs. Note that the sensor must
        // be completely stationary and flat during calibration! When calling
        // calibrateImu(), you can specify the number of samples to take and whether
        // to wait until the calibration is complete. If no parameters are provided,
        // it will take 255 samples and wait until done; each sample takes about
        // 2.4ms, so about 612ms total
        sensor.calibrateImu();

        // Reset the tracking algorithm - this resets the position to the origin,
        // but can also be used to recover from some rare tracking errors
        sensor.resetTracking();

        // Get the hardware and firmware version
        SparkFunOTOS.Version hwVersion = new SparkFunOTOS.Version();
        SparkFunOTOS.Version fwVersion = new SparkFunOTOS.Version();
        sensor.getVersionInfo(hwVersion, fwVersion);

        // TODO: Convert to roadrunner FlightRecorder messages
        // telemetry.addLine(String.format("OTOS Hardware Version: v%d.%d", hwVersion.major, hwVersion.minor));
        // telemetry.addLine(String.format("OTOS Firmware Version: v%d.%d", fwVersion.major, fwVersion.minor));
    }

    public static class Params {
        public double X_OFFSET_INCHES = 0;
        public double Y_OFFSET_INCHES = 0;
        public double HEADING_OFFSET_DEGREES = 0;
    }
}
