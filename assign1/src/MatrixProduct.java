import java.util.Scanner;

public class MatrixProduct {
    public static void onMult(int m_ar ,int m_br){
        long Time1, Time2;
        String st;
        long temp;
        int i, j, k;

        long[] pha = new long[m_ar * m_ar];
        long[] phb = new long[m_ar * m_ar];
        long[] phc = new long[m_ar * m_ar];

        for(i=0; i<m_ar; i++){
            for(j=0; j<m_ar; j++){
                pha[i*m_ar+j] = 1;
            }
        }

        for(i=0; i<m_br; i++){
            for(j=0; j<m_br; j++){
                phb[i*m_br+j] = i+1;
            }
        }

        Time1 = System.currentTimeMillis();

        for(i=0; i<m_ar; i++){
            for(j=0; j<m_br; j++){
                temp = 0;
                for(k=0; k<m_ar; k++){
                    temp += pha[i*m_ar+k] * phb[k*m_br+j];
                }
                phc[i*m_ar+j]=temp;
            }
        }

        Time2 = System.currentTimeMillis();
        st = String.format("Time: %,.3f seconds\n", (double) (Time2-Time1)/1000);
        System.out.print(st);

        System.out.println("Result matrix:");
        for(i=0; i<1; i++){
            for(j=0; j<Math.min(10, m_br); j++){
                System.out.print(phc[j] + " ");
            }
        }
        System.out.println();
    }

    public static void onMultLine(int m_ar, int m_br) {
        long Time1, Time2;
        String st;
        double temp;
        int i, j, k;

        double[] pha = new double[m_ar * m_ar];
        double[] phb = new double[m_ar * m_ar];
        double[] phc = new double[m_ar * m_ar];

        for(i=0; i<m_ar; i++){
            for(j=0; j<m_ar; j++){
                pha[i*m_ar+j] = 1.0;
            }
        }

        for(i=0; i<m_br; i++){
            for(j=0; j<m_br; j++){
                phb[i*m_br+j] = i+1;
            }
        }

        Time1 = System.currentTimeMillis();

        for(i=0; i<m_ar; i++){
            for(j=0; j<m_br; j++){
                temp = 0;
                for(k=0; k<m_ar; k++){
                    temp += pha[i*m_ar+k] * phb[k*m_br+j];
                }
                phc[i*m_ar+j]=temp;
            }
        }

        Time2 = System.currentTimeMillis();
        st = String.format("Time: %,.3f seconds\n", (double)(Time2-Time1)/1000);
        System.out.print(st);

        System.out.println("Result matrix:");
        for(i=0; i<1; i++){
            for(j=0; j<Math.min(10, m_br); j++){
                System.out.print(phc[j] + " ");
            }
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int op = 1;
        Scanner reader = new Scanner(System.in);

        while (op != 0) {
            System.out.println("1. Multiplication");
            System.out.println("2. Line Multiplication");
            System.out.println("3. Block Multiplication");
            System.out.print("Selection?: ");
            op = reader.nextInt();
            System.out.print("Dimensions: lins=cols ? ");
            int lin = reader.nextInt();

            switch (op) {
                case 1:
                    MatrixProduct.onMult(lin, lin);
                    break;
                case 2:
                    MatrixProduct.onMultLine(lin, lin);
                    break;
                case 3:
                    System.out.println("Block Size?");
                    int blockSize = reader.nextInt();
                    System.out.println("Not implemented.\n");
            }
        }
        reader.close();
    }
}
