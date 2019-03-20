package in.example.jsonparsing.Activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.example.jsonparsing.Adapters.MainRecyclerViewAdapter;
import in.example.jsonparsing.DatabaseHelper.DatabaseHelper;
import in.example.jsonparsing.Models.FeedbackModel;
import in.example.jsonparsing.R;

public class MainActivity extends AppCompatActivity implements MainRecyclerViewAdapter.ItemClickListener, MainRecyclerViewAdapter.ItemLongClickListner {

    String givenJson = "{\"feedback_forms\" : {\"questions\" : [ {\"options\" : [ {\"title\" : \"Poor\"}, {\"title\" : \"Good\"} ],\"title\" : \"AMBIENCE\"}, {\"options\" : [ {\"title\" : \"Poor\"}, {\"title\" : \"Good\"} ],\"title\" : \"SERVICES\"}, {\"options\" : [ {\"title\" : \"Poor\"}, {\"title\" : \"Good\"} ],\"title\" : \"FOOD\"}, {\"options\" : [ {\"title\" : \"no\"}, {\"title\" : \"yes\"} ],\"title\" : \"DidyoufindusValueformoney?\"} ]}}";
    String[] options = {"Question", "Option one", "Option Two" };
    DatabaseHelper helper;
    List<FeedbackModel> list;
    RecyclerView recyclerView;
    MainRecyclerViewAdapter adapter;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    boolean toInsert = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();

        try {
            toInsert = pref.getBoolean("inserted", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!toInsert) {
            ProcessJson(givenJson);
        } else if (toInsert) {
            getQuestion();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_retrive:
                ProcessJson(givenJson);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void ProcessJson(String s) {
        try {
            JSONObject rootJson = new JSONObject(s);
            JSONObject object = rootJson.getJSONObject("feedback_forms");
            JSONArray array = object.getJSONArray("questions");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                FeedbackModel model = new FeedbackModel();
                model.setId(i);
                model.setTitle(jsonObject.getString("title"));
                JSONArray ar = jsonObject.getJSONArray("options");
                for (int x = 0; x < ar.length(); x++) {
                    JSONObject object1 = ar.getJSONObject(x);
                    if (x == 0) {
                        model.setOptionOne(object1.getString("title"));
                    } else if (x == 1) {
                        model.setOptionTwo(object1.getString("title"));
                    }
                }
                boolean addQuestio = helper.addQuestion(model.getId(), model.getTitle(), model.getOptionOne(), model.getOptionTwo());
                if (addQuestio) {
                    Toast.makeText(this, "Data inserted.", Toast.LENGTH_SHORT).show();
                }
            }
            editor.putBoolean("inserted", true).apply();
            getQuestion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getQuestion();
    }

    private void getQuestion() {
        list = new ArrayList<>();
        Cursor cursor = helper.getQuestion();
        while (cursor.moveToNext()) {
            FeedbackModel model = new FeedbackModel();
            model.setId(cursor.getInt(0));
            model.setTitle(cursor.getString(1));
            model.setOptionOne(cursor.getString(2));
            model.setOptionTwo(cursor.getString(3));
            list.add(model);
        }
        adapter = new MainRecyclerViewAdapter(this, list);
        adapter.setLongClickListener(this);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onItemLongClick(View view, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete question?");
        builder.setMessage("You can't undo this");
        builder.setCancelable(true);
        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                helper.deleteQuestion(position);
                getQuestion();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onItemClick(View view, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New text");
        builder.setCancelable(true);
        final View customLayout = getLayoutInflater().inflate(R.layout.edit_text_dialog, null);
        builder.setView(customLayout);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editText = customLayout.findViewById(R.id.user_input);
                String newQuestion = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(newQuestion)) {
                    helper.updateQuestion(position, newQuestion);
                } else {
                    Toast.makeText(MainActivity.this, "Can't be empty", Toast.LENGTH_SHORT).show();
                }
                getQuestion();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
