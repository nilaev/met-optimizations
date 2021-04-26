//package lab.two.algorithm;
//
//import java.util.Arrays;
//
//public class ConjugateGradients extends Function {
//    private static double[] startPoint;
//    private static double eps;
//
//    @Override
//    protected void init(double[] startPoint, double eps) {
//        ConjugateGradients.startPoint = startPoint;
//        ConjugateGradients.eps = eps;
//    }
//
//    private double GoldenRatio(double eps, double[] a, double[] b) {
//        double left = 0;
//        double right = 1e5;
//        double x0 = left + 0.5 * (3 - Math.sqrt(5)) * (right - left);
//        double x1 = right - x0 + left;
//
//        while (Math.abs(right - left) > eps) {
//            double[] arL = new double[a.length];
//            double[] arR = new double[a.length];
//            for (int i = 0; i < a.length; i++) {
//                arL[i] = a[i] + x0 * b[i];
//                arR[i] = a[i] + x1 * b[i];
//            }
//
//            if (func(arL) < func(arR)) {
//                right = x1;
//            } else {
//                left = x0;
//            }
//            x1 = x0;
//            x0 = right + left - x1;
//        }
//        return (left + right) / 2;
//    }
//
//    private double innerproduct(double[] a, double[] b) {
//
//        double ans = 0;
//        for (int i = 0; i < a.length; i++) {
//            ans += a[i] * b[i];
//        } return ans;
//    }
//
//    double sqGrad(double ... args) {
//        double sum = 0, d = 0.00000001;
//        for (int i = 0; i < args.length; i++) {
//            double[] argsCopy = Arrays.copyOf(args, args.length);
//            argsCopy[i] += d;
//            sum += Math.pow((func(argsCopy) - func(args)) / d, 2);
//        }
//        return sum;
//    }
//
//    private double[] calculateConjugateGradients(double eps, double ... args) {
//        double[][] s = new double[args.length][args.length];
//        for (int j = 0; j < args.length; j++) {
//            for (int i = 0; i < args.length; i++) {
//                s[i][j] = i == j ? 1 : 0;
//            }
//        }
//        int iter = 0;
//        while (sqGrad(args) > Math.pow(eps, 2)) {
//
//        }
//
//
//
//        double[] p = Gradfunc(args);
//        for (int i = 0; i < p.length; i++) p[i] = -p[i];
//        double[] grad = Arrays.copyOf(p, p.length);
//
//        double prevX = args[0];
//        double prevY = args[1];
//        double curX = 0;
//        double curY = 0;
//        while (!stop) {
//            iter += 1;
//            // for 1 task "Скорость сходимости"
//            curX = args[0];
//            curY = args[1];
//            System.out.println((iter) + " & " + Math.sqrt(Math.pow(prevX - curX, 2) + Math.pow(prevY - curY, 2)) + "\\" + "\\");
//            prevX = curX;
//            prevY = curY;
//            // 1 task ended
//
//            double alpha = GoldenRatio(eps, args, p);
//            for (int i = 0; i < args.length; i++) {
//                args[i] += alpha * p[i];
//            }
//
//            double[] grad1 = Gradfunc(args);
//            for (int i = 0; i < grad1.length; i++) grad1[i] = -grad1[i];
//
//            double beta = iter % 2 == 1 ? innerproduct(grad1, grad1) / innerproduct(grad, grad) : 0;
//            for (int i = 0; i < p.length; i++) {
//                p[i] = grad1[i] + beta * p[i];
//            }
//
//            grad = Arrays.copyOf(grad1, grad1.length);
//            if (innerproduct(grad, grad) <= eps) {
//                stop = true;
//            }
//        }
//
//        double[] ans = new double[args.length + 1];
//        System.arraycopy(args, 0, ans, 0, args.length);
//        ans[ans.length - 1] = iter;
//        return ans;
//    }
//
//    @Override
//    public double[] returnAns() {
//        return calculateConjugateGradients(eps, startPoint);
//    }
//}

package lab.two.algorithm;

public class ConjugateGradients extends Function {
    private static double[] startPoint;
    private static double eps;

    @Override
    protected void init(double[] startPoint, double eps) {
        ConjugateGradients.startPoint = startPoint;
        ConjugateGradients.eps = eps;
    }

//    double func(double[] x) {
//        return (x[0] - 5) * (x[0] - 5) * (x[1] - 4) * (x[1] - 4) +  (x[0] - 5) * (x[0] - 5) + (x[1] - 4) * (x[1] - 4) + 1;
//    }

    double[] Gradfunc(double[] x) {
        double[] res = new double[x.length];
        res[0] = 2 * (x[0] - 5) * ( (x[1] - 4) * (x[1] - 4) + 1);
        res[1] =  2 * (x[1] - 4) * ( (x[0] - 5) * (x[0] - 5) + 1);
        return res;
    }

    private double innerproduct(double[] a, double[] b) {
        double ans = 0;
        for (int i = 0; i < a.length; i++) {
            ans += a[i] * b[i];
        } return ans;
    }

    private double GoldenRatio(double[] s, double[] p) {
        double eps = 1e-8;
        double a = 0;
        double b = 1e5;
        double x0 = a + 0.5 * (3 - Math.sqrt(5.0)) * (b - a);
        double x1 = b - x0 + a;

        while (Math.abs(b - a) > eps) {
            double[] arL = new double[s.length];
            double[] arR = new double[s.length];
            for (int i = 0; i < s.length; i++) {
                arL[i] = s[i] + x0 * p[i];
                arR[i] = s[i] + x1 * p[i];
            }

            if (func(arL) < func(arR)) b = x1;
            else a = x0;

            x1 = x0;
            x0 = b + a - x1;
        }
        return (a + b)/2;
    }

    private double[] calculateConjugateGradients(double eps, int spaceSize) {
        eps = 1e-5;
        double[] x = new double[spaceSize];
        double curVal = func(x);
        double prevVal = curVal;
        double[] p = Gradfunc(x);
        for (int i = 0; i < p.length; i++) p[i] = -p[i];
        double gradSquare = innerproduct(p, p);

        int iter = 0;
        do {
            iter++;
            double alpha, beta, newGradSquare;

            alpha = GoldenRatio(x, p);
            for (int i = 0; i < x.length; i++) x[i] += alpha * p[i];

            double[] newGrad = Gradfunc(x);
            for (int i = 0; i < newGrad.length; i++) newGrad[i] = -newGrad[i];
            newGradSquare = innerproduct(newGrad, newGrad);

            if (iter % (5 * spaceSize) == 0) beta = 0;
            else beta = newGradSquare / gradSquare;
            for (int i = 0; i < p.length; i++) p[i] = newGrad[i] + beta * p[i];
            prevVal = curVal;
            curVal = func(x);
            gradSquare = newGradSquare;
        } while (gradSquare > 1e-3);

        double[] ans = new double[x.length + 1];
        System.arraycopy(x, 0, ans, 0, x.length);
        ans[ans.length - 1] = iter;
        return ans;

    }

    @Override
    protected double[] returnAns() {
        return calculateConjugateGradients(eps, 2);
    }
}

