public final class ConnectFour
{
	public static void main(String[] args)
	{
		// Creates a model representing the state of the game.
		Model model = new Model();

		// This text-based view is used to communicate with the user.
		// It can print the state of the board and handles user input.
		TextView view = new TextView();
		
		// The controller facilitates communication between model and view.
		// It also contains the main loop that controls the sequence of events.
		Controller controller = new Controller(model, view);
		
		// Start a new session.
		controller.startSession();
	}
}
