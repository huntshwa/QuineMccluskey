import java.io.FileNotFoundException;
import java.util.*;
import java.io.File;

public class Algorithm {

    private static int numInputs;
    private static int numOutputs;
    private static int numTerms;
    private static Term[] terms;
    private static ArrayList<ArrayList<String>> finalEquation = new ArrayList<>();

    public static void runAlgorithm() throws FileNotFoundException {
        read();

        //go through all outputs of the truth table
        for (int i = 0; i < numOutputs; i++) {
            ArrayList<Term> unorderedMinterms = new ArrayList<>();
            ArrayList<Term> realMinterms = new ArrayList<>();
            HashMap<String, int[]> primeImplicants = new HashMap<>();
            HashMap<Integer, ArrayList<Term>> minterms = new HashMap<>();

            //adding minterms and dont cares
            for (Term term : terms) {
                if (term.getOutput()[i].equals("1")) {
                    unorderedMinterms.add(term);
                    realMinterms.add(term);
                }
                if (term.getOutput()[i].equals("x"))
                    unorderedMinterms.add(term);
            }

            //early check for if an output has no solution
            if (unorderedMinterms.isEmpty()) {
                DisplayPanel.updateOutputs("No Equations");
                continue;
            }

            //ordering minterms into a hashmap for Quine-Mccluskey method based on amount of ones in binary representation
            for (int j = 0; j <= numInputs; j++) {
                minterms.put(j, new ArrayList<>());
            }

            for (Term term : unorderedMinterms) {
                int count = 0;
                for (int k = 0; k < term.getBinaryRep().length(); k++) {
                    if (term.getBinaryRep().charAt(k) == '1') count++;

                }

                minterms.get(count).add(term);
            }

            //loop which keeps on merging terms until no terms have been merged
            //once no more merges occur, the loop should finish and leave the prime implicants
            boolean iterate = true;
            while (iterate) {
                boolean merged = false;
                HashMap<Integer, ArrayList<Term>> newMinterms = new HashMap<>();

                for (ArrayList<Term> list : minterms.values()) {
                    for (Term term : list) {
                        term.updateUsed(false);
                    }
                }

                for (int j = 0; j <= numInputs; j++) {
                    newMinterms.put(j, new ArrayList<>());
                }

                for (int j = 0; j < numInputs; j++) {
                    for (int k = 0; k < minterms.get(j).size(); k++) {
                        Term curTerm = minterms.get(j).get(k);
                        for (int l = 0; l < minterms.get(j + 1).size(); l++) {
                            Term compTerm = minterms.get(j + 1).get(l);
                            if (Term.onesAligned(curTerm, compTerm) && Term.dashesAligned(curTerm, compTerm)) {
                                int index = Term.misalignedOnesIndex(curTerm, compTerm);
                                String newBinRep = curTerm.getBinaryRep().substring(0, index) + "-" + curTerm.getBinaryRep().substring(index + 1);
                                int[] addOgTerms = mergeIntArray(curTerm.getOriginalTerms(), compTerm.getOriginalTerms());

                                Term addTerm = new Term(newBinRep, curTerm.getOutput(), addOgTerms);

                                boolean seen = false;
                                for (ArrayList<Term> list : newMinterms.values()) {
                                    for (Term term : list) {
                                        if (term.getBinaryRep().equals(addTerm.getBinaryRep())) {
                                            seen = true;
                                            break;
                                        }
                                    }
                                }

                                if (!seen) newMinterms.get(j).add(addTerm);
                                merged = true;
                                curTerm.updateUsed(true);
                                compTerm.updateUsed(true);
                            }
                        }
                    }

                }

                iterate = merged;

                for (ArrayList<Term> termList : minterms.values()) {
                    for (Term term : termList) {
                        if (!term.getUsed()) {
                            primeImplicants.put(term.getBinaryRep(), term.getOriginalTerms());
                        }
                    }
                }

                boolean emptyLists = true;

                for (ArrayList<Term> list : newMinterms.values()) {
                    if (!list.isEmpty()) {
                        emptyLists = false;
                        break;
                    }
                }

                if (!emptyLists) minterms = newMinterms;
            }

            /*
            //Prints all prime implicants
            primeImplicants.forEach((key, list) -> System.out.println(key + " : " + Arrays.toString(list)));
            System.out.println("-----------------------------------------------------");
            */

            HashMap<String, boolean[]> essPrimeTable = new HashMap<>();
            HashSet<Integer> tempEssPrimes = new HashSet<>();
            HashMap<String, int[]> essPrimeImplicants = new HashMap<>();

            //making the essential prime implicant table to find the most simple equation
            for (String key : primeImplicants.keySet()) {
                essPrimeTable.put(key, new boolean[realMinterms.size()]);
                boolean[] row = essPrimeTable.get(key);
                for (int j = 0; j < primeImplicants.get(key).length; j++) {
                    for (int k = 0; k < row.length; k++) {
                        if (primeImplicants.get(key)[j] == realMinterms.get(k).getOriginalTerms()[0]) {
                            essPrimeTable.get(key)[k] = true;
                        }
                    }
                }
            }

            /*
            //Prints the prime implicant table
            System.out.print("BinRep:" + "   ");
            realMinterms.forEach(term -> System.out.print(term.getOriginalTerms()[0] + "    "));
            System.out.println();
            essPrimeTable.forEach((key, list) -> System.out.println(key + " : " + Arrays.toString(list)));

            System.out.println("-----------------------------------------------------");
            */

            //traverse the prime implicant table in column major order to find the essential ones
            //a prime implicant is essential if it is the only term that covers a certain minterm
            for (int c = 0; c < realMinterms.size(); c++) {
                int count = 0;
                String binRep = "";
                for (String key : essPrimeTable.keySet()) {
                    if (essPrimeTable.get(key)[c]) {
                        count++;
                        binRep = key;
                    }
                }

                if (count == 1) essPrimeImplicants.put(binRep, primeImplicants.get(binRep));
            }


            for (int[] arr : essPrimeImplicants.values()) {
                for (int num : arr) {
                    boolean contains = false;

                    for (Term real : realMinterms) {
                        if (num == real.getOriginalTerms()[0]) contains = true;
                    }

                    if (contains) tempEssPrimes.add(num);
                }
            }

            //makes a list of all the needed minterms
            ArrayList<Integer> neededTerms = new ArrayList<>();
            for (Term term : realMinterms) {
                neededTerms.add(term.getOriginalTerms()[0]);
            }

            //checks if all minterms are covered by the essential prime implicants
            //if not, then Petricks method is used to find all equations
            if (tempEssPrimes.size() == neededTerms.size()) {

                finalEquation.add(new ArrayList<>());
                for (String key : essPrimeImplicants.keySet()) {
                    finalEquation.getFirst().add(key);
                }

            } else {

                //finds the missing minterms
                ArrayList<Integer> missing = new ArrayList<>();
                for (int neededNum : neededTerms) {
                    boolean contains = false;
                    for (int tempNum : tempEssPrimes) {
                        if (tempNum == neededNum) {
                            contains = true;
                            break;
                        }
                    }
                    if (!contains) missing.add(neededNum);
                }

                //creates the Petrick table which contains no essential prime implicants
                HashMap<String, boolean[]> newEssPrimeTable = new HashMap<>();
                for (String key : primeImplicants.keySet()) {
                    boolean isEss = false;
                    for (String essKey : essPrimeImplicants.keySet()) {
                        if (key.equals(essKey)) {
                            isEss = true;
                            break;
                        }
                    }
                    if (isEss) continue;
                    newEssPrimeTable.put(key, new boolean[missing.size()]);
                    boolean[] row = newEssPrimeTable.get(key);
                    for (int j = 0; j < primeImplicants.get(key).length; j++) {
                        for (int k = 0; k < row.length; k++) {
                            if (primeImplicants.get(key)[j] == missing.get(k)) {
                                newEssPrimeTable.get(key)[k] = true;
                            }
                        }
                    }
                }

                HashMap<BitSet, String> bitToVar = new HashMap<>();
                HashMap<String, BitSet> varToBit = new HashMap<>();
                //creates a mapping between bit representations and BitSets
                int count = 0;
                for (String key : newEssPrimeTable.keySet()) {
                    BitSet bitSet = new BitSet();
                    bitSet.set(count);
                    bitToVar.put(bitSet, key);
                    varToBit.put(key, bitSet);
                    count++;
                }

                //creates the Petrick equation, which is a POS expression of all terms which cover a minterm
                ArrayList<ArrayList<BitSet>> equation = new ArrayList<>();
                for (int c = 0; c < missing.size(); c++) {
                    ArrayList<BitSet> nestedEqu = new ArrayList<>();
                    for (String key : newEssPrimeTable.keySet()) {
                        if (newEssPrimeTable.get(key)[c]) {
                            nestedEqu.add(varToBit.get(key));
                        }
                    }
                    equation.add(nestedEqu);
                }


                //Petricks method to simplify the equation
                //merges all the terms from the equation into a SOP expression where each list is a possible equation
                ArrayList<BitSet> tempEqu = equation.getFirst();

                for (int j = 1; j < equation.size(); j++) {
                    tempEqu = (bitListMerge(tempEqu, equation.get(j)));

                    HashSet<BitSet> dedup = new HashSet<>();
                    for (BitSet set : tempEqu) {
                        dedup.add(set);
                    }
                    tempEqu.clear();
                    for (BitSet key : dedup) {
                        tempEqu.add(key);
                    }

                    tempEqu = simplification(tempEqu);
                }

                int minCard = Integer.MAX_VALUE;
                ArrayList<BitSet> finalEqu = new ArrayList<>();
                //finds the minimum cardinality of a BitSet
                //uses that to find the possible equation with the least amount of terms
                for (BitSet bitSet : tempEqu) {
                    if (bitSet.cardinality() < minCard) minCard = bitSet.cardinality();
                }

                for (BitSet bitSet : tempEqu) {
                    if (bitSet.cardinality() == minCard) finalEqu.add(bitSet);
                }

                /*
                //Prints Petricks equation and bit to var converter
                System.out.println(bitToVar);
                System.out.println(finalEqu);
                System.out.println("-----------------------------------------------------");
                */

                int number = 0;
                for (BitSet set : finalEqu) {

                    finalEquation.add(new ArrayList<>());

                    for (String key : essPrimeImplicants.keySet()) {
                        finalEquation.get(number).add(key);
                    }

                    for (BitSet key : bitToVar.keySet()) {
                        if (set.intersects(key)) {
                            finalEquation.get(number).add(bitToVar.get(key));
                        }
                    }

                    number++;
                }
            }

            DisplayPanel.updateOutputs("Equation: " + String.join(" + ", letterEq(finalEquation).get(0)));

            finalEquation.clear();
        }
    }

