package lab.two;

import lab.two.algorithm.ConjugateGradients;
import lab.two.algorithm.Function;
import lab.two.algorithm.GradientDescent;
import lab.two.algorithm.SteepestDescent;

public class Lab2Runner {
    private static final double[] startPoint = new double[]{20, 20};
    public static final double eps = 0.00001;

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
/*
lab.two.algorithm.SteepestDescent

Minimum point coordinates:
-0.002722046103533122 -0.002686573025327429
Number of iteration: 37.0
 */