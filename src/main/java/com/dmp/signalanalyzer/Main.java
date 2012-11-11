/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmp.signalanalyzer;

import com.dmp.signalanalyzer.analysis.ASignalAnalysis;
import com.dmp.signalanalyzer.analysis.MaximumAnalysis;
import com.dmp.signalanalyzer.analysis.MeanAnalysis;
import com.dmp.signalanalyzer.analysis.MedianAnalysis;
import com.dmp.signalanalyzer.analysis.NinetiethPercentileAnalysis;
import com.dmp.signalanalyzer.utils.Logger;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.List;

/**
 *
 * @author paco
 */
public class Main {
    static Logger logger = new Logger();
    static Logger.LOGLEVEL logLevel = Logger.LOGLEVEL.DEBUG;
    
    static double stepMult = 1d/40;
    static double winMult = 1d/20; 

    static String positionsFileName;
    static String fstFileName;
    static Float selectionPosition = null;
    private static String outputFile = null;
    //---------------------------
    static List<Double> positions;
    static List<Float> fstValues;
    // Several Signal Analysis and results
    static ASignalAnalysis[] sigAn;
    static List<Block>[] blocks;
    static List<Integer>[] overBlocks;
    

    public static void main(String[] args) {
        logger.setLogLevel(logLevel);
        run(args);
    }

    private static void run(String[] args) {
        if (parseInput(args)) {
            try {
                int analysis_number = 4;
                positions = InputLoader.loadPositions(positionsFileName);
                fstValues = InputLoader.loadFst(fstFileName);
                double maxPosition = positions.get(positions.size()-1);
                double step = maxPosition * stepMult;
                logger.info(String.format("Performing analysis using step: %s", step));
                double window = maxPosition * winMult;
                logger.info(String.format("Performing analysis using window: %s", window));
                

                // Two type of analysis
                sigAn = new ASignalAnalysis[analysis_number];
                sigAn[0] = new MeanAnalysis();
                sigAn[1] = new NinetiethPercentileAnalysis();
                sigAn[2] = new MaximumAnalysis();
                sigAn[3] = new MedianAnalysis();
                //Prepare result containers
                blocks = new List[analysis_number];
                overBlocks = new List[analysis_number];

                // Run Each analysis
                for (int i = 0; i < sigAn.length; i++) {
                    ASignalAnalysis sa = sigAn[i];

                    // set Analysis input
                    sa.setPositions(positions);
                    sa.setSignal(fstValues);
                    sa.setStep(step);
                    sa.setWindow(window);


                    // execute
                    logger.debug(String.format("Starting %s analysis...", sa.getName()));
                    sa.runAnalysis();
                    logger.debug(String.format("%s analysis done, getting top 5perc...", sa.getName()));
                    overBlocks[i] = sa.topFivePercent();
                    logger.debug(String.format("%s got %s blocks suspected for selection...", sa.getName(),overBlocks[i].size()));
                    blocks[i] = sa.getBlocks();

                }
                manageOutput();

            } catch (FileNotFoundException e) {
                System.err.println(e.getMessage());
            }

        }
    }

    private static boolean parseInput(String[] args) {
        int i = 0;
        int paramNum = args.length;
        Boolean error = false;


        while (i < paramNum) {

            String param = args[i];


            if (param.equalsIgnoreCase("--positions")) {
                if (i + 1 < paramNum) {

                    positionsFileName = args[i + 1];
                    i++;//skip filename
                }

            } else if (param.equalsIgnoreCase("--fst")) {
                //one more parameter is needed to speify filename
                if (i + 1 < paramNum) {
                    fstFileName = args[i + 1];

                    i++;//skip filename
                }
            } else if (param.equalsIgnoreCase("--step")) {
                //one more parameter is needed to specify step
                if (i + 1 < paramNum) {
                    stepMult = Double.valueOf(args[i + 1]).doubleValue();

                    i++;//skip 
                }
            } else if (param.equalsIgnoreCase("--win")) {
                //one more parameter is needed to specify window
                if (i + 1 < paramNum) {
                    winMult = Double.valueOf(args[i + 1]).doubleValue();

                    i++;//skip 
                }

            } else if (param.equalsIgnoreCase("-Sp")) {
                //Load selected snp position 
                if (i + 1 < paramNum) {
                    selectionPosition = new Float(args[i + 1]);
                    i++;
                }
            } else if (param.equalsIgnoreCase("--outfile")) {
                //Load selected snp position 
                if (i + 1 < paramNum) {
                    outputFile = args[i + 1];
                    i++;
                }

            } else if (param.equalsIgnoreCase("--help")) {
                System.out.println(getHelp());
                System.exit(1);
            }




            //increase index
            i++;

        }

        // check for mandatory parameters
        if (outputFile == null) {
            System.err.println("You must specify output directory!");
            error = true;
        }

        if (positionsFileName == null) {
            System.err.println("You must specify SNPs positions filename!");
            error = true;
        }

        if (fstFileName == null) {
            System.err.println("You must specify Signal filename!");
            error = true;
        }

        if (error) {
            System.err.println("Incorrect parameters detected!");
            System.err.println(getHelp());
        }

        return !error;
    }

