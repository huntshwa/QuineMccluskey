import java.io.FileNotFoundException;
import java.util.*;
import java.io.File;

public class Main {

    private static int numInputs;
    private static int numOutputs;
    private static int numTerms;
    private static Term[] terms;
    private static ArrayList<ArrayList<String>> finalEquation = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException {
        read();

        System.out.println("********************");
        //go through all outputs
        for (int i = 0; i < numOutputs; i++) {
            System.out.println("Output " + (i + 1) + ": ");
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

            //ordering minterms into a hashmap
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


            //loop the merge process
            boolean iterate = true;
            while (iterate) {
                boolean merged = false;
                HashMap<Integer, ArrayList<Term>> newMinterms = new HashMap<>();

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

                minterms = newMinterms;

                for (ArrayList<Term> list : minterms.values()) {
                    for (Term term : list) {
                        term.updateUsed(false);
                    }
                }
            }

            /*
            //Prints all prime implicants
            primeImplicants.forEach((key, list) -> System.out.println(key + " : " + Arrays.toString(list)));
            System.out.println("-----------------------------------------------------");
             */


            HashMap<String, boolean[]> essPrimeTable = new HashMap<>();
            HashSet<Integer> tempEssPrimes = new HashSet<>();
            HashMap<String, int[]> essPrimeImplicants = new HashMap<>();

            //making the essential prime implicant table
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
            Prints the prime implicant table
            System.out.print("BinRep:" + "   ");
            realMinterms.forEach(term -> System.out.print(term.getOriginalTerms()[0] + "    "));
            System.out.println();
            essPrimeTable.forEach((key, list) -> System.out.println(key + " : " + Arrays.toString(list)));

            System.out.println("-----------------------------------------------------");
            */
            //column major order to find essential minterms
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

            ArrayList<Integer> neededTerms = new ArrayList<>();
            for (Term term : realMinterms) {
                neededTerms.add(term.getOriginalTerms()[0]);
            }

            if (tempEssPrimes.size() == neededTerms.size()) {

                finalEquation.add(new ArrayList<>());
                for (String key : essPrimeImplicants.keySet()) {
                    finalEquation.getFirst().add(key);
                }

                /*
                String equation = String.join(" + ", essPrimeImplicants.keySet());
                System.out.println("Equation: " + equation);
                 */

            } else {

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

                int count = 0;
                for (String key : newEssPrimeTable.keySet()) {
                    BitSet bitSet = new BitSet();
                    bitSet.set(count);
                    bitToVar.put(bitSet, key);
                    varToBit.put(key, bitSet);
                    count++;
                }


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


                //Petricks method
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

                for (int j = 0; j < tempEqu.size(); j++) {
                    if (tempEqu.get(j).cardinality() < minCard) minCard = tempEqu.get(j).cardinality();
                }

                for (int j = 0; j < tempEqu.size(); j++) {
                    if (tempEqu.get(j).cardinality() == minCard) finalEqu.add(tempEqu.get(j));
                }

                /*
                Prints Petricks equation and bit to var converter
                System.out.println(bitToVar);
                System.out.println(finalEqu);
                System.out.println("-----------------------------------------------------");
                */
                //int number = 1;
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


                    /*
                    essPrimeImplicants.forEach((key, list) -> System.out.print(key + " + "));
                    int counter = 1;
                    for (BitSet key : bitToVar.keySet()) {
                        if (set.intersects(key)) {
                            if (counter == set.cardinality()) System.out.print(bitToVar.get(key));
                            else System.out.print(bitToVar.get(key) + " + ");
                            counter++;
                        }
                    }
                     */

                    number++;
                }
            }

            for (int j = 0; j < letterEq(finalEquation).size(); j++) {
                System.out.println("Equation " + (j + 1) + ": " + String.join(" + ", letterEq(finalEquation).get(j)));
            }
            System.out.println("********************");

            finalEquation.clear();
        }
    }

    public static void read() throws FileNotFoundException{
        Scanner s = new Scanner(System.in);

        System.out.println("Enter the amount of inputs: ");

        numInputs = s.nextInt();
        numTerms = (int) Math.pow(2, numInputs);

        //Test files and my truth table file
//        File f = new File("src/WikiTruthTest.csv");
        File f = new File("src/TruthTable.csv");
//        File f = new File("src/TruthTableTest.csv");
//        File f = new File("src/Petrick.csv");
        s = new Scanner(f);

        terms = new Term[numTerms];

        //get to numbers
        s.nextLine();
        s.nextLine();

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

        for (int i = 0; i < binRep.length(); i++) {
            char curChar = binRep.charAt(i);
            if (curChar != '-') {
                char letter = (char) ('A' + i);
                result += letter;
                if (curChar == '0') {
                    result += "'";
                }
            }
        }

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