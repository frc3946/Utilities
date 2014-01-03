package org.usfirst.frc3946.Utilities;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//You may want to add this class to a package, we recommend:
//package org.usfirst.frc3946.Utilities;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.parsing.IInputOutput;

/**
 * A nearly drop in replacement for Joystick using a Logitech Dual Action G-UF13A Controller
 * @author Gustave Michel
 */
public class LogitechController extends GenericHID implements IInputOutput {
    
    private DriverStation m_ds;
    private final int m_port;
    
    /**
     * Represents an analog axis on a joystick.
     */
    public static class AxisType {
        
        /**
         * The integer value representing this enumeration
         */
        public final int value;
        private static final int kLeftX_val = 1;
        private static final int kLeftY_val = 2;
        private static final int kRightX_val = 3;
        private static final int kRightY_val = 4;
        private static final int kDLeftRight_val = 5;
        private static final int kDUpDown_val = 6;
        
        private AxisType(int value) {
            this.value = value;
        }
        
        /**
         * Axis: Left X
         */
        public static final AxisType kLeftX = new AxisType(kLeftX_val);
        
        /**
         * Axis: Left Y
         */
        public static final AxisType kLeftY = new AxisType(kLeftY_val);
        
        /**
         * Axis: Right X
         */
        public static final AxisType kRightX = new AxisType(kRightX_val);
        
        /**
         * Axis: Right Y
         */
        public static final AxisType kRightY = new AxisType(kRightY_val);
        
        /**
         * Axis: D-Pad Left-Right
         */
        public static final AxisType kDLeftRight = new AxisType(kDLeftRight_val);
        
        /**
         * Axis: D-Pad Up-Down
         */
        public static final AxisType kDUpDown = new AxisType(kDUpDown_val);
    }
    
    /**
     * Represents a digital button on a joystick.
     */
    public static class ButtonType {
        
        /**
         * The integer value representing this enumeration
         */
        public final int value;
        private static final int k1_val = 1;
        private static final int k2_val = 2;
        private static final int k3_val = 3;
        private static final int k4_val = 4;
        private static final int kLeftBumper_val = 5;
        private static final int kRightBumper_val = 6;
        private static final int kLeftTrigger_val = 7;
        private static final int kRightTrigger_val = 8;
        private static final int kLeft_val = 9;
        private static final int kRight_val = 10;
        private static final int kLeftJoystick_val = 11;
        private static final int kRightJoystick_val = 12;
        
        private ButtonType(int value) {
            this.value = value;
        }
        
        /**
         * Button: 1
         */
        public static final ButtonType k1 = new ButtonType(k1_val);
        
        /**
         * Button: 2
         */
        public static final ButtonType k2 = new ButtonType(k2_val);
        
        /**
         * Button: 3
         */
        public static final ButtonType k3 = new ButtonType(k3_val);
        
        /**
         * Button: 4
         */
        public static final ButtonType k4 = new ButtonType(k4_val);
        
        /**
         * Button: Left Bumper
         */
        public static final ButtonType kLeftBumper = new ButtonType(kLeftBumper_val);
        
        /**
         * Button: Right Bumper
         */
        public static final ButtonType kRightBumper = new ButtonType(kRightBumper_val);
        
        /**
         * Button: Left Trigger
         */
        public static final ButtonType kLeftTrigger = new ButtonType(kLeftTrigger_val);
        
        /**
         * Button: Right Trigger
         */
        public static final ButtonType kRightTrigger = new ButtonType(kRightTrigger_val);
        
        /**
         * Button: Left Center Button, 9
         */
        public static final ButtonType kLeft = new ButtonType(kLeft_val);
        
        /**
         * Button: Right Center Button, 10
         */
        public static final ButtonType kRight = new ButtonType(kRight_val);
        
        /**
         * Button: Left Joystick
         */
        public static final ButtonType kLeftJoystick = new ButtonType(kLeftJoystick_val);
        
        /**
         * Button: Right Joystick
         */
        public static final ButtonType kRightJoystick = new ButtonType(kRightJoystick_val);
    }
    
    
    /**
     * Constructor
     * @param port USB Port on DriverStation
     */
    public LogitechController(int port) {
        super();
        m_port = port;
        m_ds = DriverStation.getInstance();
    }
    
    /**
     * Get Value from an Axis
     * @param axis Axis Number
     * @return Value from Axis (-1 to 1)
     */
    public double getRawAxis(int axis) {
        return m_ds.getStickAxis(m_port, axis);
    }
    
    /**
     * Get Value from an Axis
     * @param axis AxisType
     * @return Value from Axis (-1 to 1)
     */
    public double getAxis(AxisType axis) {
        return getRawAxis(axis.value);
    }
    
    /**
     * Retrieve value for X axis
     * @param hand Hand associated with the Joystick
     * @return Value of Axis (-1 to 1)
     */
    public double getX(Hand hand) {
        if(hand.value == Hand.kRight.value) {
            return getAxis(AxisType.kRightX);
        } else if(hand.value == Hand.kLeft.value) {
            return getAxis(AxisType.kLeftX);
        } else {
            return 0;
        }
    }
    
    /**
     * Retrieve value for Y axis
     * @param hand Hand associated with the Joystick
     * @return Value of Axis (-1 to 1)
     */
    public double getY(Hand hand) {
        if(hand.value == Hand.kRight.value) {
            return getAxis(AxisType.kRightY);
        } else if(hand.value == Hand.kLeft.value) {
            return getAxis(AxisType.kLeftY);
        } else {
            return 0;
        }
    }
    
    /**
     * Unused
     * @param hand Unused
     * @return 0
     */
    public double getZ(Hand hand) {
        return 0;
    }
    
    /**
     * Unused
     * @return 0
     */
    public double getTwist() {
        return 0;
    }
    
    /**
     * Unused
     * @return 0
     */
    public double getThrottle() {
        return 0;
    }
    
    /**
     * Gets value from a button
     * @param button number of the button 
     * @return State of the button
     */
    public boolean getRawButton(int button) {
        return ((0x1 << (button - 1)) & m_ds.getStickButtons(m_port)) != 0;
    }
    
    /**
     * Get Value from a button
     * @param button Button Type
     * @return 
     */
    public boolean getButton(ButtonType button) {
        return getRawButton(button.value);
    }
    
    /**
     * Get Trigger Button
     * @param hand Hand associated with button
     * @return false
     */
    public boolean getTrigger(Hand hand) {
        if(hand == Hand.kLeft) {
            return getButton(ButtonType.kLeftTrigger);
        } else if(hand == Hand.kRight) {
            return getButton(ButtonType.kRightTrigger);
        } else {
            return false;
        }
    }
    
    /**
     * Get Button from Joystick
     * @param hand hand associated with the button
     * @return Button Status (true or false)
     */
    public boolean getTop(Hand hand) {
        if(hand == Hand.kRight) {
            return getButton(ButtonType.kRightJoystick);
        } else if(hand == Hand.kLeft) {
            return getButton(ButtonType.kLeftJoystick);
        } else {
            return false;
        }
    }
    
    /**
     * Get Value from Back buttons
     * @param hand hand associated with the button
     * @return state of left or right 
     */
    public boolean getBumper(Hand hand) {
        if(hand == Hand.kLeft) {
            return getButton(ButtonType.kLeftBumper);
        } else if(hand == Hand.kRight) {
            return getButton(ButtonType.kRightBumper);
        } else {
            return false;
        }
    }
}
