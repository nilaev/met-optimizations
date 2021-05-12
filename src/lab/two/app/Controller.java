package lab.two.app;

import javafx.fxml.FXML;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import lab.two.Lab2Runner;
import lab.two.algorithm.ConjugateGradients;
import lab.two.algorithm.Function;
import lab.two.algorithm.GradientDescent;
import lab.two.algorithm.SteepestDescent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.function.DoubleFunction;
import java.util.stream.IntStream;

public class Controller {
	
	@FXML
	public RadioMenuItem conjMenu;
	@FXML
	Random random = new Random(4);
	@FXML
	private RadioMenuItem gradMenu;
	@FXML
	private RadioMenuItem steepMenu;
	@FXML
	private Canvas canvas;
	@FXML
	private Text chooseAlgoLabel;
	@FXML
	private TextField rangeInput1;
	@FXML
	private TextField rangeInput2;
	private double startX = 20;
	private double startY = 20;
	
	@FXML
	void onAlgorithmUpdate() {
		chooseAlgoLabel.setVisible(false);
		rangeInput1.setVisible(true);
		rangeInput2.setVisible(true);
		clearCanvas();
		Function algo;
		BoundingBox bounds = new BoundingBox(10, 10, canvas.getHeight() - 20, canvas.getHeight() - 20);
		if (gradMenu.isSelected()) {
			algo = new GradientDescent();
		} else if (steepMenu.isSelected()) {
			algo = new SteepestDescent();
		} else if (conjMenu.isSelected()) {
			algo = new ConjugateGradients();
		} else {
			throw new AssertionError();
		}
		algo.apply(new double[]{startX, startY}, Lab2Runner.eps);
		paintAlgorithm(algo, bounds, canvas.getGraphicsContext2D());
	}
	
	private void clearCanvas() {
		GraphicsContext g = canvas.getGraphicsContext2D();
		Paint oldFill = g.getFill();
		g.setFill(Color.WHITE);
		g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		g.setFill(oldFill);
	}
	
	void paintAlgorithm(Function performedAlgo, Bounds bounds, GraphicsContext g) {
		switch (Function.MODE) {
			case 1:
				// Part for function 10*x^2 + y^2
				for (double level = 0; level <= 20; level++) {
					ArrayList<Double> xs = new ArrayList<>();
					ArrayList<Double> ys = new ArrayList<>();
					for (double x = -level; x <= level; x += 0.1) {
						xs.add(x);
						ys.add(Function.inverseFunction(x, level));
					}
					for (double x = level; x >= -level; x -= 0.1) {
						xs.add(x);
						ys.add(Function.minusInverseFunction(x, level));
					}
					paintPolygon(
							xs.stream().mapToDouble(xx -> xx).toArray(),
							ys.stream().mapToDouble(yy -> yy).toArray(),
							-10, 10, -10, 10, bounds, g);
				}
			case 2:
				// Part for function x^2 + 10xy + y^2
				for (double level = 0; level <= 20; level++) {
					ArrayList<Double> xs = new ArrayList<>();
					ArrayList<Double> ys = new ArrayList<>();
					for (double x = -level; x <= level; x += 0.1) {
						xs.add(x);
						ys.add(Function.inverseFunction(x, level));
					}
					for (double x = level; x >= -level; x -= 0.1) {
						xs.add(x);
						ys.add(Function.inverseFunction(x, level));
					}
					paintPolygon(
							xs.stream().mapToDouble(xx -> xx).toArray(),
							ys.stream().mapToDouble(yy -> yy).toArray(),
							-2, 2, -2, 2, bounds, g);

					xs.clear();
					ys.clear();
					for (double x = level; x >= -level; x -= 0.1) {
						xs.add(x);
						ys.add(Function.minusInverseFunction(x, level));
					}
					for (double x = -level; x <= level; x += 0.1) {
						xs.add(x);
						ys.add(Function.minusInverseFunction(x, level));
					}
					paintPolygon(
							xs.stream().mapToDouble(xx -> xx).toArray(),
							ys.stream().mapToDouble(yy -> yy).toArray(),
							-2, 2, -2, 2, bounds, g);
				}
		}
		ArrayList<ArrayList<Double>> coors = performedAlgo.getPoints();
		int coorsSz = coors.size();
		double[] xs = new double[coorsSz];
		double[] ys = new double[coorsSz];
		for (int i = 0; i < coorsSz; i++) {
			xs[i] = coors.get(i).get(0);
			ys[i] = coors.get(i).get(1);
		}
		paintSegments(xs, ys, -20, 20, -20, 20, bounds, g);
	}