    private static String getHelp() {
        String help = "";

        help += ("Parameters format: \n"
                + "--positions <positions_filename> --fst <fst_filename>\n"
                + "--outfile <path>\n"
                + String.format("[--step <step_multiplier> (def: %s)] [--win <win_multiplier> (def: %s)]\n", stepMult, winMult)
                + "[-Sp <selection_position> (only to test analyzer when selection position in known)]\n");
        help += ("--help shows this help message\n");
        // help += (String.format("If not specified: step=%sL, win=%sL (where L is the number of loci, not the number of SNPs)",stepMult,winMult));

        return help;
    }

    /**
     * For each block print distance_from_selection,fst_value for each analysis
     */
    private static void printAnalysisResultToFile() {
        String SEPARATOR = ",";
        String NEW_LINE = "\n";
        
        
        // blocks number
        int blocks_number = blocks[0].size();



        String fileName = outputFile;


        try {
            // Create file 
            FileWriter fstream = new FileWriter(fileName);
            BufferedWriter out = new BufferedWriter(fstream);

            System.out.println("Writing " + fileName + "...");

            // print headers
            // file hyyeaders: BlockID, start, stop, distance, value1,..,value n, sel1,.. seln 
            String headers = getHeaders(SEPARATOR);
            out.write(headers + NEW_LINE);

            // Print values for each block
            for (int i = 0; i < blocks_number; i++) {
                // print distance from selection (always use list 0)
                out.write(blocks[0].get(i).index + SEPARATOR);
                out.write(blocks[0].get(i).start_position + SEPARATOR);
                out.write(blocks[0].get(i).stop_position + SEPARATOR);

                if (selectionPosition != null) {
                    out.write(blocks[0].get(i).distance_from_selection(selectionPosition.floatValue()) + SEPARATOR);
                } else {
                    out.write(SEPARATOR);
                }

                // print value for each type of analysis
                for (int j = 0; j < sigAn.length; j++) {
                    Block b = blocks[j].get(i);
                    out.write(b.value + SEPARATOR);
                }

                // print value for each type of analysis
                for (int j = 0; j < sigAn.length; j++) {
                    Block b = blocks[j].get(i);
                    if (b.selected) {
                        out.write("1" + SEPARATOR);
                    } else {
                        out.write("0" + SEPARATOR);
                    }
                }


                out.write(NEW_LINE);
            }

            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error writing " + fileName + "!");
        }

    }

    private static String getHeaders(String separator) {
        // file hyyeaders: BlockID, start, stop, distance, value1,..,value n, sel1,.. seln 
        String out;
        out = "BlockID" + separator
                + "Start" + separator
                + "Stop" + separator
                + "Distance" + separator;

        // print each SignalAnalysis Name
        for (ASignalAnalysis sa : sigAn) {
            out += (sa.getName() + " val." + separator);
        }

        for (ASignalAnalysis sa : sigAn) {
            out += (sa.getName() + " sel." + separator);
        }


        return out;

    }

    private static void manageOutput() {

        if (outputFile == null) {
            System.err.println("Error you must specify output directory!");
        } else {

            printAnalysisResultToFile();

        }

    }
}
