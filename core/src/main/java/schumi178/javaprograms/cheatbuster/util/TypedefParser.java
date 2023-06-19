package schumi178.javaprograms.cheatbuster.util;

import schumi178.javaprograms.cheatbuster.code.exception.DoesNotCompileException;
import schumi178.javaprograms.cheatbuster.code.exception.IncludeNotFoundException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class TypedefParser {

    public static List<String> getDefinedTypes(String statement) {
        List<String> types = new ArrayList<>();
        int bracketLevel = 0;
        boolean moreTypes = false;
        String potentialType = "";
        Character c1 = null;
        boolean exitLoop = false;
        boolean bracketsPassed = false;
        for(int i = statement.length() - 1; i >= 0; i--) {
            char c = statement.charAt(i);
            switch (c) {
                case ')':
                    bracketLevel++;
                    break;
                case '(':
                    bracketLevel--;
                    if(bracketLevel == 0)
                        bracketsPassed = true;
                    break;
                case ',':
                case ';':
                    moreTypes = true;
                    break;
                default:
                    if(bracketLevel < 1 || bracketsPassed) {
                        if (Character.isWhitespace(c) || c == '*') {
                            if (!potentialType.isEmpty()) {
                                if (potentialType.equals("__attribute__")) {
                                    moreTypes = true;
                                    potentialType = "";
                                    break;
                                }
                                types.add(potentialType);
                                potentialType = "";
                            }
                        } else if (Character.isLetterOrDigit(c) || c == '_' || c == '}') {
                            if (c1 != null && (Character.isWhitespace(c1) || c1 == ',')) {
                                if (!moreTypes) {
                                    exitLoop = true;
                                    break;
                                }
                            }
                            potentialType = c + potentialType;
                            moreTypes = false;
                        }
                    }
            }
            c1 = statement.charAt(i);
            if(exitLoop)
                break;
        }
        return types;
    }

    public static boolean isInclude(String line) {
        if(line.startsWith("#") && line.length() > 1) {
            for(int i = 1; i < line.length(); i++) {
                if(!Character.isWhitespace(line.charAt(i))) {
                    return line.substring(i).startsWith("include ");
                }
            }
        }
        return false;
    }

    private static void getTypedefsFromInclude(String fileName, List<String> includePaths, List<String> typedefs, List<String> filesIncluded) throws DoesNotCompileException {
        boolean fileFound = false;
        File file = null;
        for(String path: includePaths) {
            file = new File(path + (path.endsWith("/") ? "" : "/") + fileName);
            if(file.exists()) {
                fileFound = true;
                break;
            }
        }
        if(!fileFound) {
            throw new IncludeNotFoundException(fileName);
        }
        if(filesIncluded.contains(fileName)) {
            return;
        }
        filesIncluded.add(fileName);
        try {
            Scanner sc = new Scanner(file);
            boolean parsingTypedef = false;
            int bracketLevel = 0;
            int count = 0;
            List<String> currentTypedef = new ArrayList<>();
            while(sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if(parsingTypedef) {
                    long openingBracketCount = line.chars().filter(c -> c == '{').count();
                    bracketLevel += openingBracketCount;
                    long closingBracketCount = line.chars().filter(c -> c == '}').count();
                    bracketLevel -= closingBracketCount;
                    currentTypedef.add(line);
                    if(line.endsWith(";") && bracketLevel < 1) {
                        StringBuilder typedefBuilder = new StringBuilder();
                        for(String typedefLine: currentTypedef) {
                            typedefBuilder.append(typedefLine).append("\n");
                        }
                        typedefs.add(typedefBuilder.toString());
                        currentTypedef.clear();
                        parsingTypedef = false;
                    }
                    continue;
                } else if(line.startsWith("typedef ")) {
                    long openingBracketCount = line.chars().filter(c -> c == '{').count();
                    bracketLevel += openingBracketCount;
                    long closingBracketCount = line.chars().filter(c -> c == '}').count();
                    bracketLevel -= closingBracketCount;
                    parsingTypedef = true;
                    currentTypedef.add(line);
                    if(line.endsWith(";") && bracketLevel < 1) {
                        StringBuilder typedefBuilder = new StringBuilder();
                        for(String typedefLine: currentTypedef) {
                            typedefBuilder.append(typedefLine).append("\n");
                        }
                        typedefs.add(typedefBuilder.toString());
                        currentTypedef.clear();
                        parsingTypedef = false;
                    }
                } else if(isInclude(line)) {
                    if(line.contains("<") && line.contains(">")) {
                        getTypedefsFromInclude(line.substring(line.indexOf('<') + 1, line.indexOf('>')), includePaths, typedefs, filesIncluded);
                    } else if(line.chars().filter(c -> c == '\"').count() == 2) {
                        String childFileName = line.substring(line.indexOf('\"') + 1);
                        childFileName = childFileName.substring(0, childFileName.indexOf('\"'));
                        getTypedefsFromInclude(childFileName, includePaths, typedefs, filesIncluded);
                    } else throw new DoesNotCompileException("Błąd składni w dyrektywie #include (plik " + fileName + ", wiersz " + count + ")");
                }
                count++;
            }
        } catch (FileNotFoundException e) {
            throw new DoesNotCompileException("Nieznany plik: " + fileName);
        }
    }

    public static Map<String, List<String>> getTypedefStatements(String code, List<String> includePaths) throws DoesNotCompileException {
        List<String> filesIncluded = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(code, "\n");
        Map<String, List<String>> statements = new HashMap<>();
        while(tokenizer.hasMoreTokens()) {
            String line = tokenizer.nextToken().trim();
            if(isInclude(line)) {
                if(line.contains("<") && line.contains(">")) {
                    String fileName = line.substring(line.indexOf('<') + 1, line.indexOf('>'));
                    if(!statements.containsKey(fileName))
                        statements.put(fileName, new ArrayList<>());
                    getTypedefsFromInclude(fileName, includePaths, statements.get(fileName), filesIncluded);
                } else if(line.chars().filter(c -> c == '\"').count() == 2) {
                    String fileName = line.substring(line.indexOf('\"') + 1);
                    fileName = fileName.substring(0, fileName.indexOf('\"'));
                    if(!statements.containsKey(fileName))
                        statements.put(fileName, new ArrayList<>());
                    getTypedefsFromInclude(fileName, includePaths, statements.get(fileName), filesIncluded);
                }
            }
        }
        return statements;
    }

    public static List<String> getIncludedFiles(String code) {
        List<String> includedFiles = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(code, "\n");
        while(tokenizer.hasMoreTokens()) {
            String line = tokenizer.nextToken().trim();
            if(isInclude(line)) {
                if(line.contains("<") && line.contains(">")) {
                    String fileName = line.substring(line.indexOf('<') + 1, line.indexOf('>'));
                    includedFiles.add(fileName);
                } else if(line.chars().filter(c -> c == '\"').count() == 2) {
                    String fileName = line.substring(line.indexOf('\"') + 1);
                    fileName = fileName.substring(0, fileName.indexOf('\"'));
                    includedFiles.add(fileName);
                }
            }
        }
        return includedFiles;
    }
}
