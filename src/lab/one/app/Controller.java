package lab.one.app;

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
import lab.one.LabRunner;
import lab.one.algorithm.*;
import lab.one.util.Segment;
import lab.one.util.Task;

import java.util.List;
import java.util.Random;
import java.util.function.DoubleFunction;

public class Controller {

	@FXML
	public RadioMenuItem fibMenu;
	@FXML
	public RadioMenuItem brentMenu;
	Random random = new Random(4);
	@FXML
	private RadioMenuItem dichMenu;
	@FXML
	private RadioMenuItem goldRatio;
	@FXML
	private RadioMenuItem parabolicMenu;

	@FXML
	private Canvas canvas;

	@FXML
	private Text chooseAlgoLabel;

	@FXML
	private TextField rangeInput1;

	@FXML
	private TextField rangeInput2;

	private double from = -0.5;
	private double to = 0.5;

	@FXML
	void onAlgorithmUpdate() {
		chooseAlgoLabel.setVisible(false);
		rangeInput1.setVisible(true);
		rangeInput2.setVisible(true);
		clearCanvas();
		Algorithm algo;
		BoundingBox bounds = new BoundingBox(30, 0, canvas.getWidth() - 60, canvas.getHeight() - 10);
		if (dichMenu.isSelected()) {
			algo = new Dichotomy(Task.FUNC, LabRunner.EPS);
		} else if (goldRatio.isSelected()) {
			algo = new GoldenRatio(Task.FUNC, LabRunner.EPS);
		} else if (fibMenu.isSelected()) {
			algo = new Fibonacci(Task.FUNC, 8);
		} else if (parabolicMenu.isSelected()) {
			algo = new Parabolic(Task.FUNC, LabRunner.EPS);
		} else if (brentMenu.isSelected()) {
			algo = new Brent(Task.FUNC, LabRunner.EPS);
		} else {
			throw new AssertionError();
		}
		algo.apply(new Segment(from, to));
		paintAlgorithmSegments(algo, parabolicMenu.isSelected(), bounds, canvas.getGraphicsContext2D());
	}

	private void clearCanvas() {
		GraphicsContext g = canvas.getGraphicsContext2D();
		Paint oldFill = g.getFill();
		g.setFill(Color.LIGHTGRAY);
		g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		g.setFill(oldFill);
	}

	void paintAlgorithmSegments(Algorithm performedAlgo, boolean parabolic, Bounds bounds, GraphicsContext g) {
		List<Segment> segments = performedAlgo.getSegments();
		if (segments.isEmpty()) {
			return;
		}
		double margin = bounds.getHeight() / segments.size();
		Segment segment1 = segments.get(0);
		for (int i = 0; i < segments.size(); i++) {
			Segment segment = segments.get(i);
			double start = map(segment.from(), segment1.from(), segment1.to(),
					bounds.getMinX(), parabolic ? bounds.getMaxX() / 2 : bounds.getMaxX());
			double end = map(segment.to(), segment1.from(), segment1.to(),
					bounds.getMinX(), parabolic ? bounds.getMaxX() / 2 : bounds.getMaxX());
			double y = margin / 2 + margin * i;
			Bounds b = new BoundingBox(start, y, end - start, 10);
			Color color = randomHSB();
			paintSegment(segment.from(), segment.to(), color, b, g);
			if (parabolic) {
				double x = segment.getComputedXs().get(0);
				double[] p = calculateParabola(segment.from(), x, segment.to(), LabRunner.FUNC) ;
				Bounds pBounds = new BoundingBox(bounds.getMaxX() / 2 + 30, bounds.getMinY(), bounds.getWidth() / 2, bounds.getHeight());
				paintParabola(p,
						segment.from(), segment.to(), applyParabola(x, p), Math.max(applyParabola(segment.from(), p), applyParabola(segment.to(), p)),
						color, pBounds, g);
			}
			segment.getComputedXs().forEach(x -> {
				double xMapped = map(x, segment1.from(), segment1.to(),
						bounds.getMinX(), parabolic ? bounds.getMaxX() / 2 : bounds.getMaxX());
				paintPoint(xMapped, y + b.getHeight() / 2, g);
			});
		}
	}

