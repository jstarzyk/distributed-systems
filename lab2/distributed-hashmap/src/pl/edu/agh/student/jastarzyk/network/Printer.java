package pl.edu.agh.student.jastarzyk.network;

public class Printer {

    public static final String ANSI_BRIGHT_BLACK  = "\u001B[90m";
    public static final String ANSI_BRIGHT_RED    = "\u001B[91m";
    public static final String ANSI_BRIGHT_GREEN  = "\u001B[92m";
    public static final String ANSI_BRIGHT_YELLOW = "\u001B[93m";
    public static final String ANSI_BRIGHT_BLUE   = "\u001B[94m";
    public static final String ANSI_BRIGHT_PURPLE = "\u001B[95m";
    public static final String ANSI_BRIGHT_CYAN   = "\u001B[96m";
    public static final String ANSI_BRIGHT_WHITE  = "\u001B[97m";
    public static final String ANSI_RESET         = "\u001B[0m";

    public static void printEvent(
            String eventHeader, String eventText, String eventColor,
            boolean lineBefore, boolean lineAfter) {
        if (lineBefore) System.out.println();
        System.out.print(eventColor);
        System.out.println(eventHeader);
        System.out.print(ANSI_RESET);
        System.out.println(eventText);
        if (lineAfter) System.out.println();
    }

}