    public static void read() throws FileNotFoundException{

        numTerms = (int) Math.pow(2, numInputs);

        //Read file
        File f = new File("src/WriteTable.csv");
        Scanner s = new Scanner(f);

        terms = new Term[numTerms];

        //iterate through the whole file
        int j = 0;
        while(s.hasNext()) {
            String line = s.nextLine();

            String[] splitLine = line.split(",");

            numOutputs = splitLine.length - numInputs;
            String[] outputLine = new String[numOutputs];

            String binaryRep = "";

            for (int i = 0; i < splitLine.length; i++) {
                if (i < numInputs) {
                    binaryRep += splitLine[i];
                } else {
                    outputLine[i - numInputs] = splitLine[i];
                }
            }
            terms[j] = new Term(binaryRep, outputLine);
            j++;
        }
    }

    public static int[] mergeIntArray(int[] arr1, int[] arr2) {
        int[] newArr = new int[arr1.length + arr2.length];

        for (int i = 0; i < arr1.length; i++) {
            newArr[i] = arr1[i];
        }
        for (int i = 0; i < arr2.length; i++) {
            newArr[i + arr1.length] = arr2[i];
        }

        return newArr;
    }

    public static void setNumInputs(int numberOfInputs) {
        numInputs = numberOfInputs;
    }

