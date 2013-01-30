package ${packageName}.domain.roshambo;

public enum Gesture {
	Rock, Paper, Scissors;

	public boolean strongerThan(Gesture otherGesture) {
		switch (this) {
		case Rock:
			return otherGesture == Scissors;
		case Paper:
			return otherGesture == Rock;
		case Scissors:
			return otherGesture == Paper;
		default:
			throw new IllegalArgumentException(
					"no compatible gesture checking " + this + " > "
							+ otherGesture);
		}
	}
}