package lab.two.algorithm;

import java.util.ArrayList;
import java.util.Arrays;

public class ConjugateGradients extends Function {
    private static double[] startPoint;
    private static double eps;
    private static final double[][] A = new double[][]{{20, 0}, {0, 2}};
    private static final double[] B = new double[]{0, 0};

    @Override
    protected void init(double[] startPoint, double eps) {
        ConjugateGradients.startPoint = startPoint;
        ConjugateGradients.eps = eps;
    }

    private double innerproduct(double[] a, double[] b) {
        double ans = 0;
        for (int i = 0; i < a.length; i++) {
            ans += a[i] * b[i];
        } return ans;
    }

    private double getMod(double[] x) {
        double ans = 0;
        for (double v : x) {
            ans += v * v;
        }
        return Math.sqrt(ans);
    }

    private double[] multiply(double[][] a, double[] b) {
        double[] ans = new double[b.length];
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b.length; j++) {
                ans[i] += a[j][i] * b[j];
            }
        } return ans;
    }

    private double[] multiply(double[] a, double b) {
        int l = a.length;
        double[] c = new double[l];
        for (int i = 0; i < l; ++i) {
            c[i] = a[i] * b;
        }
        return c;
    }

    private double[] add(double[] a, double[] b) {
        int l = a.length;
        double[] c = new double[l];
        for (int i = 0; i < l; ++i) {
            c[i] = a[i] + b[i];
        }
        return c;
    }

    private double[] findGradient(final double ... args) {
        return add(multiply(A, args), B);
    }

    private double[] calculateConjugateGradients(double eps, double ... args) {
        double[] curX = args;
        double[] curGradient = findGradient(curX);
        double[] curP = multiply(curGradient, -1D);
        int iter = 0;
        do {
            iter++;
            ArrayList<Double> tmp = new ArrayList<>();
            tmp.add(curX[0]);
            tmp.add(curX[1]);
            curPoints.add(tmp);
//            double[] cp = curX;
            for (int i = 0; i < A.length && getMod(curGradient) > eps; i++) {
                double[] curApK = multiply(A, curP);
                double alfa = Math.pow(getMod(findGradient(curX)), 2) / innerproduct(curApK, curP);
                double[] newX = new double[curX.length];
                for (int j = 0; j < newX.length; j++) {
                    newX[j] = curX[j] + alfa * curP[j];
                }

                double[] newGradient = new double[curGradient.length];
                for (int j = 0; j < newGradient.length; j++) {
                    newGradient[j] = curGradient[j] + alfa * curApK[j];
                }
                double beta = (i == 0) ? 0 : Math.pow(getMod(findGradient(newX)), 2) /
                        Math.pow(getMod(findGradient(curX)), 2);
                double[] newP = add(multiply(newGradient, -1D), multiply(curP, beta));

                curX = newX;
                curP = newP;
                curGradient = newGradient;
            }

//            double currX = curX[0];
//            double curY = curX[1];
//            System.out.println((iter) + " & " + Math.sqrt(Math.pow(cp[0] - currX, 2) + Math.pow(cp[1] - curY, 2)) + "\\" + "\\");
        } while (getMod(curGradient) > eps);
        ArrayList<Double> tmp = new ArrayList<>();
        tmp.add(curX[0]);
        tmp.add(curX[1]);
        curPoints.add(tmp);

        double[] ans = new double[curX.length + 1];
        System.arraycopy(curX, 0, ans, 0, curX.length);
        ans[ans.length - 1] = iter;
        return ans;

    }

    @Override
    protected double[] returnAns() {
        eps = 0.001;
        return calculateConjugateGradients(eps, startPoint);
    }
}

