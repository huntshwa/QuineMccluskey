import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.io.File;

public class Main {

    private static int numInputs;
    private static int numOutputs;
    private static int numTerms;
    private static Term[] terms;

    public static void main(String[] args) throws FileNotFoundException {
        read();

        for (int i = 0; i < numOutputs; i++) {
            ArrayList<Term> unorderedMinterms = new ArrayList<>();
            ArrayList<Term> realMinterms = new ArrayList<>();
            HashMap<String, int[]> primeImplicants = new HashMap<>();
            HashMap<Integer, ArrayList<Term>> minterms = new HashMap<>();
            for (Term term : terms) {
                if (term.getOutput()[0].equals("1")) {
                    unorderedMinterms.add(term);
                    realMinterms.add(term);
                }
                if (term.getOutput()[0].equals("x"))
                    unorderedMinterms.add(term);
            }

            for (int j = 0; j <= numInputs; j++) {
                if (!minterms.containsKey(j)) {
                    minterms.put(j, new ArrayList<>());
                }
                for (Term term : unorderedMinterms) {

                    int count = 0;
                    for (int k = 0; k < term.getBinaryRep().length(); k++) {
                        if (term.getBinaryRep().charAt(k) == '1') count++;
                    }

                    if (count == j) minterms.get(j).add(term);
                }
            }

            boolean iterate = true;
            while (iterate) {
                HashMap<Integer, ArrayList<Term>> newMinterms = new HashMap<>();
                for (int j = 0; j < minterms.size() - 1; j++) {
                    if (!newMinterms.containsKey(j)) {
                        newMinterms.put(j, new ArrayList<>());
                    }

                    for (int k = 0; k < minterms.get(j).size(); k++) {
                        Term curTerm = minterms.get(j).get(k);
                        for (int l = 0; l < minterms.get(j + 1).size(); l++) {
                            Term compTerm = minterms.get(j + 1).get(l);
                            if (Term.onesAligned(curTerm, compTerm) && Term.dashesAligned(curTerm, compTerm)) {
                                int index = Term.misalignedOnesIndex(curTerm, compTerm);
                                String newBinRep = curTerm.getBinaryRep().substring(0, index) + "-" + curTerm.getBinaryRep().substring(index + 1);
                                int[] addOgTerms = mergeIntArray(curTerm.getOriginalTerms(), compTerm.getOriginalTerms());

                                Term addTerm = new Term(newBinRep, curTerm.getOutput(), addOgTerms);

                                newMinterms.get(j).add(addTerm);
                                curTerm.updateUsed(true);
                                compTerm.updateUsed(true);
                            }
                        }
                    }

                }

                iterate = false;
                for (ArrayList<Term> termList : minterms.values()) {
                    for (Term term : termList) {
                        if (!term.getUsed()) {
                            primeImplicants.put(term.getBinaryRep(), term.getOriginalTerms());
                        }
                        if (term.getUsed()) iterate = true;
                    }
                }

                minterms = newMinterms;
            }
            primeImplicants.forEach((key, list) -> System.out.println(key + " : " + Arrays.toString(list)));
            System.out.println("-----------------------------------------------------");

            //next step is here, you need to do ess. prime implicants
//            boolean[][] essPrimeTable = new boolean[realMinterms.size()][primeImplicants.size()];
            HashMap<String, boolean[]> essPrimeTable = new HashMap<>();
            ArrayList<Integer> tempEssPrimes = new ArrayList<>();
            HashMap<String, int[]> essPrimeImplicants = new HashMap<>();

            for (String key : primeImplicants.keySet()) {
                essPrimeTable.put(key, new boolean[realMinterms.size()]);
            }

            for (int j = 0; j < realMinterms.size(); j++) {

            }
//            int row = 0;
//            for (int[] list : primeImplicants.values()) {
//                for (int k = 0; k < list.length; k++) {
//                    for (int l = 0; l < realMinterms.size(); l++) {
//                        if (list[k] == realMinterms.get(l).getOriginalTerms()[0]) {
//                            essPrimeTable[row][l] = true;
//                        }
//                    }
//                }
//                row++;
//            }
//
//            for (int c = 0; c < essPrimeTable[0].length; c++) {
//                int count = 0;
//                int index = 0;
//                for (int r = 0; r < essPrimeTable.length; r++) {
//                    if (essPrimeTable[r][c]) {
//                        count++;
//                        index = r;
//                    }
//                }
//                if (count == 1) tempEssPrimes.add(index);
//            }
//
//            for (int num : tempEssPrimes) {
//                essPrimeImplicants.put(primeImplicantsprimeImplicants.get(num));
//            }

        }
    }

    public static void read() throws FileNotFoundException{
        Scanner s = new Scanner(System.in);

        System.out.println("Enter the amount of inputs: ");

        numInputs = s.nextInt();
        numTerms = (int) Math.pow(2, numInputs);

        File f = new File("src/WikiTruthTest.csv");
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
            newArr[i + arr2.length] = arr2[i];
        }

        return newArr;
    }
}