    public static String getFinalEquation() {
        return String.join(" + ", letterEq(finalEquation).get(0));
    }

    public static ArrayList<BitSet> bitListMerge(ArrayList<BitSet> list1, ArrayList<BitSet> list2) {
        ArrayList<BitSet> newList = new ArrayList<>();
        HashSet<String> check = new HashSet<>();

        for (BitSet set1 : list1) {
            for (BitSet set2 : list2) {
                BitSet temp = (BitSet) set1.clone();
                temp.or(set2);

                if (!check.contains(temp.toString())) {
                    check.add(temp.toString());
                    newList.add(temp);
                }
            }
        }

        return newList;
    }

    public static ArrayList<BitSet> simplification(ArrayList<BitSet> list) {

        ArrayList<BitSet> newList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            BitSet outer = list.get(i);
            boolean simp = false;

            for (int j = 0; j < list.size(); j++) {
                if (i == j) continue;

                BitSet temp = (BitSet) outer.clone();
                BitSet inner = list.get(j);
                temp.and(inner);

                if (temp.equals(inner) && !outer.equals(inner)) {
                    simp = true;
                    break;
                }
            }

            if (!simp) {
                newList.add(outer);
            }
        }
        return newList;
    }

    /**
     *
     * @param binRep length is greater than zero and less than 26
     * @return binRep converted to letters as a term
     */
    public static String numToLetter(String binRep) {
        String result = "";
        int dashCount = 0;

        for (int i = 0; i < binRep.length(); i++) {
            char curChar = binRep.charAt(i);
            if (curChar != '-') {
                char letter = (char) ('A' + i);
                result += letter;
                if (curChar == '0') {
                    result += "'";
                }
            } else dashCount++;
        }

        if (dashCount == binRep.length()) result += "1";

        return result;
    }


    public static ArrayList<ArrayList<String>> letterEq(ArrayList<ArrayList<String>> numEq) {
        ArrayList<ArrayList<String>> letterFinalEq = new ArrayList<>(numEq.size());

        for(int i = 0; i < numEq.size(); i++) {
            letterFinalEq.add(new ArrayList<>());

            for(int j = 0; j < numEq.get(i).size(); j++) {
                letterFinalEq.get(i).add(numToLetter(numEq.get(i).get(j)));
            }
        }
        return letterFinalEq;
    }

}