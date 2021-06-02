package lab.two.algorithm;

import java.util.ArrayList;

public abstract class Function {
    public static final int MODE = 1;
    // MODE = 1 for function 10*x^2 + y^2
    // MODE = 2 for function x^2 + 10xy + y^2
    // MODE = 3 for function 64x^2+ 126xy+ 64y^2 − 10x + 30y+ 13

    protected abstract void init(double[] startPoint, double eps);
    protected abstract double[] returnAns();
    protected ArrayList<ArrayList<Double>> curPoints = new ArrayList<>();

    protected final double func(final double ... args) {
        switch (MODE) {
            case 1:
                return 10 * Math.pow(args[0], 2) + Math.pow(args[1], 2);
            case 2:
                return Math.pow(args[0], 2) + 10 * args[0] * args[1] + Math.pow(args[1], 2);
            case 3:
                return 64 * Math.pow(args[0], 2) + 126 * args[0] * args[1] + 64 * Math.pow(args[1], 2) - 10 * args[0] + 30 * args[1] + 13;
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
            case 3:
                return (Math.sqrt(64 * level - 127 * x * x + 2530 * x - 607) - 63 * x - 15) / 64;
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
            case 3:
                return (-Math.sqrt(64 * level - 127 * x * x + 2530 * x - 607) - 63 * x - 15) / 64;
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