	private void paintParabola(double[] p,
	                           double x0, double x1,
	                           double y0, double y1,
	                           Color color, Bounds bounds, GraphicsContext g) {
		if (Double.isNaN(p[0]) || Double.isNaN(p[1]) || Double.isNaN(p[2])) {
			return;
		}
		paintFunction(x -> applyParabola(x, p), x0, x1, y0, y1, color, bounds, g);
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

	private double[] calculateParabola(double x1, double x2, double x3, DoubleFunction<Double> func) {
		double y1 = func.apply(x1);
		double y2 = func.apply(x2);
		double y3 = func.apply(x3);

		double a = (y3 - (x3 * (y2 - y1) + x2 * y1 - x1 * y2) / (x2 - x1)) / (x3 * (x3 - x1 - x2) + x1 * x2);
		double b = (y2 - y1) / (x2 - x1) - a * (x1 + x2);
		double c = (x2 * y1 - x1 * y2) / (x2 - x1) + a * x1 * x2;
		return new double[]{a, b, c};
	}

	private double applyParabola(double x, double[] p) {
		return p[0] * x * x + p[1] * x + p[2];
	}


	private void paintPoint(double x, double y, GraphicsContext g) {
		double radius = 3;
		g.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
	}

	private Color randomHSB() {
		return Color.hsb(random.nextInt(360), 0.7, 1);
	}


	void paintSegment(double start, double end, Color color, Bounds bounds, GraphicsContext g) {
		Paint oldStroke = g.getStroke();
		double oldLineWidth = g.getLineWidth();

		g.setStroke(color);
		g.setLineWidth(3.0);
		paintSegmentLine(bounds, g);
		paintSegmentEdges(start, end, bounds, g);

		g.setLineWidth(oldLineWidth);
		g.setStroke(oldStroke);
	}

	private void paintSegmentEdges(double start, double end, Bounds bounds, GraphicsContext g) {
		Font font = Font.font(12);
		g.setFont(font);

		Text s = new Text(String.format("%.3f", start));
		s.setFont(font);
		Bounds t1 = s.getLayoutBounds();
		g.strokeLine(bounds.getMinX(), bounds.getMinY(), bounds.getMinX(), bounds.getMaxY());
		g.fillText(s.getText(), bounds.getMinX() - t1.getWidth() - 3, bounds.getMaxY());

		Text e = new Text(String.format("%.3f", end));
		e.setFont(font);
//		Bounds t2 = e.getLayoutBounds();
		g.strokeLine(bounds.getMaxX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY());
		g.fillText(e.getText(), bounds.getMaxX() + 3, bounds.getMaxY());
	}

	private void paintSegmentLine(Bounds bounds, GraphicsContext g) {
		g.strokeLine(bounds.getMinX(), bounds.getMinY() + bounds.getHeight() / 2,
				bounds.getMaxX(), bounds.getMinY() + bounds.getHeight() / 2);
	}

	private double map(double val, double inStart, double inEnd, double outStart, double outEnd) {
		return (val - inStart) / (inEnd - inStart) * (outEnd - outStart) + outStart;
	}

	public void onRange1Action() {
		String value = rangeInput1.textProperty().getValue();
		try {
			double newFrom = Double.parseDouble(value);
			if (newFrom < to) {
				rangeInput1.setStyle("");
				if (Math.abs(from - newFrom) > 1e-5) {
					from = newFrom;
					onAlgorithmUpdate();
				}
			} else {
				rangeInput1.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
			}
		} catch (NumberFormatException e) {
			rangeInput1.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
		}
	}

	public void onRange2Action() {
		String value = rangeInput2.textProperty().getValue();
		try {
			double newTo = Double.parseDouble(value);
			if (newTo > from) {
				rangeInput2.setStyle("");
				if (Math.abs(to - newTo) > 1e-5) {
					to = newTo;
					onAlgorithmUpdate();
				}
			} else {
				rangeInput2.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
			}
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
