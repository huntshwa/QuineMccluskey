import java.util.Arrays;

public class Term {
    private String binaryRep;
    private String[] output;
    private int[] originalTerms;
    private boolean used;

    public Term(String binaryRep, String[] output) {
        this.binaryRep = binaryRep;
        this.output = output;
        originalTerms = new int[]{Integer.parseInt(this.binaryRep, 2)};
        used = false;
    }

    public Term(String binaryRep, String[] output, int[] originalTerms) {
        this.binaryRep = binaryRep;
        this.output = output;
        this.originalTerms = originalTerms;
        used = false;
    }

    public String getBinaryRep() {
        return binaryRep;
    }

    public String[] getOutput() {
        return output;
    }
    public int[] getOriginalTerms() {
        return originalTerms;
    }

    public boolean getUsed() {
        return used;
    }

    public void updateUsed(boolean value) {
        used = value;
    }

    public static boolean onesAligned(Term term1, Term term2) {
        int count = 0;

        for (int i = 0; i < term1.getBinaryRep().length(); i++) {

            char term2Digit = term2.getBinaryRep().charAt(i);
            char term1Digit = term1.getBinaryRep().charAt(i);

            if ((term2Digit == '1' && term1Digit == '0') || (term2Digit == '0' && term1Digit == '1')) {
                count++;
            }
        }

        return !(count > 1);
    }

    public static boolean dashesAligned(Term term1, Term term2) {

        for (int i = 0; i < term1.getBinaryRep().length(); i++) {

            char term2Digit = term2.getBinaryRep().charAt(i);
            char term1Digit = term1.getBinaryRep().charAt(i);

            if ((term2Digit == '-' && term1Digit != '-') || (term2Digit != '-' && term1Digit == '-')) {
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @param term1 Term object with same binaryRep length as term2
     * @param term2 Term object with same binaryRep length as term1
     * @return the index of two Term objects which have misaligned ones in binaryRep, returns -1 if not found
     */
    public static int misalignedOnesIndex(Term term1, Term term2) {
        for (int i = 0; i < term1.getBinaryRep().length(); i++) {

            char term2Digit = term2.getBinaryRep().charAt(i);
            char term1Digit = term1.getBinaryRep().charAt(i);

            if ((term2Digit == '1' && term1Digit == '0') || (term2Digit == '0' && term1Digit == '1')) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public String toString() {
       return binaryRep + Arrays.toString(originalTerms);
    }
}