	private void paintSegments(double[] xs, double[] ys,
	                           double xMin, double xMax,
	                           double yMin, double yMax,
	                           Bounds bounds, GraphicsContext g) {
		Paint oldStroke = g.getStroke();
		double oldLineWidth = g.getLineWidth();
		g.setLineWidth(1.0);
		g.setStroke(Color.RED);
		Font font = Font.font(10);
		g.setFont(font);
		
		double prevX = -1;
		double prevY = -1;
		for (int i = 0; i < xs.length; i++) {
			double x = xs[i];
			double y = ys[i];
			double mappedX = map(x, xMin, xMax, bounds.getMinX(), bounds.getMaxX());
			double mappedY = map(y, yMax, yMin, bounds.getMinY(), bounds.getMaxY());
			//Text s = new Text(String.format("x%d", i));
			Text s = new Text("");
			s.setFont(font);
			g.fillText(s.getText(), mappedX - 10, mappedY - 5);
			if (i != 0) {
				g.strokeLine(prevX, prevY, mappedX, mappedY);
				g.strokeLine(prevX, prevY, mappedX, mappedY);
				// arrow
				double factor = 3 / Math.hypot(prevX - mappedX, prevY - mappedY);
				double dx = (prevX - mappedX) * factor;
				double dy = (prevY - mappedY) * factor;
				g.strokeLine(mappedX + dx - dy, mappedY + dy + dx, mappedX, mappedY);
				g.strokeLine(mappedX + dx + dy, mappedY + dy - dx, mappedX, mappedY);
			}
			prevX = mappedX;
			prevY = mappedY;
		}
		g.setStroke(oldStroke);
		g.setLineWidth(oldLineWidth);
	}
	
	
	private void paintPolygon(double[] xs, double[] ys,
	                          double xMin, double xMax,
	                          double yMin, double yMax,
	                          Bounds bounds, GraphicsContext g) {
		paintLine(xs, ys, xMin, xMax, yMin, yMax, true, Color.DARKGRAY, bounds, g);
	}
	
	private void paintLine(double[] xs, double[] ys,
	                       double xMin, double xMax,
	                       double yMin, double yMax,
	                       boolean closed,
	                       Color color, Bounds bounds, GraphicsContext g) {
		Paint oldStroke = g.getStroke();
		double oldLineWidth = g.getLineWidth();
		g.setLineWidth(1.0);
		g.setStroke(color);
		
		double prevX = -1;
		double prevY = -1;
		double startX = 0;
		double startY = 0;
		for (int i = 0; i < xs.length; i++) {
			double x = xs[i];
			double y = ys[i];
			double mappedX = map(x, xMin, xMax, bounds.getMinX(), bounds.getMaxX());
			double mappedY = map(y, yMax, yMin, bounds.getMinY(), bounds.getMaxY());
			if (i != 0) {
				g.strokeLine(prevX, prevY, mappedX, mappedY);
			} else {
				startX = mappedX;
				startY = mappedY;
			}
			prevX = mappedX;
			prevY = mappedY;
		}
		if (closed && xs.length >= 3) {
			g.strokeLine(prevX, prevY, startX, startY);
		}
		g.setStroke(oldStroke);
		g.setLineWidth(oldLineWidth);
	}
	
	
	private void paintFunction(DoubleFunction<Double> f,
	                           double x0, double x1,
	                           double y0, double y1,
	                           Color color, Bounds bounds, GraphicsContext g) {
		Paint oldStroke = g.getStroke();
		double oldLineWidth = g.getLineWidth();
		g.setLineWidth(2.0);
		g.setStroke(color);
		
		int steps = 100;
		double prevX = -1;
		double prevY = -1;
		for (int step = 0; step < steps; step++) {
			double x = x0 + step * (x1 - x0) / steps;
			double y = f.apply(x);
			double mappedX = map(x, x0, x1, bounds.getMinX(), bounds.getMaxX());
			double mappedY = map(y, y1, y0, bounds.getMinY(), bounds.getMaxY());
			if (step != 0) {
				g.strokeLine(prevX, prevY, mappedX, mappedY);
			}
			prevX = mappedX;
			prevY = mappedY;
		}
		g.setStroke(oldStroke);
		g.setLineWidth(oldLineWidth);
	}
	
	private Color randomHSB() {
		return Color.hsb(random.nextInt(360), 0.7, 1);
	}
	
	private double map(double val, double inStart, double inEnd, double outStart, double outEnd) {
		return (val - inStart) / (inEnd - inStart) * (outEnd - outStart) + outStart;
	}
	
	public void onRange1Action() {
		String value = rangeInput1.textProperty().getValue();
		try {
			startX = Double.parseDouble(value);
			rangeInput1.setStyle("");
			onAlgorithmUpdate();
		} catch (NumberFormatException e) {
			rangeInput1.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
		}
	}
	
	public void onRange2Action() {
		String value = rangeInput2.textProperty().getValue();
		try {
			startY = Double.parseDouble(value);
			rangeInput2.setStyle("");
			onAlgorithmUpdate();
		} catch (NumberFormatException e) {
			rangeInput2.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
		}
	}
	
	public void initialize() {
		rangeInput1.focusedProperty().addListener((ov, oldV, newV) -> {
			if (!newV) {
				onRange1Action();
			}
		});
		rangeInput2.focusedProperty().addListener((ov, oldV, newV) -> {
			if (!newV) {
				onRange2Action();
			}
		});
	}
	
}
