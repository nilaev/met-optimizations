package lab.two.algorithm;

import java.util.ArrayList;
import java.util.Arrays;

public class GradientDescent extends Function {
    private static double[] startPoint;
    private static double eps;

    private double[] calculateGradientDescent(double eps, double ... args) {
        boolean stop = false;
        double lambda = 0.01;
        int iter = 0;
        double[] args0 = Arrays.copyOf(args, args.length);
        double[] args1 = new double[args.length];
//        double prevX = args[0];
//        double prevY = args[1];

        while (!stop) {
            double[] grad = gradient(args0);
            for (int i = 0; i < args1.length; i++) {
                args1[i] = args0[i] - grad[i] * lambda;
            }

            double dist = 0;
            for (int i = 0; i < args1.length; i++) {
                dist += Math.pow(args1[i] - args0[i], 2);
            }

            if (dist < eps * eps && Math.abs(func(args0) - func(args1)) < eps) {
                stop = true;
            }

            args0 = Arrays.copyOf(args1, args1.length);
            ArrayList<Double> tmp = new ArrayList<>();
            tmp.add(args0[0]);
            tmp.add(args0[1]);
            curPoints.add(tmp);

//            // for 1 task "Скорость сходимости"
//            double curX = args0[0];
//            double curY = args0[1];
//            System.out.println((iter + 1) + " & " + Math.sqrt(Math.pow(prevX - curX, 2) + Math.pow(prevY - curY, 2)) + "\\" + "\\");
//            prevX = curX;
//            prevY = curY;
//            // 1 task ended
            iter += 1;
        }

        double[] ans = new double[args0.length + 1];
        System.arraycopy(args0, 0, ans, 0, args0.length);
        ans[ans.length - 1] = iter;
        return ans;
    }

    @Override
    protected void init(double[] startPoint, double eps) {
        GradientDescent.startPoint = startPoint;
        GradientDescent.eps = eps;
    }

    @Override
    protected double[] returnAns() {
        eps = 0.01;
        return calculateGradientDescent(eps, startPoint);
    }
}