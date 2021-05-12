package lab.two.algorithm;

import java.util.ArrayList;

public abstract class Function {
    public static final int MODE = 2;
    // MODE = 1 for function 10*x^2 + y^2
    // MODE = 2 for function x^2 + 10xy + y^2

    protected abstract void init(double[] startPoint, double eps);
    protected abstract double[] returnAns();
    protected ArrayList<ArrayList<Double>> curPoints = new ArrayList<>();

    protected final double func(final double ... args) {
        switch (MODE) {
            case 1:
                return  10 * Math.pow(args[0], 2) + Math.pow(args[1], 2);
            case 2:
                return  Math.pow(args[0], 2) + 10 * args[0] * args[1] + Math.pow(args[1], 2);
            default:
                return 0;
        }
    }

    public static double inverseFunction(final double x, final double level) {
        switch (MODE) {
            case 1:
                return Math.sqrt(level * level - x * x) * 10;
            case 2:
                return (-10*x + Math.sqrt(96 * x * x + level * level)) / 2;
            default:
                return 0;
        }
    }

    public static double minusInverseFunction(final double x, final double level) {
        switch (MODE) {
            case 1:
                return -(Math.sqrt(level * level - x * x) * 10);
            case 2:
                return (-10*x - Math.sqrt(96 * x * x + level * level)) / 2;
            default:
                return 0;
        }
    }

    protected final double[] gradient(final double ... args) {
        final double d = 0.000000001;
        double[] ans = new double[args.length];
        for (int i = 0; i < ans.length; i++) {
            args[i] += d;
            ans[i] = func(args);
            args[i] -= d;
            ans[i] = (ans[i] - func(args)) / d;
        }
        return ans;
    }

    public ArrayList<ArrayList<Double>> getPoints() {
        return curPoints;
    }

    public double[] apply(double[] startPoint, double eps) {
        init(startPoint, eps);
        return returnAns();
    }
}
