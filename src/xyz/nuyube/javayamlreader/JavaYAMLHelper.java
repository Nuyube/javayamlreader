package xyz.nuyube.javayamlreader;

import java.util.ArrayList;

public class JavaYAMLHelper {

    /**
     * Gets a value from a yaml key.
     * 
     * @param key  The key to search for
     * @param yaml The yaml to search in
     * @return The text of the value, if found. "" otherwise.
     */
    public static String getValueFromKey(String key, String yaml) {
        String[] lines = yaml.split("\n");
        for (String string : lines) {
            string = string.trim();
            if (string.startsWith(key)) {
                return string.substring(string.indexOf(':') + 1).trim();
            }
        }
        return "";
    }

    /**
     * Removes all comments and empty lines in a yaml file. Searching works by
     * deleting lines starting with "#".
     * 
     * @param yaml The yaml to search
     * @return The yaml file, without any comments or empty lines.
     */
    public static String removeAllComments(String yaml) {
        String[] lines = yaml.split("\n");

        for (int i = 0; i < lines.length; i++) {
            if (lines[i].trim().startsWith("#"))
                lines[i] = "";
        }
        ArrayList<String> validLines = new ArrayList<String>();
        for (String s : lines) {
            if (!s.isBlank()) {
                validLines.add(s);
            }
        }
        String reconstruction = "";
        for (String s : validLines) {
            reconstruction += s + "\n";
        }
        return removeTrailingEndline(reconstruction);
    }

    public static String[] getBlocks(String yaml) {
        yaml = removeAllComments(yaml);
        String yamlLeft = yaml;
        int iterationLimit = 400;
        ArrayList<String> blocks = new ArrayList<String>();
        while (!yamlLeft.isBlank()) {
            iterationLimit--;
            if (iterationLimit < 0)
                break;
            String block = getBlock("*", yamlLeft);
            if (!block.isBlank()) {
                yamlLeft = yamlLeft.substring(block.length());
                blocks.add(block);
            } else {
                return (String[]) blocks.toArray();
            }
        }
        return (String[]) blocks.toArray();
    }

    /**
     * Removes the first line of a block produced by getBlock.
     * 
     * @param yaml The yaml of the block to dename
     * @return The denamed block
     */
    public static String denameBlock(String yaml) {
        String[] lines = yaml.split("\n");
        String recon = "";
        for (int i = 1; i < lines.length; i++) {
            recon += lines[i] + "\n";
        }
        return removeTrailingEndline(recon);
    }

    /**
     * Gets the number of spaces preceding a line
     * 
     * @param line The line to test
     * @return The number of spaces preceding the line
     */
    public static int getIndent(String line) {
        int indentLevel = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ' ')
                indentLevel++;
            else
                break;
        }
        return indentLevel;
    }

    private static String removeTrailingEndline(String yaml) {
        while (yaml.endsWith("\n")) {
            yaml = yaml.substring(0, yaml.length() - 1);
        }
        return yaml;
    }

    /**
     * Gets a specific block from a larger block
     * 
     * @param blockName The name of the block, or * for any block
     * @param yaml      The upper block to search
     * @return The block, if found, or "".
     */
    public static String getBlock(String blockName, String yaml) {
        // Remove all comments and empty lines. Theoretically the first line is now our
        // block.
        yaml = removeAllComments(yaml);

        // If the blockName is null, set it to *
        if (blockName == null) {
            blockName = "*";
        }

        // Get our lines
        String[] lines = yaml.split("\n");
        String thisBlock = "";
        int yamlIndent = getIndent(lines[0]);
        // Our current line pointer
        int currentLine = 0;
        while (true) {
            // If we're off the scale return what we've got
            if (currentLine >= lines.length)
                return removeTrailingEndline(thisBlock);

            // Get the actual line
            String line = lines[currentLine];

            boolean correctIndent = line.startsWith(" ".repeat(yamlIndent));
            boolean correctName = blockName == "*" || line.trim().startsWith(blockName);

            // Get the block with the matching name
            if (correctIndent && correctName) {
                // Get our indent level
                int indentLevel = getIndent(line);

                // Add our line to the block
                thisBlock += line + "\n";

                // Simplifies the following code
                String indentation = " ".repeat(indentLevel + 1);
                // Get lines that start with the correct indent and add them to our block
                do {
                    // If we're off the scale return what we've got
                    if (++currentLine >= lines.length)
                        return thisBlock;
                    line = lines[currentLine];
                    if (line.startsWith(indentation))
                        thisBlock += line + "\n";
                } while (line.startsWith(indentation));
                return removeTrailingEndline(thisBlock);
            }
            // Add one to our line
            currentLine++;
        }
    }

    /**
     * Unindents a block based on the first line. You should dename your block
     * before you use this!
     * 
     * @param yaml The block to unindent.
     * @return The unindented block
     * @throws InvalidYamlException Thrown if the block somehow goes above the base
     *                              indent.
     */
    public static String unindentBlock(String yaml) throws InvalidYamlException {
        // If this block isn't indented, just return what we were given.
        if (!yaml.startsWith(" "))
            return yaml;
        // Else,
        else {
            // Calculate how many spaces are at the beginning of each line
            int startingSpaces = 0;
            // Keep adding one for each space
            while (yaml.startsWith(" ")) {
                yaml.substring(1);
                startingSpaces++;
            }
            // Get our lines
            String[] lines = yaml.split("\n");
            // Remove the spaces from the beginning, or except if there weren't enough (the
            // input was not a block, but greater yaml.)
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].startsWith(" ".repeat(startingSpaces))) {
                    lines[i] = lines[i].substring(startingSpaces);
                } else {
                    throw new InvalidYamlException("The input was not a block.");
                }
            }

            // Reconstruct our lines into yaml.
            String reconstruction = "";
            for (String l : lines) {
                reconstruction += l + "\n";
            }
            // Return it.
            return removeTrailingEndline(reconstruction);
        }
    }
}