package lab.two;

import lab.two.algorithm.ConjugateGradients;
import lab.two.algorithm.Function;

public class Lab2Runner {
    private static final double[] startPoint = new double[]{20, 20};
    public static final double eps = 0.001;

    public static void runAlgorithm(Function algorithm) {
        System.out.println(algorithm.getClass().getName() + '\n');
        double[] answer = algorithm.apply(startPoint, eps);
        System.out.println("Minimum point coordinates:");
        for (int i = 0; i < answer.length - 1; i++) {
            System.out.print(answer[i] + " ");
        }
        System.out.print('\n' + "Number of iteration: " + answer[answer.length - 1]);
    }

    public static void main(String[] args) {
        runAlgorithm(new ConjugateGradients());
    }
}
