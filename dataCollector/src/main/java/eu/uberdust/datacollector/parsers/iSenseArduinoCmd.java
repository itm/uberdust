package eu.uberdust.datacollector.parsers;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 12/10/11
 * Time: 9:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class iSenseArduinoCmd {

    private int commandTotal;
    private static final int PART_1 = 4;
    private static final int PART_2 = 5;
    private static final int PART_3 = 6;
    private static final int HEX = 16;
    private static final int HUNDREDS = 100;
    private static final int TENS = 10;

    /**
     * Constructor.
     *
     * @param strLine the string to decode as command.
     */
    public iSenseArduinoCmd(final String strLine) {
        final String[] cmd = strLine.split("0x");
        commandTotal = 0;
        commandTotal += HUNDREDS * Integer.parseInt(cmd[PART_1].substring(0, cmd[PART_1].indexOf('|')), HEX);
        commandTotal += TENS * Integer.parseInt(cmd[PART_2].substring(0, cmd[PART_2].indexOf('|')), HEX);
        commandTotal += Integer.parseInt(cmd[PART_3].substring(0, cmd[PART_3].indexOf('|')), HEX);

    }

    @Override
    public String toString() {
        return String.valueOf(commandTotal);
    }

    /**
     * Returns the integer value of the command.
     *
     * @return int value of command
     */
    public int toInt() {
        return commandTotal;
    }
}
