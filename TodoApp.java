import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class TodoApp extends JFrame {
    private DefaultListModel<Task> taskListModel = new DefaultListModel<>();
    private JList<Task> taskList = new JList<>(taskListModel);
    private JTextField taskField = new JTextField(20);

    public TodoApp() {
        super("To-Do List App");

        loadTasksFromFile();

        JButton addButton = new JButton("Add Task");
        JButton deleteButton = new JButton("Delete");
        JButton completeButton = new JButton("Toggle Complete");

        addButton.addActionListener(e -> addTask());
        deleteButton.addActionListener(e -> deleteTask());
        completeButton.addActionListener(e -> toggleTaskStatus());

        JPanel panel = new JPanel();
        panel.add(taskField);
        panel.add(addButton);
        panel.add(deleteButton);
        panel.add(completeButton);

        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(taskList);

        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                saveTasksToFile();
                System.exit(0);
            }
        });

        setSize(400, 400);
        setVisible(true);
    }

    private void addTask() {
        String taskDesc = taskField.getText().trim();
        if (!taskDesc.isEmpty()) {
            taskListModel.addElement(new Task(taskDesc));
            taskField.setText("");
        }
    }

    private void deleteTask() {
        int index = taskList.getSelectedIndex();
        if (index != -1) {
            taskListModel.remove(index);
        }
    }

    private void toggleTaskStatus() {
        int index = taskList.getSelectedIndex();
        if (index != -1) {
            Task task = taskListModel.get(index);
            task.toggleStatus();
            taskList.repaint();
        }
    }

    private void saveTasksToFile() {
        try (PrintWriter writer = new PrintWriter("tasks.txt")) {
            for (int i = 0; i < taskListModel.size(); i++) {
                Task t = taskListModel.get(i);
                writer.println(t.isCompleted() + "::" + t.getDescription());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTasksFromFile() {
        File file = new File("tasks.txt");
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("::", 2);
                Task t = new Task(parts[1]);
                if (parts[0].equals("true")) t.toggleStatus();
                taskListModel.addElement(t);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new TodoApp();
    }
}
