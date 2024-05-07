import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import components.map.Map;
import components.map.Map1L;
import components.queue.Queue;
import components.queue.Queue1L;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * Put a short phrase describing the program here.
 *
 * @author dylan earl
 *
 */
public final class Glossary {

    /**
     * No argument constructor--private to prevent instantiation.
     */
    private Glossary() {
    }

    /**
     * Prints the beginning of the main html page.
     *
     * @param fileOut
     *            -file to be printed to
     *
     */
    private static void printMainOpener(SimpleWriter fileOut) {

        fileOut.println("<html>");
        fileOut.println("<head>");
        fileOut.println("<title>" + "Dylan's Glossary" + "</title>");
        fileOut.println("</head>");
        fileOut.println("<body>");
        fileOut.println("<h2>" + "Dylan's Glossary" + "</h2>");
        fileOut.println("<hr />");
        fileOut.println("<h3>Index</h3>");
        fileOut.println("<ul>");

    }

    /**
     * Compare {@code String}s in lexicographic order. I've been told that I can
     * ignore the serializable error since that was not discussed in class
     */
    private static class StringLT implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    }

    /**
     * Prints the ending of the main html page.
     *
     * @param fileOut
     *            -file to be printed to
     */
    private static void printMainCloser(SimpleWriter fileOut) {

        fileOut.println("</ul>");
        fileOut.println("</body>");
        fileOut.println("</html>");

    }

    /**
     * Prints the ending of a sub-html page.
     *
     * @param fileOut
     *            -file to be printed to
     *
     */
    private static void printSubCloser(SimpleWriter fileOut) {

        fileOut.println("<hr />");
        fileOut.println("<p>Return to <a href=\"" + "index" + ".html\">"
                + "index" + "</a>.</p>");
        fileOut.println("</body>");
        fileOut.println("</html>");

    }

    /**
     * Prints given map to the console to debug and check if it looks correct.
     *
     * @param map
     *            -the map the method is printing to console
     * @param out
     *            -output to console
     * @ensures -map is printed to console line by line, then as a whole
     *
     */
    private static void printMap(Map<String, String> map, SimpleWriter out) {

        /*
         * DEBUG: This was to check if all words and definitions were correct
         * (they were not)
         */

        Map<String, String> temp = map.newInstance();
        temp.transferFrom(map);

        while (temp.size() > 0) {
            Map.Pair<String, String> pair = temp.removeAny();
            out.println(pair);
            map.add(pair.key(), pair.value());
        }
        out.println();
        out.println(map);
        out.println();
    }

    /**
     *
     *
     * Iterates through each word of the given definition.
     *
     * If the word within the definition is another word defined within the
     * glossary, it must be linked AND printed.
     *
     * Otherwise, the word is just printed.
     *
     * @param definition
     *            -the string for the definition
     *
     * @param subFile
     *            -print to main index.html file
     *
     * @param definedWords
     *            - words that have already been defined
     *
     * @ensures -definitions are printed as given, except with links to other
     *          words that are already defined in the index if they are used
     *          within a definition
     *
     * @assert -definition exists
     * @assert -definedWords exists
     */
    private static void printDefinition(String definition, SimpleWriter subFile,
            Set<String> definedWords) {

        assert definition != null : "definition must exist";
        assert definedWords != null : "defined words must exist";

        int startIndex = 0;
        int endIndex = 0;

        while (startIndex < definition.length()) {
            // Find the index of the next space
            endIndex = definition.indexOf(' ', startIndex);
            if (endIndex == -1) {
                endIndex = definition.length();
            }
            // Extract the word from the definition
            String word = definition.substring(startIndex, endIndex);

            // Remove any trailing commas
            if (word.endsWith(",")) {
                word = word.substring(0, word.length() - 1);
            }

            if (definedWords.contains(word)) {
                // link the word's definition and print the word
                subFile.print(
                        "<a href=\"" + word + ".html\">" + word + "</a> ");
            } else {
                subFile.print(word + " ");
            }

            // Move to the next word
            startIndex = endIndex + 1;
        }

    }

    /**
     * Creates sub-pages for main index.
     *
     * Sub-pages lists the word and definition given the provided standards in
     * the lab.
     *
     * If the definition of a word contains another defined word in the
     * glossary, the other defined word's .html page must be linked.
     *
     * Sub pages also link back to the main index.
     *
     * Finally, once the pages are created, print the links to the main index
     * page, in alphabetical order
     *
     * @param map
     *            -the map(pairs) of words and definitions
     *
     * @param fileOut
     *            -print to main index.html file
     *
     * @param outputFolderName
     *            -the name of the folder pages should save to
     *
     * @ensures -sub pages have links to the main index
     * @ensures -definitions align with words
     * @ensures -if a word used in a definition is another defined word in the
     *          index, it is linked
     * @ensures -links to sub-pages are created to the main index, and that they
     *          are in alphabetical order
     *
     *
     * @assert map exists
     */
    private static void createPages(Map<String, String> map,
            SimpleWriter fileOut, String outputFolderName) {

        assert map != null : "ERROR: File cannot be null";

        int size = map.size();
        Set<String> definedWords = new Set1L<>();
        Queue<String> definedWordsSorted = new Queue1L<>();

        Map<String, String> temp = map.newInstance();
        temp.transferFrom(map);

        while (temp.size() > 0) { //get all defined words into a set and queue
            Map.Pair<String, String> p = temp.removeAny();

            definedWords.add(p.key());
            //create list of defined words
            definedWords.add(p.key() + ",");
            //in case there is a comma after the word
            definedWords.add(p.key() + ".");
            //same with periods
            definedWordsSorted.enqueue(p.key());
            //create queue of defined words so they can be sorted for the main page

            map.add(p.key(), p.value());
        }

        for (int i = 0; i < size; i++) { //for every pair in file{

            //extract one pair
            Map.Pair<String, String> pair = map.removeAny();

            //make a new sub file with the name of the word
            String subFileName = pair.key();
            Path subFilePath = Paths.get(outputFolderName,
                    subFileName + ".html");
            SimpleWriter subFile = new SimpleWriter1L(subFilePath.toString());

            //print html stuff
            subFile.println("<html>");
            subFile.println("<head>");
            subFile.println("<title>" + pair.key() + "</title>");
            subFile.println("</head>");
            subFile.println("<body>");
            subFile.println("<h2><b><i><font color=\"red\">" + pair.key()
                    + "</font></i></b></h2>");
            subFile.print("<blockquote>");

            String definition = pair.value();

            //prints definition line by line to check for existing words in index
            printDefinition(definition, subFile, definedWords);

            subFile.print("</blockquote>");
            subFile.println();

            printSubCloser(subFile); //closer
            subFile.close();

        }

        Comparator<String> cs = new StringLT(); //get a-z sort
        definedWordsSorted.sort(cs); //sort defined words a-z

        int length = definedWordsSorted.length();
        for (int i = 0; i < length; i++) { //for every word defined
            String wordSorted = definedWordsSorted.dequeue();
            fileOut.println("<li><a href=\"" + wordSorted + ".html\">"
                    + wordSorted + "</a></li>"); //make new link to it on main glossary
        }

    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        /*
         * setup input and output streams
         */
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();

        /*
         * get input and output file
         */
        out.println("Enter input file name (including .txt): ");
        String inFile = in.nextLine();
        SimpleReader fileIn = new SimpleReader1L(inFile);

        String outputFolderName = "";
        boolean valid = false;
        while (!valid) {
            out.println("Enter the name of the output folder: ");
            outputFolderName = in.nextLine();
            /*
             * I had to look this up, hopefully this implementation with Paths
             * and Files is acceptable :) I didn't see any resources on how to
             * do this in the class or class materials
             */
            Path outputFolderPath = Paths.get(outputFolderName);
            if (Files.exists(outputFolderPath)
                    && Files.isDirectory(outputFolderPath)) {
                valid = true;
            } else {
                out.println("Error: The specified folder does not exist or is"
                        + " not a directory. Enter a valid folder name.");
            }
        }

        /*
         * create output file (named index)
         */
        Path outFile = Paths.get(outputFolderName, "index.html");
        SimpleWriter fileOut = new SimpleWriter1L(outFile.toString());
        //  SimpleWriter fileOut = new SimpleWriter1L(outFile);
        out.println();
        out.println("\t-Creating index.html");
        out.println();
        /*
         * read input file into pairs (definition, word)
         */
        out.println("\t-Sorting file into word-definition pairs");
        out.println();
        Map<String, String> map = new Map1L<>();

        while (!fileIn.atEOS()) {

            String word = fileIn.nextLine();
            String definition = "";
            String line = fileIn.nextLine();
            while (!line.isEmpty()) {
                definition += line + "\n";
                line = fileIn.nextLine();
            }
            map.add(word, definition);
        }

//        /*
//         * DEBUG: print map to console (by pair) to see if the map is read in
//         * correctly
//         */
//
//        out.println(
//                "Printing words and definitions to console (for debugging purposes)");
//        out.println();
//
//        printMap(map, out);

        out.println("\t-Creating " + outFile + ".html contents");
        out.println();
        printMainOpener(fileOut);

        out.println("\t-Creating the pages for words in glossary");
        out.println();
        createPages(map, fileOut, outputFolderName);
        //create sub-pages for every pair in map

        out.println("\t-Finishing up the " + outFile + ".html code");
        out.println();
        printMainCloser(fileOut);
        out.println("Done! The generated glossary is available as \"" + outFile
                + ".html\"");

        fileIn.close();
        in.close();
        out.close();
    }

}
