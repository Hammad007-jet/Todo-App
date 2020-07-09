package com.hammad.todoapp;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final List<String> list = new ArrayList<>();
    int[] backgroundColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button newTaskButton = findViewById(R.id.newTaskButton);
        final Button deleteAllTaskButton = findViewById(R.id.deleteAllTaskButton);
        final ListView listView = findViewById(R.id.listView1);
        final TextAdapter adapter = new TextAdapter();

//        Here I define no of item and put background color in it.

        int maxItems = 100;
         backgroundColors = new int[maxItems];
         for (int i = 0; i<maxItems; i++){
            backgroundColors[i]= Color.DKGRAY;
        }

        readInfo();

//        Here I recommend color for important and medium tasks from list items.

        for (int i =0; i<list.size();i++){
            if (list.get(i).startsWith("important")){
                backgroundColors[i] = Color.RED;

            }else if (list.get(i).startsWith("medium")){
                backgroundColors[i] = Color.parseColor("#ff9933");
            }
        }
        adapter.setData(list,backgroundColors);
        listView.setAdapter(adapter);

//  This listener is used to put funcionality of deleting task from the list manually

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {

                AlertDialog dialog = new Builder(MainActivity.this)
                       .setTitle("Delete this Task?")
                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               list.remove(position);
                               adapter.setData(list,backgroundColors);
                               saveInfo();
                           }
                       })
                       .setNegativeButton("No",null)
                       .create();
               dialog.show();
            }
        });


// This listener on button is to create new tasks in list
        newTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Here we added edit text feature in alert dialogue
                final EditText taskInput = new EditText(MainActivity.this);
                taskInput.setSingleLine();
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Add New Task")
                        .setMessage("Write your new Task")
                        .setView(taskInput)
                        .setPositiveButton("Add Task", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String task = taskInput.getText().toString();
//                                code for important task should be seen as 1st item in the list
                                if (task.startsWith("important")){
                                    int taskCount = list.size();
                                    list.add("");
                                    while (taskCount>0){
                                        list.set(taskCount,list.get(taskCount-1));
                                        backgroundColors[taskCount] = backgroundColors[taskCount-1];
                                        taskCount --;
                                    }
                                    list.set(0,task);
                                    backgroundColors[0] = Color.RED;
//                                code for medium task should be seen as 1st item in the list
                                }else if (task.startsWith("medium")){
                                    int taskCount = list.size();
                                    list.add("");
                                    int importantTasksCount = 0;
                                    while (importantTasksCount < taskCount &&
                                            list.get(importantTasksCount).startsWith("important")){
                                        importantTasksCount++;
                                    }

                                    while (taskCount> importantTasksCount){
                                        list.set(taskCount,list.get(taskCount-1));
                                        backgroundColors[taskCount] = backgroundColors[taskCount-1];
                                        taskCount --;
                                    }
                                    list.set(importantTasksCount,task);
                                    backgroundColors[importantTasksCount] = Color.parseColor("#ff9933");
                                }
                                else {
                                    list.add(task);
                                }
                                adapter.setData(list,backgroundColors);
                                saveInfo();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        });

//  This button featured to delete all task in one click with alert dialogue built in.
        deleteAllTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete All")
                        .setMessage("Do you want to delete all data?")
                        .setPositiveButton("Delete All", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                list.clear();
                                adapter.setData(list,backgroundColors);
                                saveInfo();
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .create();
                dialog.show();
            }
        });
    }


// This  method is used to save data or info into storage of mobile

    private void saveInfo() {
        try {
            File file = new File(this.getFilesDir(),"saved");

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

            for (int i = 0;i<list.size();i++){
                bw.write(list.get(i));
                bw.newLine();
            }

            bw.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//   This method is used to read info or data even if app is closed or in pause state. This helps us to read info from app.
    private void readInfo(){
        File file = new File(this.getFilesDir(),"saved");
        if(!file.exists()){
            return;
        }
        try {
           FileInputStream fileInputStream = new FileInputStream(file);
           BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
           String line = reader.readLine();

                while (line!=null){
                    list.add(line);
                    line = reader.readLine();
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
// This is adapter used to attach List view with info stored in list items inform of strings.
    class TextAdapter extends BaseAdapter{

        List<String> list = new ArrayList<>();

        int[] backgroundColor;


        void setData(List<String> mList,int[] mBackgroundColors) {
            list.clear();
            list.addAll(mList);

            backgroundColor = new  int[list.size()];
            for (int i=0; i< list.size(); i++){
                backgroundColor[i] = mBackgroundColors[i];
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
//        we used layout inflater to show items of list from text view in convert view.
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_to_listview, parent, false);
        }
        final TextView textView = convertView.findViewById(R.id.task);
            textView.setBackgroundColor(backgroundColors[position]);
            textView.setText(list.get(position));
        return  convertView;
        }
    }
}
