package ca.ucalgary.cpsc.csus.todo.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ca.ucalgary.cpsc.csus.todo.R;


public class MainActivity extends ActionBarActivity {

    private ListView lvTasks;
    private EditText etNewTask;
    private Button btnAddTask;

    private ArrayList<String> listTasks; // Maintains the list of tasks.
    private ArrayAdapter<String> adapterTasks; // Maintains views of each task for the list view.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the views from the layout so we can call functions on them.
        lvTasks = (ListView) findViewById(R.id.lv_tasks);
        etNewTask = (EditText) findViewById(R.id.et_new_task);
        btnAddTask = (Button) findViewById(R.id.btn_add_task);

        readTasks(); // Reads the tasks from the tasks file.

        // Initialize adapter for list view with a built-in list item layout.
        adapterTasks = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listTasks);

        setupButton(); // Sets up the button's on click listeners.
        setupListView(); // Sets up the list view, i.e. populating it.
    }

    /**
     * Sets up the add task button's on click listener.
     */
    private void setupButton() {
        // Set the on click listener for the add task button.
        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTask();
            }
        });
    }

    /**
     * Sets up the list view and its on click listeners.
     */
    private void setupListView() {
        // Sets the list view's adapter.
        lvTasks.setAdapter(adapterTasks);

        // Set on item long click listener for tasks list view.
        // Removes the task if it is clicked and held for a short period of time.
        lvTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Removes the task from the list of tasks.
                listTasks.remove(position);

                // Writes tasks to the file.
                writeTasks();

                // Notifies the list view that the tasks have been changed (refreshes it).
                adapterTasks.notifyDataSetChanged();
                return true;
            }
        });

        // Opens a new activity with the task when it is clicked.
        lvTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the task you clicked.
                String taskText = listTasks.get(position);

                // Initialize an intent to open a new Task Activity.
                Intent intent = new Intent(MainActivity.this, TaskActivity.class);

                // Adds the task's text to the intent so the Task Activity can use it.
                intent.putExtra("TASK", taskText);

                // Starts the activity with the intent.
                startActivity(intent);
            }
        });
    }

    /**
     * Adds a new task and refreshes the list view with the new task.
     */
    private void addNewTask() {
        String task = etNewTask.getText().toString();

        // Add the task if the task field is not empty, otherwise display a message.
        if (task.length() > 0) {
            // Add a task to the list of tasks.
            listTasks.add(task);

            writeTasks();

            // Clears the task text field.
            etNewTask.getText().clear();

            // Tells the list view to refresh.
            adapterTasks.notifyDataSetChanged();
        } else {
            // Displays a message "No task" if the task is empty.
            Toast toast = Toast.makeText(MainActivity.this, "No task", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * Reads tasks to the tasks text file.
     */
    private void readTasks() {
        File fileDir = getFilesDir(); // Get the directory to put the text file.
        File tasksFile = new File(fileDir, "tasks.txt"); // Get a pointer to the file.
        try {
            // Initialize the list of tasks based on the lines of the file.
            // Each line is a task.
            listTasks = new ArrayList<>(FileUtils.readLines(tasksFile));
        } catch (IOException e) {
            // If there is an error in reading the file, just initialize it to an empty list.
            listTasks = new ArrayList<>();
        }
    }

    /**
     * Writes tasks to the tasks text file.
     */
    private void writeTasks() {
        File fileDir = getFilesDir(); // Get the directory to put the text file.
        File tasksFile = new File(fileDir, "tasks.txt"); // Get a pointer to the text file.
        try {
            // Writes each task as a line in the text file.
            FileUtils.writeLines(tasksFile, listTasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
